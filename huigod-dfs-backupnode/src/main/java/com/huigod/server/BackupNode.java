package com.huigod.server;

import com.huigod.manager.EditsLogFetcher;
import com.huigod.manager.FSImageCheckpointer;
import com.huigod.manager.FSNameSystem;
import com.huigod.network.NameNodeRpcClient;

/**
 * 负责同步editslog的进程
 */
public class BackupNode {

  private volatile Boolean isRunning = true;

  private FSNameSystem nameSystem;

  private NameNodeRpcClient nameNode;

  public static void main(String[] args) throws Exception {
    BackupNode backupNode = new BackupNode();
    backupNode.init();
    backupNode.start();
  }

  public void init() {
    this.nameSystem = new FSNameSystem();
    this.nameNode = new NameNodeRpcClient();
  }

  public void start() throws Exception {
    EditsLogFetcher editsLogFetcher = new EditsLogFetcher(this, nameSystem);
    editsLogFetcher.start();

    FSImageCheckpointer fsimageCheckpointer = new FSImageCheckpointer(this, nameSystem, nameNode);
    fsimageCheckpointer.start();
  }

  public void run() throws Exception {
    while (isRunning) {
      Thread.sleep(1000);
    }
  }

  public Boolean isRunning() {
    return isRunning;
  }
}
