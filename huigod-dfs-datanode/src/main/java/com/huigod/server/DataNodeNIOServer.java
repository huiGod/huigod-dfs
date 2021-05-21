package com.huigod.server;

import com.huigod.config.DataNodeConfig;
import com.huigod.entity.CachedRequest;
import com.huigod.entity.Filename;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据节点的NIOServer
 */
@Slf4j
public class DataNodeNIOServer extends Thread {

  public static final Integer SEND_FILE = 1;
  public static final Integer READ_FILE = 2;
  public static final Integer NIO_BUFFER_SIZE = 10 * 1024;

  /**
   * NIO的selector，负责多路复用监听多个连接的请求
   */
  private Selector selector;

  /**
   * 内存队列，无界队列
   */
  private List<LinkedBlockingQueue<SelectionKey>> queues = new ArrayList<>();

  /**
   * 缓存没读取完的文件数据
   */
  private Map<String, CachedRequest> cachedRequests = new ConcurrentHashMap<>();

  /**
   * 缓存没读取完的请求类型
   */
  private Map<String, ByteBuffer> requestTypeByClient = new ConcurrentHashMap<>();

  /**
   * 缓存没读取完的文件名
   */
  private Map<String, ByteBuffer> filenameByClient = new ConcurrentHashMap<>();

  /**
   * 缓存没读取完的文件名大小
   */
  private Map<String, ByteBuffer> filenameLengthByClient = new ConcurrentHashMap<>();

  /**
   * 缓存没读取完的文件大小
   */
  private Map<String, ByteBuffer> fileLengthByClient = new ConcurrentHashMap<>();

  /**
   * 缓存没读取完的文件
   */
  private Map<String, ByteBuffer> fileByClient = new ConcurrentHashMap<>();

  /**
   * 与NameNode进行通信的客户端
   */
  private NameNodeRpcClient nameNodeRpcClient;

  /**
   * NIOServer的初始化，监听端口、队列初始化、线程初始化
   */
  public DataNodeNIOServer(NameNodeRpcClient nameNodeRpcClient) {
    ServerSocketChannel serverSocketChannel;
    this.nameNodeRpcClient = nameNodeRpcClient;

    try {

      selector = Selector.open();

      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.socket().bind(new InetSocketAddress(DataNodeConfig.NIO_PORT), 100);

      for (int i = 0; i < 3; i++) {
        queues.add(new LinkedBlockingQueue<>());
      }

      for (int i = 0; i < 3; i++) {
        new Worker(queues.get(i)).start();
      }

      log.info("NIOServer已经启动，开始监听端口：" + DataNodeConfig.NIO_PORT);

    } catch (Exception e) {
      log.error("DataNodeNIOServer is error:", e);
    }

  }

  @Override
  public void run() {
    //等待IO多路复用方式监听请求
    while (true) {
      try {
        selector.select();
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();
          handleEvents(key);
        }
      } catch (Exception e) {
        log.error("DataNodeNIOServer run is error:", e);
      }
    }
  }

  /**
   * 处理请求分发
   *
   * @param key
   */
  private void handleEvents(SelectionKey key) throws Exception {
    SocketChannel channel = null;

    try {
      if (key.isAcceptable()) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        channel = serverSocketChannel.accept();
        if (channel != null) {
          channel.configureBlocking(false);
          channel.register(selector, SelectionKey.OP_READ);
        }
      } else if (key.isReadable()) {
        channel = (SocketChannel) key.channel();
        String client = channel.getRemoteAddress().toString();
        int queueIndex = client.hashCode() % queues.size();
        queues.get(queueIndex).put(key);
      }
    } catch (Throwable t) {
      log.error("handleEvents is error:", t);
      if (channel != null) {
        channel.close();
      }
    }
  }

  /**
   * 处理请求的工作线程
   */
  class Worker extends Thread {

    private LinkedBlockingQueue<SelectionKey> queue;

    public Worker(LinkedBlockingQueue<SelectionKey> queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      while (true) {
        SocketChannel channel = null;

        try {
          SelectionKey key = queue.take();
          channel = (SocketChannel) key.channel();
          handleRequest(channel, key);
        } catch (Exception e) {
          log.error("worker run is error:", e);
          if (channel != null) {
            try {
              channel.close();
            } catch (Exception e1) {
              log.error("io is error:", e1);
            }
          }
        }
      }
    }
  }

  /**
   * 处理客户端发送过来的请求
   *
   * @param channel
   * @param key
   * @throws Exception
   */
  private void handleRequest(SocketChannel channel, SelectionKey key) throws Exception {
    String client = channel.getRemoteAddress().toString();
    log.info("接收到客户端的请求:{}", client);

    if (cachedRequests.containsKey(client)) {
      log.info("上一次上传文件请求出现拆包问题，本次继续执行文件上传操作......");
      handleSendFileRequest(channel, key);
      return;
    }

    //获取请求类型
    Integer requestType = getRequestType(channel);

    if (requestType == null) {
      return;
    }

    log.info("从请求中解析出来请求类型:{}", requestType);

    //需要处理拆包与粘包操作
    if (SEND_FILE.equals(requestType)) {
      handleSendFileRequest(channel, key);
    } else if (READ_FILE.equals(requestType)) {
      handleReadFileRequest(channel, key);
    }
  }

  /**
   * 客户端文件上传
   *
   * @param channel
   * @param key
   */
  private void handleSendFileRequest(SocketChannel channel, SelectionKey key) throws Exception {

    String client = channel.getRemoteAddress().toString();

    // 从请求中解析文件名
    Filename filename = getFilename(channel);
    log.info("从网络请求中解析出来文件名：" + filename);
    if (filename == null) {
      return;
    }

    // 从请求中解析文件大小
    Long fileLength = getFileLength(channel);
    log.info("从网络请求中解析出来文件大小：" + fileLength);
    if (fileLength == null) {
      return;
    }

    // 获取已经读取的文件大小
    long hasReadImageLength = getHasReadFileLength(channel);
    log.info("初始化已经读取的文件大小：" + hasReadImageLength);

    // 构建针对本地文件的输出流
    FileOutputStream imageOut = null;
    FileChannel imageChannel = null;

    try {
      imageOut = new FileOutputStream(filename.getAbsoluteFilename());
      imageChannel = imageOut.getChannel();
      imageChannel.position(imageChannel.size());
      log.info("对本地磁盘文件定位到position={}", imageChannel.size());

      ByteBuffer fileBuffer;
      if (fileByClient.containsKey(client)) {
        fileBuffer = fileByClient.get(client);
      } else {
        fileBuffer = ByteBuffer.allocate(Integer.parseInt(String.valueOf(fileLength)));
      }

      hasReadImageLength += channel.read(fileBuffer);

      if (!fileBuffer.hasRemaining()) {
        fileBuffer.rewind();
        int written = imageChannel.write(fileBuffer);

        fileByClient.remove(client);
        log.info("本次文件上传完毕，将" + written + " bytes的数据写入本地磁盘文件.......");

        ByteBuffer outBuffer = ByteBuffer.wrap("SUCCESS".getBytes(StandardCharsets.UTF_8));
        channel.write(outBuffer);
        cachedRequests.remove(client);
        log.info("文件读取完毕，返回响应给客户端: " + client);

        nameNodeRpcClient.informReplicaReceived(filename.getRelativeFilename() + "_" + fileLength);
        log.info("增量上报收到的文件副本给NameNode节点......");

        //客户端上传文件，接受完毕后，取消OP_READ事件的关注
        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
      } else {
        fileByClient.put(client, fileBuffer);
        getCachedRequest(client).setHasReadFileLength(hasReadImageLength);
        log.info("本次文件上传出现拆包问题，缓存起来，下次继续读取.......");
        return;
      }
    } finally {
      imageChannel.close();
      imageOut.close();
    }

  }

  /**
   * 读取文件
   *
   * @param channel
   * @param key
   */
  private void handleReadFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
    String client = channel.getRemoteAddress().toString();

    Filename filename = getFilename(channel);
    log.info("从网络请求中解析出来文件名：" + filename);
    if (filename == null) {
      return;
    }

    File file = new File(filename.getAbsoluteFilename());
    Long fileLength = file.length();

    FileInputStream imageIn = new FileInputStream(filename.getAbsoluteFilename());
    FileChannel imageChannel = imageIn.getChannel();

    ByteBuffer buffer = ByteBuffer.allocate(8 + Integer.parseInt(String.valueOf(fileLength)));
    buffer.putLong(fileLength);

    //本地磁盘的IO操作，当然不会有粘包、拆包
    //粘包、拆包只会在TCP网络IO时可能出现！！！
    int hasReadImageLength = imageChannel.read(buffer);
    log.info("从本次磁盘文件中读取了" + hasReadImageLength + " bytes的数据");

    buffer.rewind();

    //将数据写入到客户端SocketChannel，客户端在read时才需要处理粘包与拆包
    int sent = channel.write(buffer);
    log.info("将" + sent + " bytes的数据发送给了客户端.....");
    imageChannel.close();
    imageIn.close();
    //判断一下，如果已经读取完毕，就返回一个成功给客户端
    if (hasReadImageLength == fileLength) {
      log.info("文件发送完毕，给客户端: " + client);
      cachedRequests.remove(client);
      //文件读取完成，不需要再关注OP_READ事件
      key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
    }

  }

  /**
   * 获取文件名同时转换为本地磁盘目录中的绝对路径
   *
   * @param channel
   * @return
   */
  private Filename getFilename(SocketChannel channel) throws Exception {
    Filename filename = new Filename();
    String client = channel.getRemoteAddress().toString();

    if (getCachedRequest(client).getFilename() != null) {
      return getCachedRequest(client).getFilename();
    } else {
      String relativeFilename = getRelativeFilename(channel);
      if (relativeFilename == null) {
        return null;
      }
      // /image/product/iphone.jpg
      filename.setRelativeFilename(relativeFilename);

      String absoluteFilename = getAbsoluteFilename(relativeFilename);
      filename.setAbsoluteFilename(absoluteFilename);

      CachedRequest cachedRequest = getCachedRequest(client);
      cachedRequest.setFilename(filename);
    }

    return filename;
  }

  /**
   * 获取本次请求的类型
   *
   * @param channel
   * @return
   */
  private Integer getRequestType(SocketChannel channel) throws Exception {
    Integer requestType = null;
    String client = channel.getRemoteAddress().toString();

    if (getCachedRequest(client).getRequestType() != null) {
      return getCachedRequest(client).getRequestType();
    }

    ByteBuffer requestTypeBuffer;

    if (requestTypeByClient.containsKey(client)) {
      requestTypeBuffer = requestTypeByClient.get(client);
    } else {
      requestTypeBuffer = ByteBuffer.allocate(4);
    }

    channel.read(requestTypeBuffer);

    if (!requestTypeBuffer.hasRemaining()) {
      requestTypeBuffer.rewind();
      requestType = requestTypeBuffer.getInt();
      log.info("从请求中解析出来本次请求的类型:{}", requestType);

      requestTypeByClient.remove(client);

      //读取完成后，放入本次完整请求的缓存
      CachedRequest cachedRequest = getCachedRequest(client);
      cachedRequest.setRequestType(requestType);

    } else {
      requestTypeByClient.put(client, requestTypeBuffer);
    }
    return requestType;
  }

  /**
   * 获取缓存的请求
   *
   * @param client
   * @return
   */
  private CachedRequest getCachedRequest(String client) {
    CachedRequest cachedRequest = cachedRequests.get(client);
    if (cachedRequest == null) {
      cachedRequests.put(client, new CachedRequest());
    }
    return cachedRequest;
  }

  /**
   * 获取相对路径的文件名
   *
   * @param channel
   * @return
   */
  private String getRelativeFilename(SocketChannel channel) throws Exception {
    String client = channel.getRemoteAddress().toString();

    Integer filenameLength = null;
    String filename = null;

    // 读取文件名的大小
    if (!filenameByClient.containsKey(client)) {
      ByteBuffer filenameLengthBuffer;
      if (filenameLengthByClient.containsKey(client)) {
        filenameLengthBuffer = filenameLengthByClient.get(client);
      } else {
        filenameLengthBuffer = ByteBuffer.allocate(4);
      }

      channel.read(filenameLengthBuffer);

      if (!filenameLengthBuffer.hasRemaining()) {
        filenameLengthBuffer.rewind();
        filenameLength = filenameLengthBuffer.getInt();
        filenameLengthByClient.remove(client);
      } else {
        filenameLengthByClient.put(client, filenameLengthBuffer);
        return null;
      }
    }

    // 读取文件名
    ByteBuffer filenameBuffer;
    if (filenameByClient.containsKey(client)) {
      filenameBuffer = filenameByClient.get(client);
    } else {
      filenameBuffer = ByteBuffer.allocate(filenameLength);
    }

    channel.read(filenameBuffer);

    if (!filenameBuffer.hasRemaining()) {
      filenameBuffer.rewind();
      filename = new String(filenameBuffer.array());
      filenameByClient.remove(client);
    } else {
      filenameByClient.put(client, filenameBuffer);
    }

    return filename;
  }

  /**
   * 获取文件在本地磁盘上的绝对路径名
   *
   * @param relativeFilename
   * @return
   */
  private String getAbsoluteFilename(String relativeFilename) {
    String[] relativeFilenameSplited = relativeFilename.split("/");

    String dirPath = DataNodeConfig.DATA_DIR;
    for (int i = 0; i < relativeFilenameSplited.length - 1; i++) {
      if (i == 0) {
        continue;
      }
      dirPath += "/" + relativeFilenameSplited[i];
    }

    File dir = new File(dirPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    String absoluteFilename = dirPath + "/" +
        relativeFilenameSplited[relativeFilenameSplited.length - 1];
    return absoluteFilename;
  }

  /**
   * 从网络请求中获取文件大小
   *
   * @param channel
   * @return
   * @throws Exception
   */
  private Long getFileLength(SocketChannel channel) throws Exception {
    Long fileLength = null;
    String client = channel.getRemoteAddress().toString();

    if (getCachedRequest(client).getFileLength() != null) {
      return getCachedRequest(client).getFileLength();
    } else {
      ByteBuffer fileLengthBuffer;
      if (fileLengthByClient.get(client) != null) {
        fileLengthBuffer = fileLengthByClient.get(client);
      } else {
        fileLengthBuffer = ByteBuffer.allocate(8);
      }

      channel.read(fileLengthBuffer);
      if (!fileLengthBuffer.hasRemaining()) {
        fileLengthBuffer.rewind();
        fileLength = fileLengthBuffer.getLong();
        fileLengthByClient.remove(client);
        getCachedRequest(client).setFileLength(fileLength);
      } else {
        fileLengthByClient.put(client, fileLengthBuffer);
      }
    }

    return fileLength;
  }

  /**
   * 获取已经读取的文件大小
   *
   * @param channel
   * @return
   * @throws Exception
   */
  private long getHasReadFileLength(SocketChannel channel) throws Exception {
    String client = channel.getRemoteAddress().toString();
    return getCachedRequest(client).getHasReadFileLength() != null ? getCachedRequest(client)
        .getHasReadFileLength() : 0;
  }
}
