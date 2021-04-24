package com.huigod.server;

import com.huigod.manager.FSNameSystem;

/**
 * 负责同步editslog的进程
 */
public class BackupNode {

  private volatile Boolean isRunning = true;

  private FSNameSystem nameSystem;

  public static void main(String[] args) throws Exception {
    BackupNode backupNode = new BackupNode();
    backupNode.init();
    backupNode.start();
  }

  public void init() {
    this.nameSystem = new FSNameSystem();
  }

  public void start() throws Exception {
    EditsLogFetcher editsLogFetcher = new EditsLogFetcher(this, nameSystem);
    editsLogFetcher.start();
  }

  public void run() throws Exception {
    while(isRunning) {
      Thread.sleep(1000);
    }
  }

  public Boolean isRunning() {
    return isRunning;
  }
}
