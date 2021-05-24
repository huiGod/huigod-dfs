package com.huigod.manager;

import com.huigod.entity.DataNodeInfo;
import com.huigod.entity.RemoveReplicaTask;
import com.huigod.entity.ReplicateTask;
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

  private FSNameSystem nameSystem;

  public void setNameSystem(FSNameSystem nameSystem) {
    this.nameSystem = nameSystem;
  }

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
   * 获取DataNode信息
   *
   * @param ip
   * @param hostname
   * @return
   */
  public DataNodeInfo getDatanode(String ip, String hostname) {
    return dataNodes.get(ip + "-" + hostname);
  }

  /**
   * 获取DataNode信息
   *
   * @return
   */
  public DataNodeInfo getDatanode(String id) {
    return dataNodes.get(id);
  }

  /**
   * 分配双副本对应的数据节点
   *
   * @param fileSize
   * @param excludedDataNodeId
   * @return
   */
  public DataNodeInfo reallocateDataNode(long fileSize, String excludedDataNodeId) {
    synchronized (this) {
      // 先得把排除掉的那个数据节点的存储的数据量减少文件的大小
      DataNodeInfo excludedDataNode = dataNodes.get(excludedDataNodeId);
      excludedDataNode.addStoredDataSize(-fileSize);

      // 取出来所有的datanode，并且按照已经存储的数据大小来排序
      List<DataNodeInfo> datanodeList = new ArrayList<>();
      for (DataNodeInfo datanode : dataNodes.values()) {
        if (!excludedDataNode.equals(datanode)) {
          datanodeList.add(datanode);
        }
      }
      Collections.sort(datanodeList);

      // 选择存储数据最少的datanode出来
      DataNodeInfo selectedDatanode = null;
      if (datanodeList.size() >= 1) {
        selectedDatanode = datanodeList.get(0);
        datanodeList.get(0).addStoredDataSize(fileSize);
      }

      return selectedDatanode;
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

  /**
   * 设置一个DataNode的存储数据的大小
   *
   * @param ip
   * @param hostname
   * @param storedDataSize
   */
  public void setStoredDataSize(String ip, String hostname, Long storedDataSize) {
    DataNodeInfo datanode = dataNodes.get(ip + "-" + hostname);
    datanode.setStoredDataSize(storedDataSize);
  }

  /**
   * 创建丢失副本的复制任务
   */
  private void createLostReplicaTask(DataNodeInfo deadDatanode) {
    List<String> files = nameSystem.getFilesByDatanode(
        deadDatanode.getIp(), deadDatanode.getHostname());

    for (String file : files) {
      String filename = file.split("_")[0];
      Long fileLength = Long.valueOf(file.split("_")[1]);
      // 获取这个复制任务的源头数据节点
      DataNodeInfo sourceDatanode = nameSystem.getReplicateSource(filename, deadDatanode);
      // 复制任务的目标数据节点，第一，不能是已经死掉的节点 ；第二，不能是已经有这个副本的节点
      DataNodeInfo destDatanode = allocateReplicateDataNode(fileLength, sourceDatanode,
          deadDatanode);

      ReplicateTask replicateTask = new ReplicateTask(
          filename, fileLength, sourceDatanode, destDatanode);

      // 将复制任务放到目标数据节点的任务队列里去
      destDatanode.addReplicateTask(replicateTask);
      log.info("为目标数据节点生成一个副本复制任务，" + replicateTask);
    }
  }

  /**
   * 分配用来复制副本的数据节点
   *
   * @param fileSize
   * @param sourceDatanode
   * @param deadDatanode
   * @return
   */
  private DataNodeInfo allocateReplicateDataNode(Long fileSize, DataNodeInfo sourceDatanode,
      DataNodeInfo deadDatanode) {
    synchronized (this) {
      List<DataNodeInfo> datanodeList = new ArrayList<>();
      for (DataNodeInfo datanode : dataNodes.values()) {
        if (!datanode.equals(sourceDatanode) &&
            !datanode.equals(deadDatanode)) {
          datanodeList.add(datanode);
        }
      }
      Collections.sort(datanodeList);

      DataNodeInfo selectedDatanode = null;
      if (!datanodeList.isEmpty()) {
        selectedDatanode = datanodeList.get(0);
        datanodeList.get(0).addStoredDataSize(fileSize);
      }

      return selectedDatanode;
    }
  }

  /**
   * 为重平衡去创建副本复制的任务
   */
  public void createRebalancedTasks() {
    synchronized (this) {
      // 计算集群节点存储数据的平均值
      long totalStoredDataSize = 0;
      for (DataNodeInfo datanode : dataNodes.values()) {
        totalStoredDataSize += datanode.getStoredDataSize();
      }
      long averageStoredDataSize = totalStoredDataSize / dataNodes.size();

      // 将集群中的节点区分为两类：迁出节点和迁入节点
      List<DataNodeInfo> sourceDataNodes = new ArrayList<>();
      List<DataNodeInfo> destDataNodes = new ArrayList<>();

      for (DataNodeInfo datanode : dataNodes.values()) {
        if (datanode.getStoredDataSize() > averageStoredDataSize) {
          sourceDataNodes.add(datanode);
        }
        if (datanode.getStoredDataSize() < averageStoredDataSize) {
          destDataNodes.add(datanode);
        }
      }

      // 为迁入节点生成复制的任务，为迁出节点生成删除的任务
      // 删除任务通过延迟调度执行线程来处理
      List<RemoveReplicaTask> removeReplicaTasks = new ArrayList<>();

      for (DataNodeInfo sourceDataNode : sourceDataNodes) {
        long toRemoveDataSize = sourceDataNode.getStoredDataSize() - averageStoredDataSize;

        for (DataNodeInfo destDatanode : destDataNodes) {
          // 直接一次性放到一台机器就可以了
          if (destDatanode.getStoredDataSize() + toRemoveDataSize <= averageStoredDataSize) {
            createRebalanceTasks(sourceDataNode, destDatanode,
                removeReplicaTasks, toRemoveDataSize);
            break;
          }// 只能把部分数据放到这台机器上去
          else if (destDatanode.getStoredDataSize() < averageStoredDataSize) {
            long maxRemoveDataSize = averageStoredDataSize - destDatanode.getStoredDataSize();
            long removedDataSize = createRebalanceTasks(sourceDataNode, destDatanode,
                removeReplicaTasks, maxRemoveDataSize);
            toRemoveDataSize -= removedDataSize;
          }
        }
      }
      // 交给一个延迟线程去24小时之后执行删除副本的任务
      new DelayRemoveReplicaThread(removeReplicaTasks).start();
    }
  }

  private long createRebalanceTasks(DataNodeInfo sourceDataNode, DataNodeInfo destDatanode,
      List<RemoveReplicaTask> removeReplicaTasks, long maxRemoveDataSize) {

    //查询当前节点所有文件数据
    List<String> files = nameSystem.getFilesByDatanode(sourceDataNode.getIp(),
        sourceDataNode.getHostname());

    // 遍历文件，不停的为每个文件生成一个复制的任务，直到准备迁移的文件的大小
    // 超过了待迁移总数据量的大小为止
    long removedDataSize = 0;
    for (String file : files) {
      String filename = file.split("_")[0];
      long fileLength = Long.parseLong(file.split("_")[1]);

      if (removedDataSize + fileLength >= maxRemoveDataSize) {
        break;
      }

      // 为这个文件生成复制任务
      ReplicateTask replicateTask = new ReplicateTask(
          filename, fileLength, sourceDataNode, destDatanode);
      destDatanode.addReplicateTask(replicateTask);
      destDatanode.addStoredDataSize(fileLength);

      // 为这个文件生成删除任务
      sourceDataNode.addStoredDataSize(-fileLength);
      nameSystem.removeReplicaFromDataNode(sourceDataNode.getId(), file);
      RemoveReplicaTask removeReplicaTask = new RemoveReplicaTask(
          filename, sourceDataNode);
      removeReplicaTasks.add(removeReplicaTask);

      removedDataSize += fileLength;

    }
    return removedDataSize;
  }

  /**
   * dataNode是否存活的监控线程
   */
  class DataNodeAliveMonitor extends Thread {

    @Override
    public void run() {
      try {
        while (true) {
          List<DataNodeInfo> toRemoveDataNodes = new ArrayList<>();

          if (!MapUtils.isEmpty(dataNodes)) {
            Iterator<DataNodeInfo> dataNodesIterator = dataNodes.values().iterator();
            DataNodeInfo datanode;
            while (dataNodesIterator.hasNext()) {
              datanode = dataNodesIterator.next();
              //超过90S没有更新心跳，判定为宕机并移除
              if (System.currentTimeMillis() - datanode.getLatestHeartbeatTime() > 90 * 1000) {
                toRemoveDataNodes.add(datanode);
              }
            }

            if (!toRemoveDataNodes.isEmpty()) {
              for (DataNodeInfo toRemoveDatanode : toRemoveDataNodes) {
                log.info("数据节点【" + toRemoveDatanode + "】宕机，需要 进行副本复制......");
                //创建副本复制任务
                createLostReplicaTask(toRemoveDatanode);
                dataNodes.remove(toRemoveDatanode.getId());
                // 删除掉这个数据结构
                nameSystem.removeDeadDatanode(toRemoveDatanode);
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
   * 延迟删除副本的线程
   */
  class DelayRemoveReplicaThread extends Thread {

    private List<RemoveReplicaTask> removeReplicaTasks;

    public DelayRemoveReplicaThread(List<RemoveReplicaTask> removeReplicaTasks) {
      this.removeReplicaTasks = removeReplicaTasks;
    }

    @Override
    public void run() {
      long start = System.currentTimeMillis();

      while (true) {
        try {
          long now = System.currentTimeMillis();

          if (now - start > 24 * 60 * 60 * 1000) {
            for (RemoveReplicaTask removeReplicaTask : removeReplicaTasks) {
              removeReplicaTask.getDatanode().addRemoveReplicaTask(removeReplicaTask);
            }
            break;
          }

          Thread.sleep(60 * 1000);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
}
