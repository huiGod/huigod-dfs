package com.huigod.entity;

import java.nio.ByteBuffer;
import lombok.Data;

/**
 * 缓存请求数据封装
 */
@Data
public class CachedRequest {

  private Integer requestType;
  private FileName filename;
  private Integer filenameLength;
  private Long fileLength;
  private ByteBuffer file;
  private Boolean hasCompletedRead = false;
}
