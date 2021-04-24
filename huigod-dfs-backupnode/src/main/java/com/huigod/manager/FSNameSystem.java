package com.huigod.manager;

/**
 * 负责管理元数据的核心组件
 */
public class FSNameSystem {

  /**
   * 负责管理内存文件目录树的组件
   */
  private FSDirectory directory;

  public FSNameSystem() {
    this.directory = new FSDirectory();
  }

  /**
   * 创建目录
   *
   * @param path 目录路径
   * @return 是否成功
   */
  public Boolean mkdir(String path) throws Exception {
    this.directory.mkdir(path);
    return true;
  }
}
