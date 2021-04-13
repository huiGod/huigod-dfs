package com.huigod.service.impl;

import com.huigod.manager.DataNodeManager;
import com.huigod.manager.FSNameSystem;
import com.huigod.namenode.rpc.model.HeartbeatRequest;
import com.huigod.namenode.rpc.model.HeartbeatResponse;
import com.huigod.namenode.rpc.model.MkdirRequest;
import com.huigod.namenode.rpc.model.MkdirResponse;
import com.huigod.namenode.rpc.model.RegisterRequest;
import com.huigod.namenode.rpc.model.RegisterResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.log4j.Log4j2;

/**
 * NameNode的rpc服务的接口
 */
@Log4j2
public class NameNodeServiceImpl extends NameNodeServiceGrpc.NameNodeServiceImplBase {

  public static final Integer STATUS_SUCCESS = 1;
  public static final Integer STATUS_FAILURE = 2;

  /**
   * 负责管理元数据的核心组件：管理的是一些文件目录树，支持权限设置
   */
  private FSNameSystem nameSystem;
  /**
   * 负责管理集群中所有的Datanode的组件
   */
  private DataNodeManager datanodeManager;

  public NameNodeServiceImpl(FSNameSystem nameSystem, DataNodeManager datanodeManager) {
    this.nameSystem = nameSystem;
    this.datanodeManager = datanodeManager;
  }

  /**
   * datanode进行注册
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
    datanodeManager.register(request.getIp(), request.getHostName());

    RegisterResponse response = RegisterResponse.newBuilder()
        .setStatus(STATUS_SUCCESS)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * datanode进行心跳
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void heartbeat(HeartbeatRequest request,
      StreamObserver<HeartbeatResponse> responseObserver) {
    datanodeManager.heartbeat(request.getIp(), request.getHostName());

    HeartbeatResponse response = HeartbeatResponse.newBuilder()
        .setStatus(STATUS_SUCCESS)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * 创建目录
   */
  @Override
  public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
    try {
      nameSystem.mkdir(request.getPath());

      log.info("创建目录：path {}", request.getPath());

      MkdirResponse response = MkdirResponse.newBuilder()
          .setStatus(STATUS_SUCCESS)
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("mkdir is error:", e);
    }
  }
}
