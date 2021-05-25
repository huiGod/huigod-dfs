package com.huigod.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 网络响应队列 每个Processor对应一个响应队列
 */
public class NetworkResponseQueues {

  private static volatile NetworkResponseQueues instance = null;

  public static NetworkResponseQueues get() {
    if (instance == null) {
      synchronized (NetworkResponseQueues.class) {
        if (instance == null) {
          instance = new NetworkResponseQueues();
        }
      }
    }
    return instance;
  }

  private Map<Integer, ConcurrentLinkedQueue<NetworkResponse>> responseQueues = new HashMap<>();

  public void initResponseQueue(Integer processorId) {
    ConcurrentLinkedQueue<NetworkResponse> responseQueue = new ConcurrentLinkedQueue<>();
    responseQueues.put(processorId, responseQueue);
  }

  public void offer(Integer processorId, NetworkResponse response) {
    responseQueues.get(processorId).offer(response);
  }

  public NetworkResponse poll(Integer processorId) {
    return responseQueues.get(processorId).poll();
  }

}
