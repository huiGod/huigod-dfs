package com.huigod.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.manager.FSNameSystem;
import com.huigod.network.NameNodeRpcClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 从nameNode同步editslog的组件
 */
@Slf4j
public class EditsLogFetcher extends Thread {

  public static final Integer BACKUP_NODE_FETCH_SIZE = 10;

  private BackupNode backupNode;
  private NameNodeRpcClient nameNode;
  private FSNameSystem nameSystem;

  public EditsLogFetcher(BackupNode backupNode, FSNameSystem nameSystem) {
    this.backupNode = backupNode;
    this.nameNode = new NameNodeRpcClient();
    this.nameSystem = nameSystem;
  }

  @Override
  public void run() {
    log.info("Editslog抓取线程已经启动......");

    while (backupNode.isRunning()) {
      try {
        JSONArray fetchEditsLog = nameNode.fetchEditsLog();
        if (CollectionUtils.isEmpty(fetchEditsLog)) {
          log.info("EditsLogFetcher fetch no data return then wait......");
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            log.error("EditsLogFetcher sleep is error:", e);
          }
          continue;
        }

        if (fetchEditsLog.size() < BACKUP_NODE_FETCH_SIZE) {
          Thread.sleep(1000);
          log.info("拉取到的edits log不足10条数据，等待1秒后再次继续去拉取");
        }

        for (int i = 0; i < fetchEditsLog.size(); i++) {
          JSONObject editsLog = fetchEditsLog.getJSONObject(i);
          log.info("fetch data:{}", editsLog.toJSONString());

          String op = editsLog.getString("OP");
          if ("MKDIR".equals(op)) {
            String path = editsLog.getString("PATH");
            try {
              nameSystem.mkdir(editsLog.getLongValue("txid"), path);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      } catch (Exception e) {
        log.error("fetch thread run is error:", e);
      }
    }
  }
}
