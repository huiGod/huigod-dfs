package com.huigod.server;

import com.huigod.manager.DataNodeManager;
import com.huigod.manager.FSNameSystem;

/**
 * NameNode核心启动类
 */
public class NameNode {

  /**
   * 负责管理元数据的核心组件：管理的是一些文件目录树，支持权限设置
   */
  private FSNameSystem namesystem;
  /**
   * 负责管理集群中所有的Datanode的组件
   */
  private DataNodeManager datanodeManager;
  /**
   * NameNode对外提供rpc接口的server，可以响应请求
   */
  private NameNodeRpcServer rpcServer;

  /**
   * 初始化NameNode
   */
  private void initialize() throws Exception {
    this.namesystem = new FSNameSystem();
    this.datanodeManager = new DataNodeManager();
    this.rpcServer = new NameNodeRpcServer(this.namesystem, this.datanodeManager);
  }

  /**
   * 让NameNode运行起来
   */
  private void start() throws Exception {
    this.rpcServer.start();
    this.rpcServer.blockUntilShutdown();
  }

  public static void main(String[] args) throws Exception {
    NameNode namenode = new NameNode();
    namenode.initialize();
    namenode.start();
  }
}