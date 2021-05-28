package com.huigod.client;

import com.huigod.entity.FileInfo;
import com.huigod.entity.Host;
import com.huigod.entity.NetworkRequest;
import com.huigod.entity.NetworkResponse;
import com.huigod.manager.NetworkManager;
import com.huigod.service.ResponseCallback;
import java.nio.ByteBuffer;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端的一个NIOClient，负责跟数据节点进行网络通信 复用长连接
 */
@Slf4j
public class NioClient {

  private NetworkManager networkManager;

  public NioClient() {
    this.networkManager = new NetworkManager();
  }

  /**
   * 上传文件
   *
   * @param fileInfo
   * @param host
   * @param callback
   * @return
   */
  public Boolean sendFile(FileInfo fileInfo, Host host, ResponseCallback callback) {

    //如果跟DataNode的连接还没有创建，则创建并缓存
    if (!networkManager.maybeConnect(host.getHostName(), host.getNioPort())) {
      return false;
    }

    //构建请求数据
    NetworkRequest request = createSendFileRequest(fileInfo, host, callback);
    networkManager.sendRequest(request);

    return true;
  }

  /**
   * 下载文件
   */
  public byte[] readFile(Host host, String filename, Boolean retry) throws Exception {
    if (!networkManager.maybeConnect(host.getHostName(), host.getNioPort())) {
      if (retry) {
        throw new Exception();
      }
    }

    NetworkRequest request = createReadFileRequest(host, filename, null);
    networkManager.sendRequest(request);

    NetworkResponse response = networkManager.waitResponse(request.getId());

    if (response.getError()) {
      if (retry) {
        throw new Exception();
      }
    }

    return response.getBuffer().array();
  }

  /**
   * 构建一个发送文件的网络请求
   *
   * @param fileInfo
   * @param host
   * @param callback
   * @return
   */
  private NetworkRequest createSendFileRequest(FileInfo fileInfo, Host host,
      ResponseCallback callback) {
    NetworkRequest request = new NetworkRequest();
    ByteBuffer buffer = ByteBuffer.allocate(
        NetworkRequest.REQUEST_TYPE +
            NetworkRequest.FILENAME_LENGTH +
            fileInfo.getFilename().getBytes().length +
            NetworkRequest.FILE_LENGTH +
            (int) fileInfo.getFileLength());

    buffer.putInt(NetworkRequest.REQUEST_SEND_FILE);
    buffer.putInt(fileInfo.getFilename().getBytes().length);
    buffer.put(fileInfo.getFilename().getBytes());
    buffer.putLong(fileInfo.getFileLength());
    buffer.put(fileInfo.getFile());
    buffer.rewind();

    request.setId(UUID.randomUUID().toString());
    request.setHostname(host.getHostName());
    request.setRequestType(NetworkRequest.REQUEST_SEND_FILE);
    request.setIp(host.getIp());
    request.setNioPort(host.getNioPort());
    request.setBuffer(buffer);
    request.setNeedResponse(false);
    request.setCallback(callback);
    return request;
  }

  /**
   * 构建一个发送文件的网络请求
   */
  private NetworkRequest createReadFileRequest(Host host, String filename,
      ResponseCallback callback) {

    NetworkRequest request = new NetworkRequest();

    byte[] filenameBytes = filename.getBytes();

    ByteBuffer buffer = ByteBuffer.allocate(
        NetworkRequest.REQUEST_TYPE +
            NetworkRequest.FILENAME_LENGTH +
            filenameBytes.length);
    buffer.putInt(NetworkRequest.REQUEST_READ_FILE);
    buffer.putInt(filenameBytes.length);
    buffer.put(filenameBytes);
    buffer.rewind();
    request.setId(UUID.randomUUID().toString());
    request.setHostname(host.getHostName());
    request.setRequestType(NetworkRequest.REQUEST_READ_FILE);
    request.setIp(host.getIp());
    request.setNioPort(host.getNioPort());
    request.setBuffer(buffer);
    request.setNeedResponse(true);
    request.setCallback(callback);

    return request;
  }
}
