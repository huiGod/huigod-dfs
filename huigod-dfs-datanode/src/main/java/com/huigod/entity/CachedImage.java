package com.huigod.entity;

import lombok.Data;

/**
 * 已缓存文件
 */
@Data
public class CachedImage {

  Filename filename;
  long imageLength;
  long hasReadImageLength;

  public CachedImage(Filename filename, long imageLength, long hasReadImageLength) {
    this.filename = filename;
    this.imageLength = imageLength;
    this.hasReadImageLength = hasReadImageLength;
  }
}
