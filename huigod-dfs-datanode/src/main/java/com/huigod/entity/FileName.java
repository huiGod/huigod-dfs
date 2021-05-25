package com.huigod.entity;

import lombok.Data;

/**
 * 文件名
 */

@Data
public class FileName {

  /**
   * 相对路径名
   */
  private String relativeFilename;

  /**
   * 绝对路径名
   */
  private String absoluteFilename;
}
