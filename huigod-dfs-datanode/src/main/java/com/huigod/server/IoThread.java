package com.huigod.server;

import com.huigod.entity.NetworkRequest;
import com.huigod.entity.NetworkRequestQueue;
import com.huigod.entity.NetworkResponse;
import com.huigod.entity.NetworkResponseQueues;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责执行磁盘IO的线程
 */
@Slf4j
public class IoThread extends Thread {

  public static final Integer REQUEST_SEND_FILE = 1;
  public static final Integer REQUEST_READ_FILE = 2;

  /**
   * 所有Processor共享的完整请求队列
   */
  private NetworkRequestQueue requestQueue = NetworkRequestQueue.get();
  private NameNodeRpcClient nameNode;

  public IoThread(NameNodeRpcClient nameNode) {
    this.nameNode = nameNode;
  }

  @Override
  public void run() {
    while (true) {
      try {
        NetworkRequest request = requestQueue.poll();

        if (request == null) {
          Thread.sleep(100);
          continue;
        }

        Integer requestType = request.getCachedRequest().getRequestType();

        if (requestType.equals(REQUEST_SEND_FILE)) {
          // 对于上传文件，将文件写入本地磁盘即可
          writeFileToLocalDisk(request);
        } else if (requestType.equals(REQUEST_READ_FILE)) {
          //下载文件，从磁盘读取文件返回
          readFileFromLocalDisk(request);
        }

      } catch (Exception e) {
        log.error("IoThread run is error:", e);
      }
    }
  }

  /**
   * 将数据写入本地磁盘
   *
   * @param request
   */
  private void writeFileToLocalDisk(NetworkRequest request) throws Exception {
    FileOutputStream localFileOut = null;
    FileChannel localFileChannel = null;

    try {
      localFileOut = new FileOutputStream(
          request.getCachedRequest().getFilename().getAbsoluteFilename());
      localFileChannel = localFileOut.getChannel();
      localFileChannel.position(localFileChannel.position());
      log.info("对本地磁盘文件定位到position=" + localFileChannel.size());

      int written = localFileChannel.write(request.getCachedRequest().getFile());
      log.info("本次文件上传完毕，将" + written + " bytes的数据写入本地磁盘文件.......");

      //增量上报Master节点
      nameNode.informReplicaReceived(
          request.getCachedRequest().getFilename().getRelativeFilename() + "_" + request
              .getCachedRequest().getFileLength());
      log.info("增量上报收到的文件副本给NameNode节点......");

      // 封装响应，放入到processor对应的响应队列
      NetworkResponse response = new NetworkResponse();
      response.setClient(request.getClient());
      response.setBuffer(ByteBuffer.wrap("SUCCESS".getBytes()));
      NetworkResponseQueues responseQueues = NetworkResponseQueues.get();
      responseQueues.offer(request.getProcessorId(), response);
    } finally {
      localFileChannel.close();
      localFileOut.close();
    }
  }

  /**
   * 从本地磁盘读取文件返回
   *
   * @param request
   */
  private void readFileFromLocalDisk(NetworkRequest request) throws Exception {
    FileInputStream localFileIn = null;
    FileChannel localFileChannel = null;

    try {
      File file = new File(request.getCachedRequest().getFilename().getAbsoluteFilename());
      Long fileLength = file.length();

      localFileIn = new FileInputStream(
          request.getCachedRequest().getFilename().getAbsoluteFilename());
      localFileChannel = localFileIn.getChannel();

      // 循环不断的从channel里读取数据，并写入磁盘文件
      ByteBuffer buffer = ByteBuffer.allocate(
          8 + Integer.parseInt(String.valueOf(fileLength)));
      buffer.putLong(fileLength);
      int hasReadImageLength = localFileChannel.read(buffer);
      log.info("从本次磁盘文件中读取了" + hasReadImageLength + " bytes的数据");

      buffer.rewind();

      // 封装响应，放入到processor对应的响应队列
      NetworkResponse response = new NetworkResponse();
      response.setClient(request.getClient());
      response.setBuffer(buffer);

      NetworkResponseQueues responseQueues = NetworkResponseQueues.get();
      responseQueues.offer(request.getProcessorId(), response);
    } finally {
      if (localFileChannel != null) {
        localFileChannel.close();
      }
      if (localFileIn != null) {
        localFileIn.close();
      }
    }
  }
}
