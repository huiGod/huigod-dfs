package com.huigod.entity;

import java.nio.ByteBuffer;
import lombok.Data;

/**
 * 网络响应对象封装
 *
 */
@Data
public class NetworkResponse {

  private String client;
  private ByteBuffer buffer;
}
