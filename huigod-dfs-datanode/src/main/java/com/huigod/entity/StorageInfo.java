package com.huigod.entity;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 存储信息
 */
@Data
public class StorageInfo {

  private List<String> filenames = new ArrayList<>();
  private Long storedDataSize = 0L;

  public void addFilename(String filename) {
    this.filenames.add(filename);
  }

  public void addStoredDataSize(Long storedDataSize) {
    this.storedDataSize += storedDataSize;
  }
}
