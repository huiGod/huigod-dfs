package com.huigod.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.entity.StorageInfo;
import com.huigod.namenode.rpc.model.HeartbeatResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 心跳管理组件
 */
@Slf4j
public class HeartbeatManager {

  public static final Integer REGISTER = 1;
  public static final Integer REPORT_COMPLETE_STORAGE_INFO = 2;

  private NameNodeRpcClient namenodeRpcClient;
  private StorageManager storageManager;

  public HeartbeatManager(NameNodeRpcClient namenodeRpcClient,
      StorageManager storageManager) {
    this.namenodeRpcClient = namenodeRpcClient;
    this.storageManager = storageManager;
  }

  public void start() {
    new HeartbeatThread().start();
  }

  /**
   * 负责心跳的线程
   *
   * @author zhonghuashishan
   */
  class HeartbeatThread extends Thread {

    @Override
    public void run() {
      while (true) {
        try {
          // 通过RPC接口发送心跳到NameNode
          HeartbeatResponse response = namenodeRpcClient.heartbeat();

          // 如果心跳失败了
          if (response.getStatus() == 2) {
            JSONArray commands = JSONArray.parseArray(response.getCommands());

            for (int i = 0; i < commands.size(); i++) {
              JSONObject command = commands.getJSONObject(i);
              Integer type = command.getInteger("type");

              // 如果是注册的命令
              if (type.equals(HeartbeatManager.REGISTER)) {
                namenodeRpcClient.register();
              }
              // 如果是全量上报的命令
              else if (type.equals(HeartbeatManager.REPORT_COMPLETE_STORAGE_INFO)) {
                StorageInfo storageInfo = storageManager.getStorageInfo();
                namenodeRpcClient.reportCompleteStorageInfo(storageInfo);
              }
            }
          }
        } catch (Exception e) {
          log.info("当前NameNode不可用，心跳失败.......");
        }

        try {
          // 每隔30秒发送一次心跳到NameNode上去
          Thread.sleep(30 * 1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }
}
