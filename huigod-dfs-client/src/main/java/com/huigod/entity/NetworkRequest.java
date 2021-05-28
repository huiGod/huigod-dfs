package com.huigod.entity;

import com.huigod.service.ResponseCallback;
import java.nio.ByteBuffer;
import lombok.Data;

/**
 * 网络请求封装
 */
@Data
public class NetworkRequest {

  public static final Integer REQUEST_TYPE = 4;
  public static final Integer FILENAME_LENGTH = 4;
  public static final Integer FILE_LENGTH = 8;
  public static final Integer REQUEST_SEND_FILE = 1;
  public static final Integer REQUEST_READ_FILE = 2;

  private Integer requestType;
  private String id;
  private String hostname;
  private String ip;
  private Integer nioPort;
  private ByteBuffer buffer;
  private Boolean needResponse;
  private long sendTime;
  private ResponseCallback callback;
}
