package com.huigod.server;

import com.huigod.manager.DataNodeManager;
import com.huigod.manager.FSNameSystem;
import com.huigod.service.impl.NameNodeServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.extern.log4j.Log4j2;

/**
 * 服务端启动类
 */
@Log4j2
public class NameNodeRpcServer {

  private static final int DEFAULT_PORT = 50070;

  private Server server = null;

  /**
   * 负责管理元数据的核心组件
   */
  private FSNameSystem nameSystem;

  /**
   * 负责管理集群中所有的datanode的组件
   */
  private DataNodeManager datanodeManager;

  public NameNodeRpcServer(
      FSNameSystem nameSystem,
      DataNodeManager datanodeManager) {
    this.nameSystem = nameSystem;
    this.datanodeManager = datanodeManager;
  }

  public void start() throws IOException {
    // 启动一个rpc server，监听指定的端口号
    // 同时绑定好了自己开发的接口
    server = ServerBuilder
        .forPort(DEFAULT_PORT)
        .addService(new NameNodeServiceImpl(nameSystem, datanodeManager))
        .build()
        .start();

    log.info("NameNodeRpcServer启动，监听端口号：{}", DEFAULT_PORT);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          NameNodeRpcServer.this.stop();
        } catch (InterruptedException e) {
          log.error("NameNodeRpcServer stop is error:", e);
        }
      }
    });
  }

  public void stop() throws InterruptedException {
    if (server != null) {
      server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
    }
  }

  public void blockUntilShutdown() throws InterruptedException {
    if (server != null) {
      server.awaitTermination();
    }
  }
}
