package com.huigod.manager;

import com.alibaba.fastjson.JSONObject;
import com.huigod.entity.INode;
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
import java.util.List;
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

  public FSNameSystem() {
    this.directory = new FSDirectory();
    this.editLog = new FSEditLog(this);
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
    this.editLog.logEdit("{'OP':'MKDIR','PATH':'" + path + "'}");
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
   * 接受到BackupNode通知的fsimage对应的checkpoint txid
   * 触发一次持久化，让重启后能够对应恢复
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
                  e.printStackTrace();
                }
              }
            }
          }
        }
      }
    }
  }

}
