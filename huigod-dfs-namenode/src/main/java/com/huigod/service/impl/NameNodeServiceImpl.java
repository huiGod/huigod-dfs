package com.huigod.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.manager.DataNodeManager;
import com.huigod.manager.FSNameSystem;
import com.huigod.namenode.rpc.model.FetchEditsLogRequest;
import com.huigod.namenode.rpc.model.FetchEditsLogResponse;
import com.huigod.namenode.rpc.model.HeartbeatRequest;
import com.huigod.namenode.rpc.model.HeartbeatResponse;
import com.huigod.namenode.rpc.model.MkdirRequest;
import com.huigod.namenode.rpc.model.MkdirResponse;
import com.huigod.namenode.rpc.model.RegisterRequest;
import com.huigod.namenode.rpc.model.RegisterResponse;
import com.huigod.namenode.rpc.model.ShutdownRequest;
import com.huigod.namenode.rpc.model.ShutdownResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;

/**
 * NameNode的rpc服务的接口
 */
@Log4j2
public class NameNodeServiceImpl extends NameNodeServiceGrpc.NameNodeServiceImplBase {

  public static final Integer STATUS_SUCCESS = 1;
  public static final Integer STATUS_FAILURE = 2;
  public static final Integer STATUS_SHUTDOWN = 3;

  public static final Integer BACKUP_NODE_FETCH_SIZE = 10;

  /**
   * 负责管理元数据的核心组件：管理的是一些文件目录树，支持权限设置
   */
  private FSNameSystem nameSystem;
  /**
   * 负责管理集群中所有的Datanode的组件
   */
  private DataNodeManager datanodeManager;

  /**
   * 当前缓冲的一小部分editslog供backupNode拉取同步
   */
  private JSONArray currentBufferedEditsLog = new JSONArray();

  /**
   * 当前内存里缓冲的磁盘文件的索引数据
   */
  private String bufferedFlushedTxid;

  /**
   * 当前backupNode节点同步到了哪一条txid了
   */
  private long syncedTxid = 0L;


  /**
   * 是否还在运行
   */
  private volatile Boolean isRunning = true;

  public NameNodeServiceImpl(FSNameSystem nameSystem, DataNodeManager datanodeManager) {
    this.nameSystem = nameSystem;
    this.datanodeManager = datanodeManager;
  }

  /**
   * datanode进行注册
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
    datanodeManager.register(request.getIp(), request.getHostName());

    RegisterResponse response = RegisterResponse.newBuilder()
        .setStatus(STATUS_SUCCESS)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * datanode进行心跳
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void heartbeat(HeartbeatRequest request,
      StreamObserver<HeartbeatResponse> responseObserver) {
    datanodeManager.heartbeat(request.getIp(), request.getHostName());

    HeartbeatResponse response = HeartbeatResponse.newBuilder()
        .setStatus(STATUS_SUCCESS)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * 创建目录
   */
  @Override
  public void mkdir(MkdirRequest request, StreamObserver<MkdirResponse> responseObserver) {
    try {
      //log.info("创建目录：path {}", request.getPath());

      MkdirResponse response;

      if (!isRunning) {
        response = MkdirResponse.newBuilder()
            .setStatus(STATUS_SHUTDOWN)
            .build();
      } else {
        nameSystem.mkdir(request.getPath());
        response = MkdirResponse.newBuilder()
            .setStatus(STATUS_SUCCESS)
            .build();
      }

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("mkdir is error:", e);
    }
  }

  /**
   * 优雅关闭
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void shutdown(ShutdownRequest request, StreamObserver<ShutdownResponse> responseObserver) {
    log.info("receive shutdown message!!!!!!!!!!");
    this.isRunning = false;
    this.nameSystem.flush();
  }

  /**
   * backupNode fetch数据
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void fetchEditsLog(FetchEditsLogRequest request,
      StreamObserver<FetchEditsLogResponse> responseObserver) {
    FetchEditsLogResponse response;
    JSONArray fetchedEditsLog = new JSONArray();

    //获取所有磁盘文件索引数据
    List<String> flushedTxids = nameSystem.getEditsLog().getFlushedTxids();

    //磁盘中没有数据，则editLog都存在于内存中
    if (CollectionUtils.isEmpty(flushedTxids)) {
      fetchFromBufferedEditsLog(fetchedEditsLog);
    } else {
      //此时NameNode已经有数据刷到磁盘文件，需要扫描磁盘文件的索引范围

      //之前已经从磁盘读取过数据
      if (bufferedFlushedTxid != null) {
        // 如果fetch的txid就在当前内存缓存中，则直接读取内存数据即可
        if (existInFlushedFile(bufferedFlushedTxid)) {
          fetchFromCurrentBuffer(fetchedEditsLog);
        } else {
          //否则从下一个磁盘文件去获取
          String nextFlushedTxid = getNextFlushedTxid(flushedTxids, bufferedFlushedTxid);
          // 如果可以找到下一个磁盘文件，那么就从下一个磁盘文件里开始读取数据
          if (nextFlushedTxid != null) {
            fetchFromFlushedFile(nextFlushedTxid, fetchedEditsLog);
          }
          // 如果没有找到下一个文件，此时就需要从内存里去继续读取
          else {
            fetchFromBufferedEditsLog(fetchedEditsLog);
          }
        }
      } else {
        //第一次从磁盘获取数据

        //遍历所有的磁盘文件的索引范围，0-390，391-782
        Boolean fechedFromFlushedFile = false;
        for (String flushedTxid : flushedTxids) {
          // 如果要fetch的txid就在当前磁盘文件索引范围内
          if (existInFlushedFile(flushedTxid)) {
            //为了避免一个磁盘文件被fetch的数据不够，因此直接缓存当前磁盘文件和下一个磁盘文件的数据到内存
            fetchFromFlushedFile(flushedTxid, fetchedEditsLog);
            fechedFromFlushedFile = true;
            break;
          }
        }

        //需要fetch的txid比磁盘文件的数据都要新，则直接从内存中获取
        if (!fechedFromFlushedFile) {
          fetchFromBufferedEditsLog(fetchedEditsLog);
        }
      }
    }
    response = FetchEditsLogResponse.newBuilder()
        .setEditsLog(fetchedEditsLog.toJSONString()) // []
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * 从内存缓冲的editslog中拉取数据
   *
   * @param fetchedEditsLog
   */
  private void fetchFromBufferedEditsLog(JSONArray fetchedEditsLog) {
    currentBufferedEditsLog.clear();

    //优化点：每次从内存读取数据，没有必要都去nameNode拉取内存的完整数据（也需要进行加锁）
    //如果需要fetch的数据在内存则不需要再去nameNode读取数据
    String[] bufferedEditsLog = nameSystem.getEditsLog().getBufferedEditsLog();

    if (bufferedEditsLog != null && bufferedEditsLog.length > 0) {
      Arrays.stream(bufferedEditsLog)
          .forEach(editLog -> currentBufferedEditsLog.add(JSONObject.parseObject(editLog)));

      bufferedFlushedTxid = null;

      fetchFromCurrentBuffer(fetchedEditsLog);
    }
  }

  /**
   * 从当前已经在内存里缓存的数据中拉取editslog
   *
   * @param fetchedEditsLog
   */
  private void fetchFromCurrentBuffer(JSONArray fetchedEditsLog) {
    int fetchCount = 0;
    for (int i = 0; i < currentBufferedEditsLog.size(); i++) {
      if (currentBufferedEditsLog.getJSONObject(i).getLong("txid") == syncedTxid + 1) {
        fetchedEditsLog.add(currentBufferedEditsLog.getJSONObject(i));
        syncedTxid = currentBufferedEditsLog.getJSONObject(i).getLong("txid");
        fetchCount++;
      }
      if (fetchCount == BACKUP_NODE_FETCH_SIZE) {
        break;
      }
    }
  }

  /**
   * 判断需要fetch的txid是否在当前磁盘索引中
   *
   * @param flushedTxid
   * @return
   */
  private boolean existInFlushedFile(String flushedTxid) {
    String[] flushedTxidSplited = flushedTxid.split("_");

    long startTxid = Long.parseLong(flushedTxidSplited[0]);
    long endTxid = Long.parseLong(flushedTxidSplited[1]);
    long fetchTxid = syncedTxid + 1;

    return fetchTxid >= startTxid && fetchTxid <= endTxid;
  }

  /**
   * 从已经刷入磁盘的文件里读取editslog，同时缓存到内存
   *
   * @param flushedTxid
   * @param fetchedEditsLog
   */
  private void fetchFromFlushedFile(String flushedTxid, JSONArray fetchedEditsLog) {
    try {
      String[] flushedTxidSplited = flushedTxid.split("_");
      long startTxid = Long.parseLong(flushedTxidSplited[0]);
      long endTxid = Long.parseLong(flushedTxidSplited[1]);

      String currentEditsLogFile = "logs/edits-" + startTxid + "-" + endTxid + ".log";

      List<String> editsLogs = Files.readAllLines(Paths.get(currentEditsLogFile),
          StandardCharsets.UTF_8);

      currentBufferedEditsLog.clear();
      for (String editsLog : editsLogs) {
        currentBufferedEditsLog.add(JSONObject.parseObject(editsLog));
      }
      //设置当前内存里缓冲的磁盘文件的索引数据
      bufferedFlushedTxid = flushedTxid;

      fetchFromCurrentBuffer(fetchedEditsLog);

    } catch (Exception e) {
      log.error("fetchFromFlushedFile is error:", e);
    }
  }

  /**
   * 获取下一个磁盘文件对应的txid范围
   *
   * @param flushedTxids
   * @param bufferedFlushedTxid
   * @return
   */
  private String getNextFlushedTxid(List<String> flushedTxids, String bufferedFlushedTxid) {
    for (int i = 0; i < flushedTxids.size(); i++) {
      if (flushedTxids.get(i).equals(bufferedFlushedTxid)) {
        if (i + 1 < flushedTxids.size()) {
          return flushedTxids.get(i + 1);
        }
      }
    }
    return null;
  }
}
