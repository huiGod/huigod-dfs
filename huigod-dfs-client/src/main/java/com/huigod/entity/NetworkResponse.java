package com.huigod.entity;

import java.nio.ByteBuffer;
import lombok.Data;

/**
 * 网络请求响应
 *
 * @author 01405970
 * @date 2021/5/26 10:57
 */

@Data
public class NetworkResponse {

  public static final String RESPONSE_SUCCESS = "SUCCESS";

  private String requestId;
  private String hostname;
  private String ip;
  private ByteBuffer lengthBuffer;
  private ByteBuffer buffer;
  private Boolean error;
  private Boolean finished;
}
