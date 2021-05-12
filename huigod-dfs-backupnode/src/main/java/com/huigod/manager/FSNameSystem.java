package com.huigod.manager;

import com.alibaba.fastjson.JSONObject;
import com.huigod.entity.FSImage;
import com.huigod.entity.INode;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责管理元数据的核心组件
 */
@Slf4j
@Data
public class FSNameSystem {

  /**
   * 负责管理内存文件目录树的组件
   */
  private FSDirectory directory;

  private long checkpointTime;
  private long syncedTxid;
  private String checkpointFile = "";
  private volatile boolean finishedRecover = false;

  public FSNameSystem() {
    this.directory = new FSDirectory();
    recoverNamespace();
  }

  /**
   * 创建目录
   *
   * @param path 目录路径
   * @return 是否成功
   */
  public Boolean mkdir(long txid, String path) throws Exception {
    this.directory.mkdir(txid, path);
    return true;
  }

  /**
   * 获取文件目录树的json
   *
   * @return
   */
  public FSImage getFSImage() {
    return directory.getFSImage();
  }

  /**
   * 获取当前同步到的最大的txid
   *
   * @return
   */
  public long getSyncedTxid() {
    return directory.getFSImage().getMaxTxid();
  }

  /**
   * 恢复元数据
   */
  public void recoverNamespace() {
    try {
      //加载checkpoint txid
      loadCheckpointInfo();

      //加载fsimage文件
      loadFSImage();
      finishedRecover = true;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 加载checkpoint txid
   */
  private void loadCheckpointInfo() throws Exception {

    FileInputStream in = null;
    FileChannel channel = null;

    String path = "backupnode/checkpoint-info.meta";
    File file = new File(path);

    if (!file.exists()) {
      log.info("checkpoint info文件不存在，不进行恢复.......");
      return;
    }

    try {
      in = new FileInputStream(path);
      channel = in.getChannel();

      //分配的内存空间可以动态调整
      ByteBuffer buffer = ByteBuffer.allocate(1024);

      int count = channel.read(buffer);
      buffer.flip();

      String checkpointInfo = new String(buffer.array(), 0, count);
      long checkpointTime = Long.valueOf(checkpointInfo.split("_")[0]);
      long syncedTxid = Long.valueOf(checkpointInfo.split("_")[1]);
      String fsimageFile = checkpointInfo.split("_")[2];

      log.info("恢复checkpoint time：" + checkpointTime + ", synced txid: " + syncedTxid
          + ", fsimage file: " + fsimageFile);
      this.checkpointTime = checkpointTime;
      this.syncedTxid = syncedTxid;
      this.checkpointFile = fsimageFile;

      directory.setMaxTxid(syncedTxid);

    } catch (Exception e) {
      log.error("loadCheckpointInfo is error:", e);
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
   * 加载fsimage文件到内存里来进行恢复
   */
  private void loadFSImage() throws Exception {
    FileInputStream in = null;
    FileChannel channel = null;
    try {
      String path = "backupnode/fsimage-" + syncedTxid + ".meta";
      File file = new File(path);
      if (!file.exists()) {
        log.info("fsimage文件当前不存在，不进行恢复.......");
        return;
      }

      in = new FileInputStream(path);
      channel = in.getChannel();

      ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024); // 这个参数是可以动态调节的
      // 每次你接受到一个fsimage文件的时候记录一下他的大小，持久化到磁盘上去
      // 每次重启就分配对应空间的大小就可以了
      int count = channel.read(buffer);

      buffer.flip();
      String fsimageJson = new String(buffer.array(), 0, count);
      log.info("恢复fsimage文件path:{}中的数据：{}", path, fsimageJson);

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

}
