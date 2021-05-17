package com.huigod.manager;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.network.NameNodeRpcClient;
import com.huigod.server.BackupNode;
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

        if (!nameSystem.isFinishedRecover()) {
          log.info("当前还没完成元数据恢复，不进行editlog同步......");
          Thread.sleep(1000);
          continue;
        }

        //执行完checkpoint后，会更新该txid
        long syncedTxid = nameSystem.getSyncedTxid();
        JSONArray fetchEditsLog = nameNode.fetchEditsLog(syncedTxid);

        if (CollectionUtils.isEmpty(fetchEditsLog)) {
          log.debug("没有拉取到任何一条editslog，等待1秒后继续尝试拉取");
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
          } else if (op.equals("CREATE")) {
            String filename = editsLog.getString("PATH");
            try {
              nameSystem.create(editsLog.getLongValue("txid"), filename);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }

        nameNode.setIsNameNodeRunning(true);
      } catch (Exception e) {
        log.error("fetch thread run is error:", e);
        nameNode.setIsNameNodeRunning(false);
      }
    }
  }
}
