package com.huigod.manager;

import com.huigod.entity.FSImage;
import com.huigod.network.NameNodeRpcClient;
import com.huigod.server.BackupNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

/**
 * fsimage文件的checkpoint组件
 */
@Slf4j
public class FSImageCheckpointer extends Thread {

  /**
   * checkpoint操作的时间间隔
   */
  public static final Integer CHECKPOINT_INTERVAL = 60 * 1000;

  private BackupNode backupNode;
  private FSNameSystem nameSystem;
  private NameNodeRpcClient nameNode;
  private String lastFsimageFile = "";
  private long checkpointTime = System.currentTimeMillis();


  public FSImageCheckpointer(BackupNode backupNode, FSNameSystem nameSystem,
      NameNodeRpcClient nameNode) {
    this.backupNode = backupNode;
    this.nameSystem = nameSystem;
    this.nameNode = nameNode;
  }

  @Override
  public void run() {
    log.info("fsimage checkpoint定时调度线程启动......");

    while (backupNode.isRunning()) {
      try {
        if (!nameSystem.isFinishedRecover()) {
          log.info("当前还没完成元数据恢复，不进行checkpoint......");
          Thread.sleep(1000);
          continue;
        }

        if ("".equals(lastFsimageFile)) {
          this.lastFsimageFile = nameSystem.getCheckpointFile();
        }

        long now = System.currentTimeMillis();
        if (now - checkpointTime > CHECKPOINT_INTERVAL) {
          if (!nameNode.isNameNodeRunning()) {
            log.info("namenode当前无法访问，不执行checkpoint......");
            continue;
          }

          log.info("准备执行checkpoint操作，写入fsimage文件......");
          //checkpoint操作
          doCheckpoint();
          log.info("完成checkpoint操作......");
        }

        Thread.sleep(1000);
      } catch (Exception e) {
        log.error("FSImageCheckpointer run is error:", e);
      }
    }
  }

  /**
   * 将fsiamge持久化到磁盘上去
   */
  private void doCheckpoint() throws Exception {
    FSImage fsimage = nameSystem.getFSImage();
    //将旧的fsimage删除
    removeLastFSImageFile();
    //将fsimage写入磁盘文件
    writeFSImageFile(fsimage);
    //将fsimage文件上传到NameNode
    uploadFSImageFile(fsimage);
    //通知NameNode对应的fsimage的txid
    updateCheckpointTxid(fsimage);
    //持久化checkpoint相关数据
    saveCheckpointInfo(fsimage);
  }

  /**
   * 持久化checkpoint信息
   *
   * @param fsimage
   */
  private void saveCheckpointInfo(FSImage fsimage) {
    String path = "backupnode/checkpoint-info.meta";
    RandomAccessFile raf = null;
    FileOutputStream out = null;
    FileChannel channel = null;

    try {
      File file = new File(path);
      if(file.exists()) {
        file.delete();
      }

      long now = System.currentTimeMillis();
      this.checkpointTime = now;
      long checkpointTxid = fsimage.getMaxTxid();
      ByteBuffer buffer = ByteBuffer.wrap((now + "_" + checkpointTxid + "_" + lastFsimageFile).getBytes());

      raf = new RandomAccessFile(path, "rw");
      out = new FileOutputStream(raf.getFD());
      channel = out.getChannel();

      channel.write(buffer);
      channel.force(false);

      log.info("checkpoint信息持久化到磁盘文件......");
    } catch(Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if(out != null) {
          out.close();
        }
        if(raf != null) {
          raf.close();
        }
        if(channel != null) {
          channel.close();
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    }
  }

  /**
   * 删除上一个fsimage磁盘文件
   */
  private void removeLastFSImageFile() throws Exception {
    File file = new File(lastFsimageFile);
    if (file.exists()) {
      file.delete();
    }
  }

  /**
   * 写入最新的fsimage文件
   *
   * @throws Exception
   */
  private void writeFSImageFile(FSImage fsimage) throws Exception {
    ByteBuffer buffer = ByteBuffer.wrap(fsimage.getFsimageJson().getBytes(StandardCharsets.UTF_8));
    //fsimage文件名
    String fsimageFilePath = "backupnode/fsimage-"
        + fsimage.getMaxTxid() + ".meta";

    lastFsimageFile = fsimageFilePath;

    RandomAccessFile file = null;
    FileOutputStream out = null;
    FileChannel channel = null;

    try {

      file = new RandomAccessFile(fsimageFilePath, "rw");
      out = new FileOutputStream(file.getFD());
      channel = out.getChannel();

      channel.write(buffer);
      channel.force(false);
    } catch (Exception e) {
      log.error("doCheckpoint is error:", e);
    } finally {
      if (out != null) {
        out.close();
      }
      if (file != null) {
        file.close();
      }
      if (channel != null) {
        channel.close();
      }
    }
  }

  /**
   * 上传fsimage文件
   *
   * @param fsimage
   * @throws Exception
   */
  private void uploadFSImageFile(FSImage fsimage) throws Exception {
    FSImageUploader fsimageUploader = new FSImageUploader(fsimage);
    fsimageUploader.start();
  }

  /**
   * 更新checkpoint txid
   *
   * @param fsimage
   */
  private void updateCheckpointTxid(FSImage fsimage) {
    nameNode.updateCheckpointTxid(fsimage.getMaxTxid());
  }
}
