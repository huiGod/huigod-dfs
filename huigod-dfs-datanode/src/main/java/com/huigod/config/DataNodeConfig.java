package com.huigod.config;

/**
 * 数据节点配置文件
 */
public class DataNodeConfig {
  public static final String NAMENODE_HOSTNAME = "localhost";
  public static final Integer NAMENODE_PORT = 50070;
  private static final String NIO_HOSTNAME = "localhost3";
  private static final String NIO_IPP = "192.168.31.113";
  private static final Integer NIO_PORT = 9303;
  private static final String DATA_DIR = "D:\\user\\文档\\cl\\dfs\\test\\tmp3";

  public static String getNioHostname() {
    return NIO_HOSTNAME;
  }

  public static String getNioIpp() {
    return NIO_IPP;
  }

  public static Integer getNioPort() {
    return NIO_PORT;
  }

  public static String getDataDir() {
    return DATA_DIR;
  }
}
