package com.huigod.entity;

import lombok.Data;

/**
 * 描述DataNode节点信息
 */
@Data
public class DataNodeInfo {

  private String ip;
  private String hostName;
  /**
   * 最近心跳时间
   */
  private long latestHeartbeatTime = System.currentTimeMillis();

  public DataNodeInfo(String ip, String hostname) {
    this.ip = ip;
    this.hostName = hostname;
  }
}
