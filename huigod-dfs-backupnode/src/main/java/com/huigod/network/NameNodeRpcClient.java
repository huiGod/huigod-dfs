package com.huigod.network;

import com.alibaba.fastjson.JSONArray;
import com.huigod.namenode.rpc.model.FetchEditsLogRequest;
import com.huigod.namenode.rpc.model.FetchEditsLogResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

/**
 * 同NameNode节点发起请求客户端
 */
public class NameNodeRpcClient {

  private static final String NAMENODE_HOSTNAME = "localhost";
  private static final Integer NAMENODE_PORT = 50070;

  private NameNodeServiceGrpc.NameNodeServiceBlockingStub nameNode;

  public NameNodeRpcClient() {
    ManagedChannel channel = NettyChannelBuilder
        .forAddress(NAMENODE_HOSTNAME, NAMENODE_PORT)
        .negotiationType(NegotiationType.PLAINTEXT)
        .build();
    this.nameNode = NameNodeServiceGrpc.newBlockingStub(channel);
  }

  /**
   * 抓取editslog数据
   * @return
   */
  public JSONArray fetchEditsLog() {
    FetchEditsLogRequest request = FetchEditsLogRequest.newBuilder()
        .setCode(1)
        .build();

    FetchEditsLogResponse response = nameNode.fetchEditsLog(request);
    String editsLogJson = response.getEditsLog();

    return JSONArray.parseArray(editsLogJson);
  }

}
