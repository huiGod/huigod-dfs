package com.huigod.manager;

import com.huigod.entity.DataNodeInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.MapUtils;

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
  public Boolean register(String ip, String hostName, int nioPort) {
    if (dataNodes.containsKey(ip + "-" + hostName)) {
      log.info("注册失败，当前已经存在这个DataNode了......");
      return false;
    }

    DataNodeInfo datanode = new DataNodeInfo(ip, hostName, nioPort);
    dataNodes.put(ip + "-" + hostName, datanode);
    log.info("DataNode注册：ip=" + ip + ",hostname=" + hostName + ", nioPort=" + nioPort);

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
      log.error("心跳失败，需要重新注册.......");
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
            DataNodeInfo datanode;
            while (dataNodesIterator.hasNext()) {
              datanode = dataNodesIterator.next();
              //超过90S没有更新心跳，判定为宕机并移除
              if (System.currentTimeMillis() - datanode.getLatestHeartbeatTime() > 90 * 1000) {
                toRemoveDataNodes.add(datanode.getIp() + "-" + datanode.getHostname());
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

  /**
   * 分配双副本对应的数据节点
   *
   * @param fileSize
   * @return
   */
  public List<DataNodeInfo> allocateDataNodes(long fileSize) {
    synchronized (this) {
      // 取出来所有的datanode，并且按照已经存储的数据大小来排序
      List<DataNodeInfo> datanodeList = new ArrayList<>();
      datanodeList.addAll(dataNodes.values());
      Collections.sort(datanodeList);

      // 选择存储数据最少的头两个datanode出来
      List<DataNodeInfo> selectedDataNodes = new ArrayList<>();
      if (datanodeList.size() >= 2) {
        selectedDataNodes.add(datanodeList.get(0));
        selectedDataNodes.add(datanodeList.get(1));

        // 你可以做成这里是副本数量可以动态配置的，但是我这里的话呢给写死了，就是双副本

        // 默认认为：要上传的文件会被放到那两个datanode上去
        // 此时就应该更新那两个datanode存储数据的大小，加上上传文件的大小
        // 你只有这样做了，后面别人再次要过来上传文件的时候，就可以基于最新的存储情况来进行排序了
        datanodeList.get(0).addStoredDataSize(fileSize);
        datanodeList.get(1).addStoredDataSize(fileSize);
      }

      return selectedDataNodes;
    }
  }
}
