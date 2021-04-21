package com.huigod.service.impl;

import com.huigod.namenode.rpc.model.MkdirRequest;
import com.huigod.namenode.rpc.model.MkdirResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import com.huigod.service.FileSystem;
import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件系统客户端的实现类
 * @author zhonghuashishan
 *
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

    log.info("创建目录的响应：{}" ,response.getStatus());
  }

}