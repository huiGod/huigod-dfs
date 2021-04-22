package com.huigod.manager;

import com.huigod.entity.EditLog;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

/**
 * 内存双缓冲
 */
@Slf4j
public class DoubleBuffer {

  /**
   * 单块editslog缓冲区的最大大小：默认是512kb
   */
  public static final Integer EDIT_LOG_BUFFER_LIMIT = 25 * 1024;

  /**
   * 是专门用来承载线程写入edits log
   */
  EditLogBuffer currentBuffer = new EditLogBuffer();
  /**
   * 专门用来将数据同步到磁盘中去的一块缓冲
   */
  EditLogBuffer syncBuffer = new EditLogBuffer();

  /**
   * 将edits log写到内存缓冲里去
   */
  public void write(EditLog log) throws Exception {
    currentBuffer.write(log);
  }

  /**
   * 交换两块缓冲区，为了同步内存数据到磁盘做准备
   */
  public void setReadyToSync() {
    EditLogBuffer tmp = currentBuffer;
    currentBuffer = syncBuffer;
    syncBuffer = tmp;
  }

  /**
   * 判断一下当前的缓冲区是否写满了需要刷到磁盘上去
   * @return
   */
  public boolean shouldSyncToDisk() {
    if(currentBuffer.size() >= EDIT_LOG_BUFFER_LIMIT) {
      return true;
    }
    return false;
  }

  /**
   * 将syncBuffer缓冲区中的数据刷入磁盘中
   */
  public void flush() throws Exception {
    syncBuffer.flush();
    syncBuffer.clear();
  }

  /**
   * editslog缓冲区
   */
  public class EditLogBuffer {

    /**
     * 针对内存缓冲区的字节数组输出流
     */
    ByteArrayOutputStream buffer;

    /**
     * 当前这块缓冲区写入的最大的一个txid
     */
    long maxTxid = 0L;

    /**
     * 上一次flush到磁盘的时候他的最大的txid是多少
     */
    long lastMaxTxid = 0L;

    public EditLogBuffer() {
      this.buffer = new ByteArrayOutputStream(EDIT_LOG_BUFFER_LIMIT * 2);
    }

    /**
     * 将editslog日志写入缓冲区
     *
     * @param editLog
     * @throws IOException
     */
    public void write(EditLog editLog) throws IOException {
      this.maxTxid = editLog.getTxid();
      buffer.write(editLog.getContent().getBytes(StandardCharsets.UTF_8));
      buffer.write("\n".getBytes(StandardCharsets.UTF_8));
      log.info("写入缓冲区数据为：{}，当前缓冲区大小：{}", editLog.getContent(), size());
    }

    /**
     * 获取当前缓冲区已经写入数据的字节数量
     *
     * @return
     */
    public Integer size() {
      return buffer.size();
    }

    /**
     * 将sync buffer中的数据刷入磁盘中
     *
     * @throws IOException
     */
    public void flush() throws IOException {
      byte[] data = buffer.toByteArray();
      ByteBuffer dataBuffer = ByteBuffer.wrap(data);

      String editsLogFilePath = "\\data\\edits-"
          + (++lastMaxTxid) + "-" + maxTxid + ".log";

      RandomAccessFile file = null;
      FileOutputStream out = null;
      FileChannel editsLogFileChannel = null;

      try {
        file = new RandomAccessFile(editsLogFilePath, "rw");
        out = new FileOutputStream(file.getFD());
        editsLogFileChannel = out.getChannel();

        editsLogFileChannel.write(dataBuffer);
        editsLogFileChannel.force(true);
      } finally {
        if (out != null) {
          out.close();
        }

        if (file != null) {
          file.close();
        }

        if (editsLogFileChannel != null) {
          editsLogFileChannel.close();
        }

        this.lastMaxTxid = maxTxid;
      }
    }

    /**
     * 清空掉内存缓冲里面的数据
     */
    public void clear() {
      buffer.reset();
    }
  }
}
