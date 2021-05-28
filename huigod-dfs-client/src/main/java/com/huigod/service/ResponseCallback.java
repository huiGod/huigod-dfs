package com.huigod.service;

import com.huigod.entity.NetworkResponse;

/**
 * 响应回调函数接口
 */
public interface ResponseCallback {

  /**
   * 处理响应结果
   * @param response
   */
  void process(NetworkResponse response);
}
