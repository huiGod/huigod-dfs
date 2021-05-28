package com.huigod.entity;

import lombok.Data;

/**
 * 文件信息
 */
@Data
public class FileInfo {

  private String filename;
  private long fileLength;
  private byte[] file;
}
