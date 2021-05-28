package com.huigod.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 节点信息
 */
@Data
@NoArgsConstructor
public class Host {

  private String hostName;
  private String ip;
  private Integer nioPort;

  public Host(String hostName, Integer nioPort) {
    this.hostName = hostName;
    this.nioPort = nioPort;
  }

  public String getId() {
    return ip + "-" + hostName;
  }
}
