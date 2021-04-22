package com.huigod.manager;


import com.huigod.entity.EditLog;
import lombok.extern.slf4j.Slf4j;

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

    //交换完缓冲区后，直接唤醒业务线程让其可以继续写入数据到缓冲区，真正刷盘的过程不用让业务线程继续阻塞等待
    synchronized (this) {
      long txid = localTxid.get();

      //此时正在刷磁盘数据
      if (isSyncRunning) {

      }

      //交换两块缓冲区
      doubleBuffer.setReadyToSync();

      //保存刷磁盘时的txid，也就是缓冲区中最大的txid
      syncTxid = txid;

      //让业务线程不需要继续阻塞
      isSchedulingSync = false;
      notifyAll();
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
}
