package com.huigod.manager;


import com.huigod.entity.EditLog;
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 负责管理edits log日志的核心组件
 */
@Slf4j
public class FSEditLog {

  /**
   * 当前递增到的txid的序号
   */
  private long txidSeq = 0L;

  /**
   * editlog日志文件清理的时间间隔
   */
  private static final Long EDIT_LOG_CLEAN_INTERVAL = 30 * 1000L;

  /**
   * 内存双缓冲区
   */
  private DoubleBuffer doubleBuffer = new DoubleBuffer();
  /**
   * 当前是否在将内存缓冲刷入磁盘中
   */
  private volatile Boolean isSyncRunning = false;
  /**
   * 在同步到磁盘中的最大的一个txid
   */
  private volatile Long syncTxid = 0L;
  /**
   * 是否正在调度一次刷盘的操作
   */
  private volatile Boolean isSchedulingSync = false;
  /**
   * 每个线程自己本地的txid副本
   */
  private ThreadLocal<Long> localTxid = new ThreadLocal<Long>();
  /**
   * 元数据管理组件
   */
  private FSNameSystem nameSystem;


  public FSEditLog(FSNameSystem nameSystem) {
    this.nameSystem = nameSystem;

    EditLogCleaner editlogCleaner = new EditLogCleaner();
    editlogCleaner.start();
  }

  /**
   * 记录edits log日志
   *
   * @param content
   */
  public void logEdit(String content) {
    //写数据需要加锁
    synchronized (this) {
      // 检查是否正在调度一次刷盘的操作
      waitSchedulingSync();

      //设置当前线程事物id
      txidSeq++;
      long txid = txidSeq;
      localTxid.set(txid);

      EditLog editLog = new EditLog(txid, content);
      try {
        doubleBuffer.write(editLog);
      } catch (Exception e) {
        log.error("logEdit is error:", e);
      }

      //检查缓冲区是否满
      if (!doubleBuffer.shouldSyncToDisk()) {
        return;
      }

      //需要进行刷磁盘操作
      isSchedulingSync = true;
    }

    //这里的线程是检测到缓冲区满的线程
    //释放锁后触发执行刷盘的操作
    logSync();

  }

  /**
   * 等待正在调度的刷磁盘的操作
   */
  private void waitSchedulingSync() {
    try {
      while (isSchedulingSync) {
        wait(1000);
      }
    } catch (Exception e) {
      log.error("waitSchedulingSync is error:", e);
    }
  }

  /**
   * 将内存缓冲中的数据刷入磁盘文件中
   */
  private void logSync() {

    //交换完缓冲区后，直接唤醒业务线程让其可以继续写入数据到缓冲区
    //避免最好是的刷盘操作阻塞业务线程
    synchronized (this) {
      long txid = localTxid.get();

      //此时正在刷磁盘数据
      if (isSyncRunning) {
        //可能之前被wait的线程，也触发了刷盘操作
        //但是txit小于刷盘的syncTxid，说明他的数据已经被其他线程刷盘了，可以直接返回
        if (txid <= syncTxid) {
          return;
        }

        //否则需要等待去刷盘的操作
        try {
          while (isSyncRunning) {
            wait(1000);
          }
        } catch (Exception e) {
          log.error("logSync wait is error:", e);
        }
      }

      //交换两块缓冲区
      doubleBuffer.setReadyToSync();

      //保存刷磁盘时的txid，也就是缓冲区中最大的txid
      syncTxid = txid;

      //让业务线程不需要继续阻塞
      isSchedulingSync = false;
      //唤醒业务写的线程
      notifyAll();
      //阻塞需要刷盘的其他线程
      isSyncRunning = true;
    }

    try {
      doubleBuffer.flush();
    } catch (Exception e) {
      log.error("logSync is error:", e);
    }

    //刷盘完成后，修改标志位并且唤醒下一次刷盘
    synchronized (this) {
      isSyncRunning = false;
      notifyAll();
    }
  }

  /**
   * 强制把内存缓冲里的数据刷入磁盘中
   */
  public void flush() {
    doubleBuffer.setReadyToSync();
    try {
      doubleBuffer.flush();
    } catch (Exception e) {
      log.error("flush is error:", e);
    }
  }

  /**
   * 获取已经刷入磁盘的editslog数据
   *
   * @return
   */
  public List<String> getFlushedTxids() {
    return doubleBuffer.getFlushedTxids();
  }

  public void addFlushedTxids(long startTxid,long endTxid) {
    doubleBuffer.addFlushedTxids(startTxid + "_" + endTxid);
  }

  /**
   * 获取当前缓冲区里的数据
   *
   * @return
   */
  public String[] getBufferedEditsLog() {

    synchronized (this) {
      return doubleBuffer.getBufferedEditsLog();
    }
  }

  /**
   * 自动清理editlog文件
   */
  private class EditLogCleaner extends Thread {

    @Override
    public void run() {
      log.info("editlog日志文件后台清理线程启动......");
      try {
        while (true) {
          Thread.sleep(EDIT_LOG_CLEAN_INTERVAL);

          List<String> flushedTxids = getFlushedTxids();
          if (!CollectionUtils.isEmpty(flushedTxids)) {
            long checkpointTxid = nameSystem.getCheckpointTxid();

            for (String flushedTxid : flushedTxids) {
              long startTxid = Long.parseLong(flushedTxid.split("_")[0]);
              long endTxid = Long.parseLong(flushedTxid.split("_")[1]);

              if (checkpointTxid >= endTxid) {
                // 此时就要删除这个文件
                File file = new File("logs/edits-" + startTxid + "-" + endTxid + ".log");
                if (file.exists()) {
                  file.delete();
                  log.info("发现editlog日志文件不需要，进行删除:{}", file.getPath());
                }
              }
            }
          }

        }
      } catch (Exception e) {
        log.error("EditLogCleaner run is error:", e);
      }
    }
  }

  public void setTxidSeq(long txidSeq) {
    this.txidSeq = txidSeq;
  }
}
