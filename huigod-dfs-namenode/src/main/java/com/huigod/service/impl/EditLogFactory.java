package com.huigod.service.impl;

/**
 * 生成editlog内容的工厂类
 *
 * @author 01405970
 * @date 2021/5/13 9:52
 */
public class EditLogFactory {

  public static String mkdir(String path) {
    return "{'OP':'MKDIR','PATH':'" + path + "'}";
  }

  public static String create(String path) {
    return "{'OP':'CREATE','PATH':'" + path + "'}";
  }
}
