package com.huigod.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 内存元数据持久化文件
 *
 */
@Data
@AllArgsConstructor
public class FSImage {
  private long maxTxid;
  private String fsimageJson;
}
