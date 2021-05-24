package com.huigod.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.client.NIOClient;
import com.huigod.namenode.rpc.model.AllocateDataNodesRequest;
import com.huigod.namenode.rpc.model.AllocateDataNodesResponse;
import com.huigod.namenode.rpc.model.ChooseDataNodeFromReplicasRequest;
import com.huigod.namenode.rpc.model.ChooseDataNodeFromReplicasResponse;
import com.huigod.namenode.rpc.model.CreateFileRequest;
import com.huigod.namenode.rpc.model.CreateFileResponse;
import com.huigod.namenode.rpc.model.GetDataNodeForFileRequest;
import com.huigod.namenode.rpc.model.GetDataNodeForFileResponse;
import com.huigod.namenode.rpc.model.MkdirRequest;
import com.huigod.namenode.rpc.model.MkdirResponse;
import com.huigod.namenode.rpc.model.ReallocateDataNodeRequest;
import com.huigod.namenode.rpc.model.ReallocateDataNodeResponse;
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

  private NIOClient nioClient;

  private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

  public FileSystemImpl() {
    ManagedChannel channel = NettyChannelBuilder
        .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
        .negotiationType(NegotiationType.PLAINTEXT)
        .build();
    this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    this.nioClient = new NIOClient();
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

    log.info("在文件目录树中成功创建该文件......");

    //调用NameUpNode来获取负载均衡后的DataNode节点
    String dataNodesJson = this.allocateDataNodes(fileName, fileSize);
    log.info("申请分配了2个数据节点：{}", dataNodesJson);

    //依次将文件上传到dataNode上，需要考虑容错机制
    JSONArray dataNodes = JSONArray.parseArray(dataNodesJson);
    for (int i = 0; i < dataNodes.size(); i++) {
      JSONObject datanode = dataNodes.getJSONObject(i);
      String hostname = datanode.getString("hostname");
      int nioPort = datanode.getIntValue("nioPort");
      String ip = datanode.getString("ip");

      if (!nioClient.sendFile(hostname, nioPort, file, fileName, fileSize)) {
        datanode = JSONObject.parseObject(
            reallocateDataNode(fileName, fileSize, ip + "-" + hostname));
        hostname = datanode.getString("hostname");
        nioPort = datanode.getIntValue("nioPort");
        //如果重试其他节点仍然上传失败则抛错
        if (!nioClient.sendFile(hostname, nioPort, file, fileName, fileSize)) {
          throw new Exception("file upload failed......");
        }
      }
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
   *
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

  /**
   * 下载文件
   *
   * @param filename 文件名
   * @return
   * @throws Exception
   */
  @Override
  public byte[] download(String filename) throws Exception {
    // 调用NameNode获取文件副本所在的DataNode
    JSONObject datanode = getDataNodeForFile(filename);
    log.info("Master分配用来下载文件的数据节点：" + datanode.toJSONString());

    String hostname = datanode.getString("hostname");
    String ip = datanode.getString("ip");
    Integer nioPort = datanode.getInteger("nioPort");

    byte[] file = null;

    try {
      file = nioClient.readFile(hostname, nioPort, filename);
    } catch (Exception e) {
      datanode = chooseDataNodeFromReplicas(filename, ip + "-" + hostname);
      hostname = datanode.getString("hostname");
      nioPort = datanode.getInteger("nioPort");

      try {
        file = nioClient.readFile(hostname, nioPort, filename);
      } catch (Exception e2) {
        throw e2;
      }
    }
    return file;
  }

  /**
   * 获取文件的某个副本所在的机器
   *
   * @param filename
   * @return
   */
  private JSONObject getDataNodeForFile(String filename) {
    GetDataNodeForFileRequest request = GetDataNodeForFileRequest.newBuilder()
        .setFileName(filename)
        .build();
    GetDataNodeForFileResponse response = nameNode.getDataNodeForFile(request);
    return JSONObject.parseObject(response.getDatanodeInfo());
  }

  /**
   * 重新分配一个数据节点
   *
   * @param filename
   * @param fileSize
   * @return
   */
  private String reallocateDataNode(String filename, long fileSize, String excludedDataNodeId) {
    ReallocateDataNodeRequest request = ReallocateDataNodeRequest.newBuilder()
        .setFileSize(fileSize)
        .setExcludedDataNodeId(excludedDataNodeId)
        .build();
    ReallocateDataNodeResponse response = nameNode.reallocateDataNode(request);
    return response.getDatanode();
  }

  /**
   * 获取文件的某个副本所在的机器
   *
   * @param filename
   * @param excludedDataNodeId
   * @return
   */
  private JSONObject chooseDataNodeFromReplicas(String filename, String excludedDataNodeId) {
    ChooseDataNodeFromReplicasRequest request = ChooseDataNodeFromReplicasRequest.newBuilder()
        .setFilename(filename)
        .setExcludedDataNodeId(excludedDataNodeId)
        .build();
    ChooseDataNodeFromReplicasResponse response = nameNode.chooseDataNodeFromReplicas(request);
    return JSONObject.parseObject(response.getDatanode());
  }
}