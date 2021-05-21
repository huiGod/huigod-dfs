package com.huigod.entity;

import lombok.Data;

/**
 * 副本复制任务
 *
 */
@Data
public class ReplicateTask {

  private String filename;
  private Long fileLength;
  private DataNodeInfo sourceDatanode;
  private DataNodeInfo destDatanode;

  public ReplicateTask(String filename, Long fileLength, DataNodeInfo sourceDatanode, DataNodeInfo destDatanode) {
    this.filename = filename;
    this.fileLength = fileLength;
    this.sourceDatanode = sourceDatanode;
    this.destDatanode = destDatanode;
  }
}
