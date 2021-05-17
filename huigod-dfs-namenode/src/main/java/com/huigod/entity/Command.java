package com.huigod.entity;

import lombok.Data;

/**
 * 下发给DataNode的命令
 *
 */
@Data
public class Command {

  public static final Integer REGISTER = 1;
  public static final Integer REPORT_COMPLETE_STORAGE_INFO = 2;

  private Integer type;
  private String content;

  public Command() {

  }

  public Command(Integer type) {
    this.type = type;
  }
}
