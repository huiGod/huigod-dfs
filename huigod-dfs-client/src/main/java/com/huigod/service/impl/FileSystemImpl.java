package com.huigod.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.client.NioClient;
import com.huigod.entity.FileInfo;
import com.huigod.entity.Host;
import com.huigod.namenode.rpc.model.AllocateDataNodesRequest;
import com.huigod.namenode.rpc.model.AllocateDataNodesResponse;
import com.huigod.namenode.rpc.model.ChooseDataNodeFromReplicasRequest;
import com.huigod.namenode.rpc.model.ChooseDataNodeFromReplicasResponse;
import com.huigod.namenode.rpc.model.CreateFileRequest;
import com.huigod.namenode.rpc.model.CreateFileResponse;
import com.huigod.namenode.rpc.model.MkdirRequest;
import com.huigod.namenode.rpc.model.MkdirResponse;
import com.huigod.namenode.rpc.model.ReallocateDataNodeRequest;
import com.huigod.namenode.rpc.model.ReallocateDataNodeResponse;
import com.huigod.namenode.rpc.model.ShutdownRequest;
import com.huigod.namenode.rpc.model.ShutdownResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import com.huigod.service.FileSystem;
import com.huigod.service.ResponseCallback;
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

  private NioClient nioClient;

  private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

  public FileSystemImpl() {
    ManagedChannel channel = NettyChannelBuilder
        .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
        .negotiationType(NegotiationType.PLAINTEXT)
        .build();
    this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    this.nioClient = new NioClient();
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
   */
  @Override
  public Boolean upload(FileInfo fileInfo, ResponseCallback callback) throws Exception {

    //先通过grpc调用NameNode尝试在元数据创建文件，如果已经存在则终止操作
    if (!createFile(fileInfo.getFilename())) {
      return false;
    }

    log.info("在文件目录树中成功创建该文件......");

    //调用NameUpNode来获取负载均衡后的DataNode节点
    JSONArray dataNodes = this.allocateDataNodes(fileInfo.getFilename(), fileInfo.getFileLength());
    log.info("申请分配了2个数据节点：{}", dataNodes);

    //依次将文件上传到dataNode上，需要考虑容错机制
    for (int i = 0; i < dataNodes.size(); i++) {
      Host host = getHost(dataNodes.getJSONObject(i));

      //异步上传
      if (!nioClient.sendFile(fileInfo, host, callback)) {
        //如果失败重新获取节点进行上传
        host = reallocateDataNode(fileInfo, host.getId());
        nioClient.sendFile(fileInfo, host, null);
      }
    }

    return true;
  }

  /**
   * 获取数据节点对应的机器
   *
   * @param datanode
   * @return
   */
  private Host getHost(JSONObject datanode) {
    Host host = new Host();
    host.setHostName(datanode.getString("hostname"));
    host.setIp(datanode.getString("ip"));
    host.setNioPort(datanode.getInteger("nioPort"));
    return host;
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
  private JSONArray allocateDataNodes(String fileName, long fileSize) {
    AllocateDataNodesRequest request = AllocateDataNodesRequest.newBuilder()
        .setFileName(fileName)
        .setFileSize(fileSize)
        .build();

    AllocateDataNodesResponse response = nameNode.allocateDataNodes(request);
    return JSONArray.parseArray(response.getDataNodes());
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
    Host datanode = chooseDataNodeFromReplicas(filename, "");
    log.info("Master分配用来下载文件的数据节点:{}", datanode);

    byte[] file;

    try {
      file = nioClient.readFile(datanode, filename, true);
    } catch (Exception e) {
      //重试其他的dataNode
      datanode = chooseDataNodeFromReplicas(filename, datanode.getId());
      try {
        file = nioClient.readFile(datanode, filename, false);
      } catch (Exception e2) {
        throw e2;
      }
    }
    return file;
  }

  /**
   * 重新分配一个数据节点
   */
  private Host reallocateDataNode(FileInfo fileInfo, String excludedHostId) {
    ReallocateDataNodeRequest request = ReallocateDataNodeRequest.newBuilder()
        .setFileName(fileInfo.getFilename())
        .setFileSize(fileInfo.getFileLength())
        .setExcludedDataNodeId(excludedHostId)
        .build();
    ReallocateDataNodeResponse response = nameNode.reallocateDataNode(request);
    return getHost(JSONObject.parseObject(response.getDatanode()));
  }

  /**
   * 获取文件的某个副本所在的机器
   *
   * @param filename
   * @param excludedDataNodeId
   * @return
   */
  private Host chooseDataNodeFromReplicas(String filename, String excludedDataNodeId) {
    ChooseDataNodeFromReplicasRequest request = ChooseDataNodeFromReplicasRequest.newBuilder()
        .setFilename(filename)
        .setExcludedDataNodeId(excludedDataNodeId)
        .build();
    ChooseDataNodeFromReplicasResponse response = nameNode.chooseDataNodeFromReplicas(request);
    return getHost(JSONObject.parseObject(response.getDatanode()));
  }

  /**
   * 重试上传文件
   */
  @Override
  public Boolean retryUpload(FileInfo fileInfo, Host excludedHost) throws Exception {
    Host host = reallocateDataNode(fileInfo, excludedHost.getId());
    nioClient.sendFile(fileInfo, host, null);
    return true;
  }
}