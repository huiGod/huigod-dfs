package com.huigod.manager;

import com.huigod.entity.FSImage;
import com.huigod.server.BackupNode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * fsimage文件的checkpoint组件
 */
@Slf4j
public class FSImageCheckpointer extends Thread {

  /**
   * checkpoint操作的时间间隔
   */
  public static final Integer CHECKPOINT_INTERVAL = 20 * 1000;

  private BackupNode backupNode;
  private FSNameSystem nameSystem;
  private String lastFsimageFile;

  public FSImageCheckpointer(BackupNode backupNode, FSNameSystem namesystem) {
    this.backupNode = backupNode;
    this.nameSystem = namesystem;
  }

  @Override
  public void run() {
    log.info("fsimage checkpoint定时调度线程启动......");

    while (backupNode.isRunning()) {
      try {
        Thread.sleep(CHECKPOINT_INTERVAL);

        //移除fsimage磁盘文件
        removeLastFsimageFile();

        FSImage fsimage = nameSystem.getFSImage();
        doCheckpoint(fsimage);

      } catch (Exception e) {
        log.error("FSImageCheckpointer run is error:", e);
      }


    }
  }

  /**
   * 除上一个fsimage磁盘文件
   */
  private void removeLastFsimageFile() {
    if (StringUtils.isNotBlank(lastFsimageFile)) {
      File file = new File(lastFsimageFile);
      if (file.exists()) {
        file.delete();
      }
    }
  }

  /**
   * 将fsiamge持久化到磁盘上去
   *
   * @param fsimage
   */
  private void doCheckpoint(FSImage fsimage) throws Exception {

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
}
