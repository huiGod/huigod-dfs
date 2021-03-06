package com.huigod.server;

import com.alibaba.fastjson.JSONObject;
import com.huigod.util.FileUtils;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * 副本复制管理组件
 */
@Slf4j
public class ReplicateManager {

  public static final Integer REPLICATE_THREAD_NUM = 3;

  /**
   * NIOClient
   */
  private NioClient nioClient = new NioClient();

  private NameNodeRpcClient namenodeRpcClient;

  /**
   * 副本复制任务队列
   */
  private ConcurrentLinkedQueue<JSONObject> replicateTaskQueue = new ConcurrentLinkedQueue<>();

  public ReplicateManager(NameNodeRpcClient namenodeRpcClient) {
    this.namenodeRpcClient = namenodeRpcClient;

    for (int i = 0; i < REPLICATE_THREAD_NUM; i++) {
      new ReplicateWorker().start();
    }
  }

  /**
   * 添加复制任务
   *
   * @param replicateTask
   */
  public void addReplicateTask(JSONObject replicateTask) {
    replicateTaskQueue.offer(replicateTask);
  }

  /**
   * 副本复制线程
   *
   * @author zhonghuashishan
   */
  class ReplicateWorker extends Thread {

    @Override
    public void run() {
      while (true) {
        FileOutputStream imageOut = null;
        FileChannel imageChannel = null;

        try {
          JSONObject replicateTask = replicateTaskQueue.poll();
          if (replicateTask == null) {
            Thread.sleep(1000);
            continue;
          }

          log.info("开始执行副本复制任务......");

          // 解析复制任务
          String filename = replicateTask.getString("filename");
          Long fileLength = replicateTask.getLong("fileLength");

          JSONObject sourceDatanode = replicateTask.getJSONObject("sourceDatanode");
          String hostname = sourceDatanode.getString("hostname");
          Integer nioPort = sourceDatanode.getInteger("nioPort");

          // 跟源头数据接头通信读取图片过来
          byte[] file = nioClient.readFile(hostname, nioPort, filename);
          ByteBuffer fileBuffer = ByteBuffer.wrap(file);
          log.info("从源头数据节点读取到图片，大小为：" + file.length + "字节");

          // 根据文件的相对路径定位到绝对路径，写入本地磁盘文件中
          String absoluteFilename = FileUtils.getAbsoluteFilename(filename);
          imageOut = new FileOutputStream(absoluteFilename);
          imageChannel = imageOut.getChannel();
          imageChannel.write(fileBuffer);
          log.info("将图片写入本地磁盘文件，路径为：" + absoluteFilename);

          // 进行增量上报
          namenodeRpcClient.informReplicaReceived(filename + "_" + fileLength);
          log.info("向Master节点进行增量上报......");
        } catch (Exception e) {
          log.error("ReplicateWorker is error:", e);
        } finally {
          try {
            if (imageChannel != null) {
              imageChannel.close();
            }
            if (imageOut != null) {
              imageOut.close();
            }
          } catch (Exception e2) {
            log.error("ReplicateWorker is error:", e2);
          }
        }
      }
    }

  }
}
