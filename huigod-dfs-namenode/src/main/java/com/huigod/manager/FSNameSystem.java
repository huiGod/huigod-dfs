package com.huigod.manager;

import com.alibaba.fastjson.JSONObject;
import com.huigod.entity.DataNodeInfo;
import com.huigod.entity.INode;
import com.huigod.entity.RemoveReplicaTask;
import com.huigod.service.impl.EditLogFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 负责管理元数据的核心组件
 */
@Slf4j
public class FSNameSystem {

  /**
   * 负责管理内存文件目录树的组件
   */
  private FSDirectory directory;
  /**
   * 负责管理edits log写入磁盘的组件
   */
  private FSEditLog editLog;

  /**
   * 最近一次checkpoint更新到的txid
   */
  private long checkpointTxid = 0;

  /**
   * 每个文件对应的副本所在的DataNode
   */
  private Map<String, List<DataNodeInfo>> replicasByFilename = new HashMap<>();

  /**
   * 每个DataNode对应的所有的文件副本
   */
  private Map<String, List<String>> filesByDatanode = new HashMap<>();

  /**
   * 读写锁优化synchronized对replicasByFilename的修改
   */
  ReentrantReadWriteLock replicasByFilenameLock = new ReentrantReadWriteLock();

  /**
   * 副本数据结构的锁
   */
  ReentrantReadWriteLock replicasLock = new ReentrantReadWriteLock();

  /**
   * 副本数量
   */
  public static final Integer REPLICA_NUM = 2;

  /**
   * 数据节点管理组件
   */
  private DataNodeManager datanodeManager;

  public FSNameSystem(DataNodeManager datanodeManager) {
    this.directory = new FSDirectory();
    this.editLog = new FSEditLog(this);
    this.datanodeManager = datanodeManager;
    //数据恢复
    recoverNamespace();
  }

  /**
   * 创建目录
   *
   * @param path 目录路径
   * @return 是否成功
   */
  public Boolean mkdir(String path) throws Exception {
    this.directory.mkdir(path);
    this.editLog.logEdit(EditLogFactory.mkdir(path));
    return true;
  }

  /**
   * 强制把内存里的edits log刷入磁盘中
   */
  public void flush() {
    this.editLog.flush();
  }

  /**
   * 获取一个FSEditLog组件
   *
   * @return
   */
  public FSEditLog getEditsLog() {
    return editLog;
  }

  /**
   * 接受到BackupNode通知的fsimage对应的checkpoint txid 触发一次持久化，让重启后能够对应恢复
   *
   * @param txid
   */
  public void setCheckpointTxid(long txid) {
    log.info("接收到checkpoint txid:{}", txid);
    this.checkpointTxid = txid;

    //进行持久化
    this.saveCheckpointTxid();
  }

  public long getCheckpointTxid() {
    return checkpointTxid;
  }

  /**
   * 恢复元数据
   */
  public void recoverNamespace() {
    try {
      //从磁盘加载fsimage
      loadFSImage();
      //加载checkpoint txid
      loadCheckpointTxid();
      //加载日志数据
      loadEditLog();
    } catch (Exception e) {
      log.error("recoverNamespace is error:", e);
    }
  }

  /**
   * 加载fsimage文件到内存里来进行恢复
   */
  private void loadFSImage() throws Exception {
    FileInputStream in = null;
    FileChannel channel = null;
    File fsimage;

    try {
      fsimage = new File("backupnode/fsimage.meta");
      if (!fsimage.exists()) {
        return;
      }
      in = new FileInputStream(fsimage);
      channel = in.getChannel();

      // 每次接受到一个fsimage文件的时候记录文件大小，持久化到磁盘上去
      // 每次重启就分配对应空间的大小即可。这里简化处理，分配足够的空间
      ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);

      int count = channel.read(buffer);

      buffer.flip();

      String fsimageJson = new String(buffer.array(), 0, count);
      log.debug("loadFSImage fsimageJson is:{}", fsimageJson);

      INode dirTree = JSONObject.parseObject(fsimageJson, INode.class);
      directory.setDirTree(dirTree);
    } finally {
      if (in != null) {
        in.close();
      }
      if (channel != null) {
        channel.close();
      }
    }

  }

  /**
   * 将checkpoint txid保存到磁盘上去
   */
  public void saveCheckpointTxid() {

    String path = "backupnode/checkpoint-txid.meta";

    RandomAccessFile raf = null;
    FileOutputStream out = null;
    FileChannel channel = null;

    try {
      File file = new File(path);
      if (file.exists()) {
        file.delete();
      }

      ByteBuffer buffer = ByteBuffer.wrap(String.valueOf(checkpointTxid).getBytes());

      raf = new RandomAccessFile(path, "rw");
      out = new FileOutputStream(raf.getFD());
      channel = out.getChannel();

      channel.write(buffer);
      channel.force(false);
    } catch (Exception e) {
      log.error("saveCheckpointTxid is error:", e);
    } finally {
      try {
        if (out != null) {
          out.close();
        }
        if (raf != null) {
          raf.close();
        }
        if (channel != null) {
          channel.close();
        }
      } catch (Exception e2) {
        log.error("saveCheckpointTxid is error:", e2);
      }
    }
  }

  /**
   * 加载checkpoint txid
   */
  private void loadCheckpointTxid() throws Exception {
    FileInputStream in = null;
    FileChannel channel = null;
    File file = null;
    try {
      String path = "backupnode/checkpoint-txid.meta";

      file = new File(path);
      if (!file.exists()) {
        log.debug("checkpoint txid文件不存在，不进行恢复.......");
        return;
      }

      in = new FileInputStream(path);
      channel = in.getChannel();

      ByteBuffer buffer = ByteBuffer.allocate(1024); // 这个参数是可以动态调节的
      // 每次你接受到一个fsimage文件的时候记录一下他的大小，持久化到磁盘上去
      // 每次重启就分配对应空间的大小就可以了
      int count = channel.read(buffer);

      buffer.flip();
      long checkpointTxid = Long.valueOf(new String(buffer.array(), 0, count));
      log.info("恢复checkpoint txid：" + checkpointTxid);

      this.checkpointTxid = checkpointTxid;

      //设置日志事物递增txid
      editLog.setTxidSeq(checkpointTxid);
    } finally {
      if (in != null) {
        in.close();
      }
      if (channel != null) {
        channel.close();
      }
    }
  }

  /**
   * 加载和回放editlog
   */
  private void loadEditLog() throws Exception {
    File dir = new File("logs/");
    List<File> files = new ArrayList<>();

    if (CollectionUtils.isEmpty(files)) {
      return;
    }

    for (File file : dir.listFiles()) {
      files.add(file);
    }

    files.removeIf(file -> !file.getName().contains("edits"));

    //按照文件名txid大小排序
    files.sort((o1, o2) -> {
      Integer o1StartTxid = Integer.valueOf(o1.getName().split("-")[1]);
      Integer o2StartTxid = Integer.valueOf(o2.getName().split("-")[1]);
      return o1StartTxid - o2StartTxid;
    });

    if (files.size() == 0) {
      log.info("当前没有任何editlog文件，不进行恢复......");
      return;
    }

    for (File file : files) {
      if (file.getName().contains("edits")) {
        log.info("准备恢复editlog文件中的数据：" + file.getName());

        String[] splitedName = file.getName().split("-");
        long startTxid = Long.valueOf(splitedName[1]);
        long endTxid = Long.valueOf(splitedName[2].split("[.]")[0]);

        //扫描磁盘editLog文件的时候，同时恢复内存中flushTxid
        editLog.addFlushedTxids(startTxid, endTxid);

        // 如果是checkpointTxid之后的那些editlog都要加载出来
        if (endTxid > checkpointTxid) {
          String currentEditsLogFile = "logs/edits-" + startTxid + "-" + endTxid + ".log";
          List<String> editsLogs = Files.readAllLines(Paths.get(currentEditsLogFile),
              StandardCharsets.UTF_8);

          for (String editLogJson : editsLogs) {
            JSONObject editLogMap = JSONObject.parseObject(editLogJson);
            long txid = editLogMap.getLongValue("txid");

            if (txid > checkpointTxid) {
              editLog.setTxidSeq(txid);

              log.info("准备回放editlog：" + editLogJson);

              // 回放到内存里去
              String op = editLogMap.getString("OP");

              if (op.equals("MKDIR")) {
                String path = editLogMap.getString("PATH");
                try {
                  directory.mkdir(path);
                } catch (Exception e) {
                  log.error("mkdir is error:", e);
                }
              } else if (op.equals("CREATE")) {
                String path = editLogMap.getString("PATH");
                try {
                  directory.create(path);
                } catch (Exception e) {
                  log.error("create is error:", e);
                }
              }
            }
          }
        }
      }
    }
  }

  /**
   * 创建文件
   *
   * @param filename 文件名，包含所在的绝对路径，/products/img001.jpg
   * @return
   * @throws Exception
   */
  public Boolean create(String filename) {
    if (!directory.create(filename)) {
      return false;
    }
    editLog.logEdit(EditLogFactory.create(filename));
    return true;
  }

  /**
   * 给指定的文件增加一个成功接收的文件副本
   *
   * @param filename
   * @throws Exception
   */
  public void addReceivedReplica(String hostname, String ip, String filename, long fileLength) {
    try {
      replicasLock.writeLock().lock();

      DataNodeInfo datanode = datanodeManager.getDatanode(ip, hostname);

      //获取副本所在DataNode机器信息
      List<DataNodeInfo> replicas = replicasByFilename.get(filename);
      if (CollectionUtils.isEmpty(replicas)) {
        replicas = new ArrayList<>();
        replicasByFilename.put(filename, replicas);
      }

      // 检查当前文件的副本数量是否超标
      if (replicas.size() == REPLICA_NUM) {
        // 减少这个节点上的存储数据量
        datanode.addStoredDataSize(-fileLength);

        // 生成副本复制任务
        RemoveReplicaTask removeReplicaTask = new RemoveReplicaTask(filename, datanode);
        datanode.addRemoveReplicaTask(removeReplicaTask);

        return;
      }

      // 如果副本数量未超标，才会将副本放入数据结构中
      replicas.add(datanode);

      // 维护每个数据节点拥有的文件副本
      List<String> files = filesByDatanode
          .computeIfAbsent(ip + "-" + hostname, k -> new ArrayList<>());

      files.add(filename + "_" + fileLength);

      log.info("收到增量上报，当前的副本信息为：" + replicasByFilename);
    } finally {
      replicasLock.writeLock().unlock();
    }
  }

  /**
   * 获取文件的某个副本所在的机器
   *
   * @param filename
   * @return
   */
  public DataNodeInfo getDataNodeForFile(String filename) {
    try {
      replicasByFilenameLock.readLock().lock();
      List<DataNodeInfo> dataNodes = replicasByFilename.get(filename);
      int size = dataNodes.size();

      Random random = new Random();
      int index = random.nextInt(size);

      return dataNodes.get(index);
    } finally {
      replicasByFilenameLock.readLock().unlock();
    }

  }

  /**
   * 删除数据节点的文件副本的数据结构
   *
   * @param datanode
   */
  public void removeDeadDatanode(DataNodeInfo datanode) {
    try {
      replicasLock.writeLock().lock();

      List<String> filenames = filesByDatanode.get(datanode.getId());
      for (String filename : filenames) {
        List<DataNodeInfo> replicas = replicasByFilename.get(filename.split("_")[0]);
        //删除文件对应的DataNode节点信息
        replicas.remove(datanode);
      }

      //删除节点信息
      filesByDatanode.remove(datanode.getId());

      log.info("从内存数据结构中删除掉这个数据节点关联的数据，" + replicasByFilename + "，" + filesByDatanode);
    } finally {
      replicasLock.writeLock().unlock();
    }
  }

  /**
   * 获取数据节点包含的文件
   *
   * @param ip
   * @param hostname
   * @return
   */
  public List<String> getFilesByDatanode(String ip, String hostname) {
    try {
      replicasLock.readLock().lock();

      log.info(
          "当前filesByDatanode为" + filesByDatanode + "，将要以key=" + ip + "-" + hostname + "获取文件列表");

      return filesByDatanode.get(ip + "-" + hostname);
    } catch (Exception e) {
      log.error("getFilesByDatanode is error:", e);
    } finally {
      replicasLock.readLock().unlock();
    }
    return null;
  }

  /**
   * 获取复制任务的源头数据节点
   *
   * @param filename
   * @param deadDatanode
   * @return
   */
  public DataNodeInfo getReplicateSource(String filename, DataNodeInfo deadDatanode) {
    DataNodeInfo replicateSource = null;

    try {
      replicasLock.readLock().lock();
      List<DataNodeInfo> replicas = replicasByFilename.get(filename);
      for (DataNodeInfo replica : replicas) {
        if (!replica.equals(deadDatanode)) {
          replicateSource = replica;
        }
      }
    } finally {
      replicasLock.readLock().unlock();
    }

    return replicateSource;
  }

  /**
   * 获取文件的某个副本所在的机器
   *
   * @param filename
   * @param excludedDataNodeId
   * @return
   */
  public DataNodeInfo chooseDataNodeFromReplicas(String filename, String excludedDataNodeId) {
    try {
      replicasLock.readLock().lock();

      DataNodeInfo excludedDataNode = datanodeManager.getDatanode(excludedDataNodeId);

      List<DataNodeInfo> datanodes = replicasByFilename.get(filename);
      if (datanodes.size() == 1) {
        if (datanodes.get(0).equals(excludedDataNode)) {
          return null;
        }
      }

      int size = datanodes.size();
      Random random = new Random();

      while (true) {
        int index = random.nextInt(size);
        DataNodeInfo datanode = datanodes.get(index);
        if (!datanode.equals(excludedDataNode)) {
          return datanode;
        }
      }
    } finally {
      replicasLock.readLock().lock();
    }
  }

  /**
   * 从数据节点删除掉一个文件副本
   *
   * @param id
   * @param file
   */
  public void removeReplicaFromDataNode(String id, String file) {
    try {
      replicasLock.writeLock().lock();

      filesByDatanode.get(id).remove(file);

      replicasByFilename.get(file.split("_")[0]).removeIf(replica -> replica.getId().equals(id));
    } finally {
      replicasLock.writeLock().unlock();
    }
  }
}
