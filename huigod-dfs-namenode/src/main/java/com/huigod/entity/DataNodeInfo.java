package com.huigod.entity;

import lombok.Data;

/**
 * 描述DataNode节点信息
 */
@Data
public class DataNodeInfo implements Comparable<DataNodeInfo> {

  /**
   * ip地址
   */
  private String ip;
  /**
   * 机器名字
   */
  private String hostname;
  /**
   * NIO端口
   */
  private int nioPort;
  /**
   * 最近一次心跳的时间
   */
  private long latestHeartbeatTime;
  /**
   * 已经存储数据的大小
   */
  private long storedDataSize;

  public void addStoredDataSize(long storedDataSize) {
    this.storedDataSize += storedDataSize;
  }

  public DataNodeInfo(String ip, String hostname, int nioPort) {
    this.ip = ip;
    this.hostname = hostname;
    this.nioPort = nioPort;
    this.latestHeartbeatTime = System.currentTimeMillis();
    this.storedDataSize = 0L;
  }


  @Override
  public int compareTo(DataNodeInfo o) {
    if(this.storedDataSize - o.getStoredDataSize() > 0) {
      return 1;
    } else if(this.storedDataSize - o.getStoredDataSize() < 0) {
      return -1;
    } else {
      return 0;
    }
  }
}
