package com.huigod.server;

import com.alibaba.fastjson.JSONArray;
import com.huigod.config.DataNodeConfig;
import com.huigod.entity.StorageInfo;
import com.huigod.namenode.rpc.model.HeartbeatRequest;
import com.huigod.namenode.rpc.model.HeartbeatResponse;
import com.huigod.namenode.rpc.model.InformReplicaReceivedRequest;
import com.huigod.namenode.rpc.model.RegisterRequest;
import com.huigod.namenode.rpc.model.RegisterResponse;
import com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.log4j.Log4j2;

/**
 * 负责跟一组NameNode中的某一个进行通信的线程组件
 */
@Log4j2
public class NameNodeRpcClient {

  private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

  public NameNodeRpcClient() {
    ManagedChannel channel = NettyChannelBuilder
        .forAddress(DataNodeConfig.NAMENODE_HOSTNAME, DataNodeConfig.NAMENODE_PORT)
        .negotiationType(NegotiationType.PLAINTEXT)
        .build();
    this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
    log.info("跟NameNode的" + DataNodeConfig.NAMENODE_PORT + "端口建立连接......");
  }

  /**
   * 向自己负责通信的那个NameNode进行注册
   */
  public Boolean register() throws Exception {
    RegisterRequest request = RegisterRequest.newBuilder()
        .setIp(DataNodeConfig.getNioIpp())
        .setHostName(DataNodeConfig.getNioHostname())
        .setNioPort(DataNodeConfig.getNioPort())
        .build();
    RegisterResponse response = nameNode.register(request);

    log.info("完成向NameNode的注册，响应消息为：" + response.getStatus());

    return response.getStatus() == 1;
  }

  /**
   * 发送心跳
   */
  public HeartbeatResponse heartbeat() {
    //log.info("datanode发送心跳，ip:{},hostname:{},port:{}", DataNodeConfig.getNioIpp(),
    //    DataNodeConfig.getNioHostname(), DataNodeConfig.getNioPort());
    HeartbeatRequest request = HeartbeatRequest.newBuilder()
        .setIp(DataNodeConfig.getNioIpp())
        .setHostName(DataNodeConfig.getNioHostname())
        .setNioPort(DataNodeConfig.getNioPort())
        .build();
    return nameNode.heartbeat(request);
  }

  /**
   * 上报全量存储信息
   *
   * @param storageInfo
   */
  public void reportCompleteStorageInfo(StorageInfo storageInfo) {
    if (storageInfo == null) {
      log.info("当前没有存储任何文件，不需要全量上报.....");
      return;
    }

    ReportCompleteStorageInfoRequest request = ReportCompleteStorageInfoRequest.newBuilder()
        .setIp(DataNodeConfig.getNioIpp())
        .setHostName(DataNodeConfig.getNioHostname())
        .setFilenames(JSONArray.toJSONString(storageInfo.getFilenames()))
        .setStoredDataSize(storageInfo.getStoredDataSize())
        .build();

    nameNode.reportCompleteStorageInfo(request);

    log.info("全量上报存储信息：" + storageInfo);
  }

  /**
   * 通知Master节点自己收到了一个文件的副本
   *
   * @param filename
   * @throws Exception
   */
  public void informReplicaReceived(String filename) throws Exception {
    InformReplicaReceivedRequest request = InformReplicaReceivedRequest.newBuilder()
        .setHostname(DataNodeConfig.getNioHostname())
        .setIp(DataNodeConfig.getNioIpp())
        .setFileName(filename)
        .build();
    nameNode.informReplicaReceived(request);
  }
}
