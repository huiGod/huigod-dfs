package com.huigod.service;

/**
 * 作为文件系统的接口
 */
public interface FileSystem {

  /**
   * 创建目录
   *
   * @param path 目录对应的路径
   * @throws Exception
   */
  void mkdir(String path) throws Exception;

  /**
   * 优雅关闭
   *
   * @throws Exception
   */
  void shutdown() throws Exception;

  /**
   * 上传文件
   *
   * @param file     文件的字节数组
   * @param filename 文件名
   * @throws Exception
   */
  Boolean upload(byte[] file, String filename, long fileSize) throws Exception;

  /**
   * 下载文件
   * @param filename 文件名
   * @return 文件的字节数组
   * @throws Exception
   */
  byte[] download(String filename) throws Exception;
}
