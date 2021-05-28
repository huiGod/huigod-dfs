package com.huigod.entity;

import com.huigod.config.DataNodeConfig;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO
 *
 * @author 01405970
 * @date 2021/5/25 10:17
 */

@Data
@Slf4j
public class NetworkRequest {

  public static final Integer REQUEST_SEND_FILE = 1;
  public static final Integer REQUEST_READ_FILE = 2;

  /**
   * processor标识
   */
  private Integer processorId;
  /**
   * 该请求是哪个客户端 发送过来的
   */
  private String client;
  /**
   * 本次网络请求对应的连接
   */
  private SelectionKey key;
  /**
   * 本次网络请求对应的连接
   */
  private SocketChannel channel;

  /**
   * 缓存中的数据
   */
  private CachedRequest cachedRequest = new CachedRequest();
  private ByteBuffer cachedRequestTypeBuffer;
  private ByteBuffer cachedFilenameLengthBuffer;
  private ByteBuffer cachedFilenameBuffer;
  private ByteBuffer cachedFileLengthBuffer;
  private ByteBuffer cachedFileBuffer;

  /**
   * 从网络连接中读取与解析出来一个请求
   */
  public void read() throws Exception {
    Integer requestType;
    if (cachedRequest.getRequestType() != null) {
      requestType = cachedRequest.getRequestType();
    } else {
      requestType = getRequestType(channel);
    }
    if (requestType == null) {
      return;
    }

    log.info("从请求中解析出来请求类型:{}", requestType);

    if (REQUEST_SEND_FILE.equals(requestType)) {
      handleSendFileRequest(channel, key);
    } else if (REQUEST_READ_FILE.equals(requestType)) {
      handleReadFileRequest(channel, key);
    }
  }

  /**
   * 获取本次请求的类型
   */
  public Integer getRequestType(SocketChannel channel) throws Exception {
    Integer requestType = null;

    if (cachedRequest.getRequestType() != null) {
      return cachedRequest.getRequestType();
    }

    ByteBuffer requestTypeBuffer;
    if (cachedRequestTypeBuffer != null) {
      requestTypeBuffer = cachedRequestTypeBuffer;
    } else {
      requestTypeBuffer = ByteBuffer.allocate(4);
    }

    channel.read(requestTypeBuffer);

    if (!requestTypeBuffer.hasRemaining()) {
      requestTypeBuffer.rewind();
      requestType = requestTypeBuffer.getInt();
      cachedRequest.setRequestType(requestType);
    } else {
      cachedRequestTypeBuffer = requestTypeBuffer;
    }
    return requestType;
  }

  /**
   * 客户端上传文件
   *
   * @param channel
   * @param key
   */
  private void handleSendFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
    // 从请求中解析文件名
    FileName filename = getFilename(channel);
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

    // 循环不断的从channel里读取数据，并写入磁盘文件
    ByteBuffer fileBuffer;
    if (cachedFileBuffer != null) {
      fileBuffer = cachedFileBuffer;
    } else {
      fileBuffer = ByteBuffer.allocate(Integer.parseInt(String.valueOf(fileLength)));
    }

    channel.read(fileBuffer);

    if (!fileBuffer.hasRemaining()) {
      fileBuffer.rewind();
      cachedRequest.setFile(fileBuffer);
      cachedRequest.setHasCompletedRead(true);
      log.info("本次文件上传请求读取完毕.......");
    } else {
      cachedFileBuffer = fileBuffer;
      log.info("本次文件上传出现拆包问题，缓存起来，下次继续读取.......");
      return;
    }
  }

  /**
   * 获取文件名同时转换为本地磁盘目录中的绝对路径
   *
   * @param channel
   * @return
   * @throws Exception
   */
  private FileName getFilename(SocketChannel channel) throws Exception {
    FileName filename = new FileName();

    if (cachedRequest.getFilename() != null) {
      return cachedRequest.getFilename();
    } else {
      String relativeFilename = getRelativeFilename(channel);
      if (relativeFilename == null) {
        return null;
      }

      String absoluteFilename = getAbsoluteFilename(relativeFilename);
      // /image/product/iphone.jpg
      filename.setRelativeFilename(relativeFilename);
      filename.setAbsoluteFilename(absoluteFilename);

      cachedRequest.setFilename(filename);
    }

    return filename;
  }

  /**
   * 获取相对路径的文件名
   *
   * @param channel
   * @return
   */
  private String getRelativeFilename(SocketChannel channel) throws Exception {
    Integer filenameLength = null;
    String filename = null;

    // 读取文件名的大小
    if (cachedRequest.getFilenameLength() == null) {
      ByteBuffer filenameLengthBuffer = null;
      if (cachedFilenameLengthBuffer != null) {
        filenameLengthBuffer = cachedFilenameLengthBuffer;
      } else {
        filenameLengthBuffer = ByteBuffer.allocate(4);
      }

      channel.read(filenameLengthBuffer);

      if (!filenameLengthBuffer.hasRemaining()) {
        filenameLengthBuffer.rewind();
        filenameLength = filenameLengthBuffer.getInt();
        cachedRequest.setFilenameLength(filenameLength);
      } else {
        cachedFilenameLengthBuffer = filenameLengthBuffer;
        return null;
      }
    }

    // 读取文件名
    ByteBuffer filenameBuffer = null;
    if (cachedFilenameBuffer != null) {
      filenameBuffer = cachedFilenameBuffer;
    } else {
      filenameBuffer = ByteBuffer.allocate(filenameLength);
    }

    channel.read(filenameBuffer);

    if (!filenameBuffer.hasRemaining()) {
      filenameBuffer.rewind();
      filename = new String(filenameBuffer.array());
    } else {
      cachedFilenameBuffer = filenameBuffer;
    }

    return filename;
  }

  /**
   * 获取文件在本地磁盘上的绝对路径名
   *
   * @param relativeFilename
   * @return
   * @throws Exception
   */
  private String getAbsoluteFilename(String relativeFilename) throws Exception {
    String[] relativeFilenameSplited = relativeFilename.split("/");

    String dirPath = DataNodeConfig.getDataDir();
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

    if (cachedRequest.getFileLength() != null) {
      return cachedRequest.getFileLength();
    } else {
      ByteBuffer fileLengthBuffer;
      if (cachedFileLengthBuffer != null) {
        fileLengthBuffer = cachedFileLengthBuffer;
      } else {
        fileLengthBuffer = ByteBuffer.allocate(8);
      }

      channel.read(fileLengthBuffer);

      if (!fileLengthBuffer.hasRemaining()) {
        fileLengthBuffer.rewind();
        fileLength = fileLengthBuffer.getLong();
        cachedRequest.setFileLength(fileLength);
      } else {
        cachedFileLengthBuffer = fileLengthBuffer;
      }
    }

    return fileLength;
  }

  /**
   * 读取文件
   *
   * @param channel
   */
  private void handleReadFileRequest(SocketChannel channel, SelectionKey key) throws Exception {
    // 从请求中解析文件名
    // 已经是：F:\\development\\tmp1\\image\\product\\iphone.jpg
    FileName filename = getFilename(channel);
    log.info("从网络请求中解析出来文件名：" + filename);
    if (filename == null) {
      return;
    }
    cachedRequest.setHasCompletedRead(true);
  }

  /**
   * 是否已经完成了一个请求的读取
   *
   * @return
   */
  public Boolean hasCompletedRead() {
    return cachedRequest.getHasCompletedRead();
  }
}
