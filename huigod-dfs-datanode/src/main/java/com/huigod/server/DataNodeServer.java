package com.huigod.server;

import com.huigod.entity.StorageInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * DataNode启动类
 */
@Slf4j
public class DataNodeServer {

  /**
   * 是否还在运行
   */
  private volatile Boolean shouldRun;
  /**
   * 负责跟一组NameNode通信的组件
   */
  private NameNodeRpcClient nameNodeRpcClient;
  /**
   * 心跳管理组件
   */
  private HeartbeatManager heartbeatManager;
  /**
   * 磁盘存储管理组件
   */
  private StorageManager storageManager;

  /**
   * 复制任务管理组件
   */
  private ReplicateManager replicateManager;

  /**
   * 初始化DataNode
   */
  public DataNodeServer() throws Exception {
    this.shouldRun = true;
    this.nameNodeRpcClient = new NameNodeRpcClient();
    this.storageManager = new StorageManager();

    Boolean registerResult = this.nameNodeRpcClient.register();

    // 如果是首次注册成功，需要执行全量的副本元数据上报
    if (registerResult) {
      StorageInfo storageInfo = this.storageManager.getStorageInfo();
      this.nameNodeRpcClient.reportCompleteStorageInfo(storageInfo);
    } else {
      log.info("不需要全量上报存储信息......");
    }

    this.replicateManager = new ReplicateManager(this.nameNodeRpcClient);

    //心跳机制，可以接收NameNode发送的命令执行对应逻辑
    this.heartbeatManager = new HeartbeatManager(
        this.nameNodeRpcClient, this.storageManager, this.replicateManager);
    this.heartbeatManager.start();

    //接收客户端连接处理
    DataNodeNIOServer nioServer = new DataNodeNIOServer(nameNodeRpcClient);
    nioServer.start();

  }

  /**
   * 运行DataNode
   */
  private void start() {
    try {
      while (shouldRun) {
        Thread.sleep(1000);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws Exception {
    DataNodeServer datanode = new DataNodeServer();
    datanode.start();
  }
}
