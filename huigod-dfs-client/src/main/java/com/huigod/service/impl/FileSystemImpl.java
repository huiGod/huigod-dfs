package com.huigod.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.client.NIOClient;
import com.huigod.namenode.rpc.model.AllocateDataNodesRequest;
import com.huigod.namenode.rpc.model.AllocateDataNodesResponse;
import com.huigod.namenode.rpc.model.CreateFileRequest;
import com.huigod.namenode.rpc.model.CreateFileResponse;
import com.huigod.namenode.rpc.model.MkdirRequest;
import com.huigod.namenode.rpc.model.MkdirResponse;
import com.huigod.namenode.rpc.model.ShutdownRequest;
import com.huigod.namenode.rpc.model.ShutdownResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import com.huigod.service.FileSystem;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件系统客户端的实现类
 *
 * @author zhonghuashishan
 */
@Slf4j
public class FileSystemImpl implements FileSystem {

  private static final String NAMENODE_HOSTNAME = "localhost";
  private static final Integer NAMENODE_PORT = 50070;

  private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

  public FileSystemImpl() {
    ManagedChannel channel = NettyChannelBuilder
        .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
        .negotiationType(NegotiationType.PLAINTEXT)
        .build();
    this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
  }

  /**
   * 创建目录
   */
  @Override
  public void mkdir(String path) throws Exception {
    MkdirRequest request = MkdirRequest.newBuilder()
        .setPath(path)
        .build();

    MkdirResponse response = nameNode.mkdir(request);

    log.info("创建目录的响应：{}", response.getStatus());
  }

  /**
   * 优雅关闭
   *
   * @throws Exception
   */
  @Override
  public void shutdown() throws Exception {
    ShutdownRequest request = ShutdownRequest.newBuilder()
        .setCode(1)
        .build();
    ShutdownResponse shutdownResponse = nameNode.shutdown(request);
    log.info("shutdown response:{}", shutdownResponse);
  }

  /**
   * 上传文件
   *
   * @param file     文件的字节数组
   * @param fileName 文件名
   * @throws Exception
   */
  @Override
  public Boolean upload(byte[] file, String fileName, long fileSize) throws Exception {

    //先通过grpc调用NameNode尝试在元数据创建文件，如果已经存在则终止操作
    if (!createFile(fileName)) {
      return false;
    }

    //调用NameUpNode来获取负载均衡后的DataNode节点
    String dataNodesJson = this.allocateDataNodes(fileName, fileSize);
    log.info("upload file allocate DataNodes:{}", dataNodesJson);

    //依次将文件上传到dataNode上，需要考虑容错机制
    JSONArray dataNodes = JSONArray.parseArray(dataNodesJson);
    for(int i = 0; i < dataNodes.size(); i++) {
      JSONObject datanode = dataNodes.getJSONObject(i);
      String hostname = datanode.getString("hostname");
      int nioPort = datanode.getIntValue("nioPort");
      NIOClient.sendFile(hostname, nioPort, file, fileName, fileSize);
    }

    return true;
  }

  /**
   * 调用nameNode节点创建文件元数据
   *
   * @param filename
   * @return
   */
  private Boolean createFile(String filename) {
    CreateFileRequest request = CreateFileRequest.newBuilder()
        .setFilename(filename)
        .build();
    CreateFileResponse response = nameNode.create(request);

    return response.getStatus() == 1;
  }

  /**
   * 分配双副本对应的数据节点
   * @param fileName
   * @param fileSize
   * @return
   */
  private String allocateDataNodes(String fileName, long fileSize) {
    AllocateDataNodesRequest request = AllocateDataNodesRequest.newBuilder()
        .setFileName(fileName)
        .setFileSize(fileSize)
        .build();

    AllocateDataNodesResponse response = nameNode.allocateDataNodes(request);
    return response.getDataNodes();
  }
}