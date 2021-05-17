package com.huigod.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huigod.entity.Command;
import com.huigod.entity.DataNodeInfo;
import com.huigod.manager.DataNodeManager;
import com.huigod.manager.FSNameSystem;
import com.huigod.namenode.rpc.model.AllocateDataNodesRequest;
import com.huigod.namenode.rpc.model.AllocateDataNodesResponse;
import com.huigod.namenode.rpc.model.CreateFileRequest;
import com.huigod.namenode.rpc.model.CreateFileResponse;
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
import com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest;
import com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse;
import com.huigod.namenode.rpc.service.NameNodeServiceGrpc;
import io.grpc.stub.StreamObserver;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
  public static final Integer STATUS_DUPLICATE = 4;

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
   * 当前缓存里的editslog最大的一个txid
   */
  private long currentBufferedMaxTxid = 0L;


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
    Boolean result = datanodeManager.register(request.getIp(), request.getHostName(),request.getNioPort());

    RegisterResponse response;

    if(result) {
      response = RegisterResponse.newBuilder()
          .setStatus(STATUS_SUCCESS)
          .build();
    } else {
      response = RegisterResponse.newBuilder()
          .setStatus(STATUS_FAILURE)
          .build();
    }

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
    Boolean result = datanodeManager.heartbeat(request.getIp(), request.getHostName());

    HeartbeatResponse response = null;
    List<Command> commands = new ArrayList<>();

    //心跳成功直接返回
    if(result) {
      response = HeartbeatResponse.newBuilder()
          .setStatus(STATUS_SUCCESS)
          .setCommands(JSONArray.toJSONString(commands))
          .build();
    } else {
      //心跳失败，发送重新注册和上报全量副本元数据命令
      Command registerCommand = new Command(Command.REGISTER);
      Command reportCompleteStorageInfoCommand = new Command(
          Command.REPORT_COMPLETE_STORAGE_INFO);

      commands.add(registerCommand);
      commands.add(reportCompleteStorageInfoCommand);

      response = HeartbeatResponse.newBuilder()
          .setStatus(STATUS_FAILURE)
          .setCommands(JSONArray.toJSONString(commands))
          .build();
    }

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
    //核心功能拒绝请求，响应返回异常码
    this.isRunning = false;
    //将内存缓存日志刷入磁盘
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

    if (!isRunning) {
      FetchEditsLogResponse response = FetchEditsLogResponse.newBuilder()
          .setEditsLog(new JSONArray().toJSONString())
          .build();
      responseObserver.onNext(response);
      responseObserver.onCompleted();
      return;
    }

    long syncedTxid = request.getSyncedTxid();

    FetchEditsLogResponse response;
    JSONArray fetchedEditsLog = new JSONArray();

    //获取所有磁盘文件索引数据
    List<String> flushedTxids = nameSystem.getEditsLog().getFlushedTxids();

    //磁盘中没有数据，则editLog都存在于内存中
    if (CollectionUtils.isEmpty(flushedTxids)) {
      log.debug("暂时没有任何磁盘文件，直接从内存缓冲中拉取editsLog......");
      fetchFromBufferedEditsLog(syncedTxid, fetchedEditsLog);
    } else {
      //此时NameNode已经有数据刷到磁盘文件，需要扫描磁盘文件的索引范围

      //之前已经从磁盘读取过数据
      if (bufferedFlushedTxid != null) {
        // 如果fetch的txid就在当前内存缓存中，则直接读取内存数据即可
        if (existInFlushedFile(syncedTxid, bufferedFlushedTxid)) {
          log.info("上一次已经缓存过磁盘文件的数据，直接从磁盘文件缓存中拉取editsLog......");
          fetchFromCurrentBuffer(syncedTxid, fetchedEditsLog);
        } else {
          //否则从下一个磁盘文件去获取
          String nextFlushedTxid = getNextFlushedTxid(flushedTxids, bufferedFlushedTxid);
          // 如果可以找到下一个磁盘文件，那么就从下一个磁盘文件里开始读取数据
          if (nextFlushedTxid != null) {
            log.info("上一次缓存的磁盘文件找不到要拉取的数据，从下一个磁盘文件中拉取editslog......");
            fetchFromFlushedFile(syncedTxid, nextFlushedTxid, fetchedEditsLog);
          }
          // 如果没有找到下一个文件，此时就需要从内存里去继续读取
          else {
            log.info("上一次缓存的磁盘文件找不到要拉取的数据，而且没有下一个磁盘文件，尝试从内存缓冲中拉取editslog......");
            fetchFromBufferedEditsLog(syncedTxid, fetchedEditsLog);
          }
        }
      } else {
        //第一次从磁盘获取数据

        //遍历所有的磁盘文件的索引范围，0-390，391-782
        boolean fechedFromFlushedFile = false;
        for (String flushedTxid : flushedTxids) {
          // 如果要fetch的txid就在当前磁盘文件索引范围内
          if (existInFlushedFile(syncedTxid, flushedTxid)) {
            log.info("尝试从磁盘文件中拉取editslog，flushedTxid={}", flushedTxid);
            //为了避免一个磁盘文件被fetch的数据不够，因此直接缓存当前磁盘文件和下一个磁盘文件的数据到内存
            fetchFromFlushedFile(syncedTxid, flushedTxid, fetchedEditsLog);
            fechedFromFlushedFile = true;
            break;
          }
        }

        //需要fetch的txid比磁盘文件的数据都要新，则直接从内存中获取
        if (!fechedFromFlushedFile) {
          log.debug("所有磁盘文件都没找到要拉取的editslog，尝试直接从内存缓冲中拉取editslog......");
          fetchFromBufferedEditsLog(syncedTxid, fetchedEditsLog);
        }
      }
    }
    response = FetchEditsLogResponse.newBuilder()
        .setEditsLog(fetchedEditsLog.toJSONString())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * 从内存缓冲的editslog中拉取数据
   *
   * @param fetchedEditsLog
   */
  private void fetchFromBufferedEditsLog(long syncedTxid, JSONArray fetchedEditsLog) {

    //需要拉取的txid存在内存中
    long fetchTxid = syncedTxid + 1;
    if (fetchTxid <= currentBufferedMaxTxid) {
      log.info("尝试从内存缓冲拉取的时候，发现上一次内存缓存有数据可供拉取......");
      fetchFromCurrentBuffer(syncedTxid, fetchedEditsLog);
      return;
    }

    currentBufferedEditsLog.clear();

    //从nameNode拉取内存数据
    String[] bufferedEditsLog = nameSystem.getEditsLog().getBufferedEditsLog();

    if (bufferedEditsLog != null && bufferedEditsLog.length > 0) {
      Arrays.stream(bufferedEditsLog)
          .forEach(editLog -> {
            currentBufferedEditsLog.add(JSONObject.parseObject(editLog));
            //记录当前内存缓存中最大txid
            currentBufferedMaxTxid = JSONObject.parseObject(editLog).getLongValue("txid");
          });

      bufferedFlushedTxid = null;

      fetchFromCurrentBuffer(syncedTxid, fetchedEditsLog);
    }
  }

  /**
   * 从当前已经在内存里缓存的数据中拉取editslog
   */
  private void fetchFromCurrentBuffer(long syncedTxid, JSONArray fetchedEditsLog) {
    int fetchCount = 0;

    long fetchTxid = syncedTxid + 1;

    for (int i = 0; i < currentBufferedEditsLog.size(); i++) {
      if (currentBufferedEditsLog.getJSONObject(i).getLong("txid") == fetchTxid) {
        fetchedEditsLog.add(currentBufferedEditsLog.getJSONObject(i));
        fetchTxid = currentBufferedEditsLog.getJSONObject(i).getLong("txid") + 1;
        fetchCount++;
      }
      if (fetchCount == BACKUP_NODE_FETCH_SIZE) {
        break;
      }
    }
  }

  /**
   * 判断需要fetch的txid是否在当前磁盘索引中
   */
  private boolean existInFlushedFile(long syncedTxid, String flushedTxid) {
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
  private void fetchFromFlushedFile(long syncedTxid, String flushedTxid,
      JSONArray fetchedEditsLog) {
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
        currentBufferedMaxTxid = JSONObject.parseObject(editsLog).getLongValue("txid");
      }
      //设置当前内存里缓冲的磁盘文件的索引数据
      bufferedFlushedTxid = flushedTxid;

      fetchFromCurrentBuffer(syncedTxid, fetchedEditsLog);

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

  /**
   * 更新checkpoint txid
   */
  @Override
  public void updateCheckpointTxid(UpdateCheckpointTxidRequest request,
      StreamObserver<UpdateCheckpointTxidResponse> responseObserver) {
    long txid = request.getTxid();
    nameSystem.setCheckpointTxid(txid);

    UpdateCheckpointTxidResponse response = UpdateCheckpointTxidResponse.newBuilder()
        .setStatus(1)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  /**
   * 创建文件
   *
   * @param request
   * @param responseObserver
   */
  @Override
  public void create(CreateFileRequest request,
      StreamObserver<CreateFileResponse> responseObserver) {
    CreateFileResponse response = null;

    try {
      if (!isRunning) {
        response = CreateFileResponse.newBuilder()
            .setStatus(STATUS_SHUTDOWN)
            .build();
      } else {
        String filename = request.getFilename();
        Boolean success = nameSystem.create(filename);
        if (success) {
          response = CreateFileResponse.newBuilder()
              .setStatus(STATUS_SUCCESS)
              .build();
        } else {
          response = CreateFileResponse.newBuilder()
              .setStatus(STATUS_DUPLICATE)
              .build();
        }
      }
      responseObserver.onNext(response);
      responseObserver.onCompleted();
    } catch (Exception e) {
      log.error("create file is error:", e);
    }
  }

  /**
   * 为文件上传请求分配多个数据节点来传输多个副本
   * @param request
   * @param responseObserver
   */
  @Override
  public void allocateDataNodes(AllocateDataNodesRequest request,
      StreamObserver<AllocateDataNodesResponse> responseObserver) {
    long fileSize = request.getFileSize();
    List<DataNodeInfo> dataNodes = datanodeManager.allocateDataNodes(fileSize);
    String dataNodesJson = JSONArray.toJSONString(dataNodes);

    AllocateDataNodesResponse response = AllocateDataNodesResponse.newBuilder()
        .setDataNodes(dataNodesJson)
        .build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
