package com.huigod.entity;

import lombok.Data;

/**
 * 删除副本任务
 */
@Data
public class RemoveReplicaTask {

  private String filename;
  private DataNodeInfo datanode;

  public RemoveReplicaTask(String filename, DataNodeInfo datanode) {
    this.filename = filename;
    this.datanode = datanode;
  }
}
