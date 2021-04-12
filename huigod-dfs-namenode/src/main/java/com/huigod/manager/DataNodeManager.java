package com.huigod.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;
import com.huigod.entity.DataNodeInfo;

/**
 * 管理集群中所有datanode节点
 */
@Log4j2
public class DataNodeManager {

  /**
   * 集群所有datanode节点
   */
  private Map<String, DataNodeInfo> dataNodes = new ConcurrentHashMap<>();

  /**
   * 启动心跳检测线程
   */
  public DataNodeManager() {
    new DataNodeAliveMonitor().start();
  }

  /**
   * datanode进行注册
   *
   * @param ip
   * @param hostName
   * @return
   */
  public Boolean register(String ip, String hostName) {
    DataNodeInfo datanode = new DataNodeInfo(ip, hostName);
    dataNodes.put(ip + "-" + hostName, datanode);

    log.info("DataNode注册：ip={},hostName={}", ip, hostName);
    return true;
  }

  /**
   * 接受心跳
   *
   * @param ip
   * @param hostName
   * @return
   */
  public Boolean heartbeat(String ip, String hostName) {
    DataNodeInfo datanode = dataNodes.get(ip + "-" + hostName);
    if (datanode == null) {
      log.error("datanode not exists");
      return false;
    }
    datanode.setLatestHeartbeatTime(System.currentTimeMillis());
    log.info("DataNode发送心跳：ip={},hostName={}", ip, hostName);
    return true;
  }

  /**
   * dataNode是否存活的监控线程
   */
  class DataNodeAliveMonitor extends Thread {

    @Override
    public void run() {
      try {
        while (true) {
          List<String> toRemoveDataNodes = new ArrayList<>();

          if (!MapUtils.isEmpty(dataNodes)) {
            Iterator<DataNodeInfo> dataNodesIterator = dataNodes.values().iterator();
            DataNodeInfo datanode = null;
            while (dataNodesIterator.hasNext()) {
              datanode = dataNodesIterator.next();
              //超过90S没有更新心跳，判定为宕机并移除
              if (System.currentTimeMillis() - datanode.getLatestHeartbeatTime() > 90 * 1000) {
                toRemoveDataNodes.add(datanode.getIp() + "-" + datanode.getHostName());
              }
            }

            if (!toRemoveDataNodes.isEmpty()) {
              for (String toRemoveDatanode : toRemoveDataNodes) {
                dataNodes.remove(toRemoveDatanode);
              }
            }
          }
          Thread.sleep(30 * 1000);
        }
      } catch (Exception e) {
        log.error("DataNodeAliveMonitor is error:", e);
      }
    }

  }
}
