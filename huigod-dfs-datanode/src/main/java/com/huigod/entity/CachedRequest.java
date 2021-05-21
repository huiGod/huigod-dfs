package com.huigod.entity;

import lombok.Data;

/**
 * 已缓存文件
 */
@Data
public class CachedRequest {

  Integer requestType;
  Filename filename;
  Long fileLength;
  Long hasReadFileLength;
}
