package com.huigod.server;

import com.huigod.config.DataNodeConfig;
import com.huigod.entity.CachedImage;
import com.huigod.entity.Filename;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据节点的NIOServer
 */
@Slf4j
public class DataNodeNIOServer extends Thread {

  /**
   * NIO的selector，负责多路复用监听多个连接的请求
   */
  private Selector selector;

  /**
   * 内存队列，无界队列
   */
  private List<LinkedBlockingQueue<SelectionKey>> queues =
      new ArrayList<>();

  /**
   * 缓存的没读取完的文件数据
   */
  private Map<String, CachedImage> cachedImages = new HashMap<>();

  /**
   * 与NameNode进行通信的客户端
   */
  private NameNodeRpcClient nameNodeRpcClient;

  /**
   * NIOServer的初始化，监听端口、队列初始化、线程初始化
   */
  public DataNodeNIOServer(NameNodeRpcClient nameNodeRpcClient) {
    ServerSocketChannel serverSocketChannel = null;

    try {
      this.nameNodeRpcClient = nameNodeRpcClient;

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
          SelectionKey selectionKey = keyIterator.next();
          keyIterator.remove();
          handleRequest(selectionKey);
        }
      } catch (Exception e) {
        log.error("DataNodeNIOServer run is error:", e);
      }
    }
  }

  /**
   * 处理请求分发
   */
  private void handleRequest(SelectionKey selectionKey) throws Exception {
    SocketChannel channel = null;

    try {
      //客户端创建连接
      if (selectionKey.isAcceptable()) {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        channel = serverSocketChannel.accept();

        //设置客户端socket为非阻塞，并且注册到Selector上
        if (channel != null) {
          channel.configureBlocking(false);
          //客户端创建好连接后，会发送文件数据，因此关注OP_READ事件即可
          channel.register(selector, SelectionKey.OP_READ);
        }
      } else if (selectionKey.isReadable()) {
        //如果是可读时间，路由到固定的队列，并且将请求入队
        channel = (SocketChannel) selectionKey.channel();
        String remoteAddr = channel.getRemoteAddress().toString();
        int queueIndex = remoteAddr.hashCode() % queues.size();
        queues.get(queueIndex).put(selectionKey);
      }
    } catch (Exception e) {
      log.error("handleRequest is error:", e);
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
          if (!channel.isOpen()) {
            channel.close();
            continue;
          }

          String remoteAddr = channel.getRemoteAddress().toString();
          log.info("接收到客户端的请求：" + remoteAddr);

          ByteBuffer buffer = ByteBuffer.allocate(10 * 1024);
          // 从请求中解析文件名
          // F:\\development\\tmp1\\image\\product\\iphone.jpg
          Filename filename = getFilename(channel, buffer);

          log.info("从网络请求中解析出来文件名：" + filename);
          if (filename == null) {
            channel.close();
            continue;
          }

          // 从请求中解析文件大小
          long imageLength = getImageLength(channel, buffer);
          log.info("从网络请求中解析出来文件大小：" + imageLength);

          // 定义已经读取的文件大小
          long hasReadImageLength = getHasReadImageLength(channel);
          log.info("初始化已经读取的文件大小：" + hasReadImageLength);

          // 构建针对本地文件的输出流
          FileOutputStream imageOut = new FileOutputStream(filename.getAbsoluteFilename());
          FileChannel imageChannel = imageOut.getChannel();
          //将position设置到文件已有的字节数位置
          imageChannel.position(imageChannel.size());

          // 如果是第一次接收到请求，就应该把buffer里剩余的数据写入到文件里去
          if (!cachedImages.containsKey(remoteAddr)) {
            hasReadImageLength += imageChannel.write(buffer);
            log.info("已经向本地磁盘文件写入了" + hasReadImageLength + "字节的数据");
            buffer.clear();
          }

          // 循环不断的从channel里读取数据，并写入磁盘文件
          int len = -1;
          while ((len = channel.read(buffer)) > 0) {
            hasReadImageLength += len;
            log.info("已经向本地磁盘文件写入了" + hasReadImageLength + "字节的数据");
            buffer.flip();
            imageChannel.write(buffer);
            buffer.clear();
          }
          imageChannel.close();
          imageOut.close();

          // 判断一下，如果已经读取完毕，就返回一个成功给客户端
          if (hasReadImageLength == imageLength) {
            ByteBuffer outBuffer = ByteBuffer.wrap("SUCCESS".getBytes());
            channel.write(outBuffer);
            cachedImages.remove(remoteAddr);
            log.info("文件读取完毕，返回响应给客户端: " + remoteAddr);

            // 增量上报Master节点自己接收到了一个文件的副本
            // /image/product/iphone.jpg
            nameNodeRpcClient.informReplicaReceived(filename.getRelativeFilename());
            log.info("增量上报收到的文件副本给NameNode节点......");
          } else {
            // 如果一个文件没有读完，缓存起来，等待下一次读取
            CachedImage cachedImage = new CachedImage(filename, imageLength, hasReadImageLength);
            cachedImages.put(remoteAddr, cachedImage);
            key.interestOps(SelectionKey.OP_READ);
            log.info("文件没有读取完毕，等待下一次OP_READ请求，缓存文件：" + cachedImage);
          }

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

    /**
     * 从网络请求中获取文件名
     *
     * @param channel
     * @param buffer
     * @return
     */
    private Filename getFilename(SocketChannel channel, ByteBuffer buffer) throws Exception {
      Filename filename = new Filename();
      String remoteAddr = channel.getRemoteAddress().toString();

      if (cachedImages.containsKey(remoteAddr)) {
        filename = cachedImages.get(remoteAddr).getFilename();
      } else {
        String relativeFilename = getRelativeFilename(channel, buffer);
        if (relativeFilename == null) {
          return null;
        }

        filename.setRelativeFilename(relativeFilename);

        String[] relativeFilenameSplited = relativeFilename.split("/");
        String dirPath = DataNodeConfig.DATA_DIR;
        for (int i = 0; i < relativeFilenameSplited.length - 1; i++) {
          if (i == 0) {
            continue;
          }
          dirPath += "\\" + relativeFilenameSplited[i];
        }

        File dir = new File(dirPath);
        if (!dir.exists()) {
          dir.mkdirs();
        }

        String absoluteFilename = dirPath + "\\" +
            relativeFilenameSplited[relativeFilenameSplited.length - 1];
        filename.setAbsoluteFilename(absoluteFilename);
      }

      return filename;
    }

    /**
     * 从网络请求中获取相对文件名
     *
     * @param channel
     * @param buffer
     * @return
     */
    private String getRelativeFilename(SocketChannel channel, ByteBuffer buffer) throws Exception {
      int len = channel.read(buffer);
      if (len > 0) {
        buffer.flip();

        byte[] filenameLengthBytes = new byte[4];
        buffer.get(filenameLengthBytes, 0, 4);

        ByteBuffer filenameLengthBuffer = ByteBuffer.allocate(4);
        filenameLengthBuffer.put(filenameLengthBytes);
        filenameLengthBuffer.flip();
        int filenameLength = filenameLengthBuffer.getInt();

        byte[] filenameBytes = new byte[filenameLength];
        buffer.get(filenameBytes, 0, filenameLength);
        // 这里返回的应该就是：/image/product/iphone.jpg
        String filename = new String(filenameBytes);

        return filename;
      }

      return null;
    }
  }

  /**
   * 从网络请求中获取文件大小
   *
   * @param channel
   * @param buffer
   * @return
   */
  private long getImageLength(SocketChannel channel, ByteBuffer buffer) throws Exception {
    Long imageLength = 0L;
    String remoteAddr = channel.getRemoteAddress().toString();

    if (cachedImages.containsKey(remoteAddr)) {
      imageLength = cachedImages.get(remoteAddr).getImageLength();
    } else {
      byte[] imageLengthBytes = new byte[8];
      buffer.get(imageLengthBytes, 0, 8);

      ByteBuffer imageLengthBuffer = ByteBuffer.allocate(8);
      imageLengthBuffer.put(imageLengthBytes);
      imageLengthBuffer.flip();
      imageLength = imageLengthBuffer.getLong();
    }

    return imageLength;
  }

  /**
   * 获取已经读取的文件大小
   *
   * @param channel
   * @return
   */
  private long getHasReadImageLength(SocketChannel channel) throws Exception {
    long hasReadImageLength = 0;
    String remoteAddr = channel.getRemoteAddress().toString();
    if (cachedImages.containsKey(remoteAddr)) {
      hasReadImageLength = cachedImages.get(remoteAddr).getHasReadImageLength();
    }
    return hasReadImageLength;
  }

}
