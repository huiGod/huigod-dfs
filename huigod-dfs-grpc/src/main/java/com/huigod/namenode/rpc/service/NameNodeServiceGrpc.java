package com.huigod.namenode.rpc.service;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.17.1)",
    comments = "Source: NameNodeRpcServer.proto")
public final class NameNodeServiceGrpc {

  private NameNodeServiceGrpc() {}

  public static final String SERVICE_NAME = "com.huigod.namenode.rpc.NameNodeService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.RegisterRequest,
      com.huigod.namenode.rpc.model.RegisterResponse> getRegisterMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "register",
      requestType = com.huigod.namenode.rpc.model.RegisterRequest.class,
      responseType = com.huigod.namenode.rpc.model.RegisterResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.RegisterRequest,
      com.huigod.namenode.rpc.model.RegisterResponse> getRegisterMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.RegisterRequest, com.huigod.namenode.rpc.model.RegisterResponse> getRegisterMethod;
    if ((getRegisterMethod = NameNodeServiceGrpc.getRegisterMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getRegisterMethod = NameNodeServiceGrpc.getRegisterMethod) == null) {
          NameNodeServiceGrpc.getRegisterMethod = getRegisterMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.RegisterRequest, com.huigod.namenode.rpc.model.RegisterResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "register"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.RegisterRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.RegisterResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("register"))
                  .build();
          }
        }
     }
     return getRegisterMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.HeartbeatRequest,
      com.huigod.namenode.rpc.model.HeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "heartbeat",
      requestType = com.huigod.namenode.rpc.model.HeartbeatRequest.class,
      responseType = com.huigod.namenode.rpc.model.HeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.HeartbeatRequest,
      com.huigod.namenode.rpc.model.HeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.HeartbeatRequest, com.huigod.namenode.rpc.model.HeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = NameNodeServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getHeartbeatMethod = NameNodeServiceGrpc.getHeartbeatMethod) == null) {
          NameNodeServiceGrpc.getHeartbeatMethod = getHeartbeatMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.HeartbeatRequest, com.huigod.namenode.rpc.model.HeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.HeartbeatResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("heartbeat"))
                  .build();
          }
        }
     }
     return getHeartbeatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.MkdirRequest,
      com.huigod.namenode.rpc.model.MkdirResponse> getMkdirMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "mkdir",
      requestType = com.huigod.namenode.rpc.model.MkdirRequest.class,
      responseType = com.huigod.namenode.rpc.model.MkdirResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.MkdirRequest,
      com.huigod.namenode.rpc.model.MkdirResponse> getMkdirMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.MkdirRequest, com.huigod.namenode.rpc.model.MkdirResponse> getMkdirMethod;
    if ((getMkdirMethod = NameNodeServiceGrpc.getMkdirMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getMkdirMethod = NameNodeServiceGrpc.getMkdirMethod) == null) {
          NameNodeServiceGrpc.getMkdirMethod = getMkdirMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.MkdirRequest, com.huigod.namenode.rpc.model.MkdirResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "mkdir"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.MkdirRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.MkdirResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("mkdir"))
                  .build();
          }
        }
     }
     return getMkdirMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.ShutdownRequest,
      com.huigod.namenode.rpc.model.ShutdownResponse> getShutdownMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "shutdown",
      requestType = com.huigod.namenode.rpc.model.ShutdownRequest.class,
      responseType = com.huigod.namenode.rpc.model.ShutdownResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.ShutdownRequest,
      com.huigod.namenode.rpc.model.ShutdownResponse> getShutdownMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.ShutdownRequest, com.huigod.namenode.rpc.model.ShutdownResponse> getShutdownMethod;
    if ((getShutdownMethod = NameNodeServiceGrpc.getShutdownMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getShutdownMethod = NameNodeServiceGrpc.getShutdownMethod) == null) {
          NameNodeServiceGrpc.getShutdownMethod = getShutdownMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.ShutdownRequest, com.huigod.namenode.rpc.model.ShutdownResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "shutdown"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.ShutdownRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.ShutdownResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("shutdown"))
                  .build();
          }
        }
     }
     return getShutdownMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.FetchEditsLogRequest,
      com.huigod.namenode.rpc.model.FetchEditsLogResponse> getFetchEditsLogMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "fetchEditsLog",
      requestType = com.huigod.namenode.rpc.model.FetchEditsLogRequest.class,
      responseType = com.huigod.namenode.rpc.model.FetchEditsLogResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.FetchEditsLogRequest,
      com.huigod.namenode.rpc.model.FetchEditsLogResponse> getFetchEditsLogMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.FetchEditsLogRequest, com.huigod.namenode.rpc.model.FetchEditsLogResponse> getFetchEditsLogMethod;
    if ((getFetchEditsLogMethod = NameNodeServiceGrpc.getFetchEditsLogMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getFetchEditsLogMethod = NameNodeServiceGrpc.getFetchEditsLogMethod) == null) {
          NameNodeServiceGrpc.getFetchEditsLogMethod = getFetchEditsLogMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.FetchEditsLogRequest, com.huigod.namenode.rpc.model.FetchEditsLogResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "fetchEditsLog"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.FetchEditsLogRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.FetchEditsLogResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("fetchEditsLog"))
                  .build();
          }
        }
     }
     return getFetchEditsLogMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest,
      com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse> getUpdateCheckpointTxidMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updateCheckpointTxid",
      requestType = com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest.class,
      responseType = com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest,
      com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse> getUpdateCheckpointTxidMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest, com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse> getUpdateCheckpointTxidMethod;
    if ((getUpdateCheckpointTxidMethod = NameNodeServiceGrpc.getUpdateCheckpointTxidMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getUpdateCheckpointTxidMethod = NameNodeServiceGrpc.getUpdateCheckpointTxidMethod) == null) {
          NameNodeServiceGrpc.getUpdateCheckpointTxidMethod = getUpdateCheckpointTxidMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest, com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "updateCheckpointTxid"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("updateCheckpointTxid"))
                  .build();
          }
        }
     }
     return getUpdateCheckpointTxidMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.CreateFileRequest,
      com.huigod.namenode.rpc.model.CreateFileResponse> getCreateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "create",
      requestType = com.huigod.namenode.rpc.model.CreateFileRequest.class,
      responseType = com.huigod.namenode.rpc.model.CreateFileResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.CreateFileRequest,
      com.huigod.namenode.rpc.model.CreateFileResponse> getCreateMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.CreateFileRequest, com.huigod.namenode.rpc.model.CreateFileResponse> getCreateMethod;
    if ((getCreateMethod = NameNodeServiceGrpc.getCreateMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getCreateMethod = NameNodeServiceGrpc.getCreateMethod) == null) {
          NameNodeServiceGrpc.getCreateMethod = getCreateMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.CreateFileRequest, com.huigod.namenode.rpc.model.CreateFileResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "create"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.CreateFileRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.CreateFileResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("create"))
                  .build();
          }
        }
     }
     return getCreateMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.AllocateDataNodesRequest,
      com.huigod.namenode.rpc.model.AllocateDataNodesResponse> getAllocateDataNodesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "allocateDataNodes",
      requestType = com.huigod.namenode.rpc.model.AllocateDataNodesRequest.class,
      responseType = com.huigod.namenode.rpc.model.AllocateDataNodesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.AllocateDataNodesRequest,
      com.huigod.namenode.rpc.model.AllocateDataNodesResponse> getAllocateDataNodesMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.AllocateDataNodesRequest, com.huigod.namenode.rpc.model.AllocateDataNodesResponse> getAllocateDataNodesMethod;
    if ((getAllocateDataNodesMethod = NameNodeServiceGrpc.getAllocateDataNodesMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getAllocateDataNodesMethod = NameNodeServiceGrpc.getAllocateDataNodesMethod) == null) {
          NameNodeServiceGrpc.getAllocateDataNodesMethod = getAllocateDataNodesMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.AllocateDataNodesRequest, com.huigod.namenode.rpc.model.AllocateDataNodesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "allocateDataNodes"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.AllocateDataNodesRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.AllocateDataNodesResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("allocateDataNodes"))
                  .build();
          }
        }
     }
     return getAllocateDataNodesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest,
      com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse> getReportCompleteStorageInfoMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "reportCompleteStorageInfo",
      requestType = com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest.class,
      responseType = com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest,
      com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse> getReportCompleteStorageInfoMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest, com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse> getReportCompleteStorageInfoMethod;
    if ((getReportCompleteStorageInfoMethod = NameNodeServiceGrpc.getReportCompleteStorageInfoMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getReportCompleteStorageInfoMethod = NameNodeServiceGrpc.getReportCompleteStorageInfoMethod) == null) {
          NameNodeServiceGrpc.getReportCompleteStorageInfoMethod = getReportCompleteStorageInfoMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest, com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "reportCompleteStorageInfo"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("reportCompleteStorageInfo"))
                  .build();
          }
        }
     }
     return getReportCompleteStorageInfoMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.InformReplicaReceivedRequest,
      com.huigod.namenode.rpc.model.InformReplicaReceivedResponse> getInformReplicaReceivedMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "informReplicaReceived",
      requestType = com.huigod.namenode.rpc.model.InformReplicaReceivedRequest.class,
      responseType = com.huigod.namenode.rpc.model.InformReplicaReceivedResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.InformReplicaReceivedRequest,
      com.huigod.namenode.rpc.model.InformReplicaReceivedResponse> getInformReplicaReceivedMethod() {
    io.grpc.MethodDescriptor<com.huigod.namenode.rpc.model.InformReplicaReceivedRequest, com.huigod.namenode.rpc.model.InformReplicaReceivedResponse> getInformReplicaReceivedMethod;
    if ((getInformReplicaReceivedMethod = NameNodeServiceGrpc.getInformReplicaReceivedMethod) == null) {
      synchronized (NameNodeServiceGrpc.class) {
        if ((getInformReplicaReceivedMethod = NameNodeServiceGrpc.getInformReplicaReceivedMethod) == null) {
          NameNodeServiceGrpc.getInformReplicaReceivedMethod = getInformReplicaReceivedMethod = 
              io.grpc.MethodDescriptor.<com.huigod.namenode.rpc.model.InformReplicaReceivedRequest, com.huigod.namenode.rpc.model.InformReplicaReceivedResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "com.huigod.namenode.rpc.NameNodeService", "informReplicaReceived"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.InformReplicaReceivedRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.huigod.namenode.rpc.model.InformReplicaReceivedResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new NameNodeServiceMethodDescriptorSupplier("informReplicaReceived"))
                  .build();
          }
        }
     }
     return getInformReplicaReceivedMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static NameNodeServiceStub newStub(io.grpc.Channel channel) {
    return new NameNodeServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static NameNodeServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new NameNodeServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static NameNodeServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new NameNodeServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class NameNodeServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void register(com.huigod.namenode.rpc.model.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.RegisterResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getRegisterMethod(), responseObserver);
    }

    /**
     */
    public void heartbeat(com.huigod.namenode.rpc.model.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.HeartbeatResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }

    /**
     */
    public void mkdir(com.huigod.namenode.rpc.model.MkdirRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.MkdirResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getMkdirMethod(), responseObserver);
    }

    /**
     */
    public void shutdown(com.huigod.namenode.rpc.model.ShutdownRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.ShutdownResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getShutdownMethod(), responseObserver);
    }

    /**
     */
    public void fetchEditsLog(com.huigod.namenode.rpc.model.FetchEditsLogRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.FetchEditsLogResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getFetchEditsLogMethod(), responseObserver);
    }

    /**
     */
    public void updateCheckpointTxid(com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getUpdateCheckpointTxidMethod(), responseObserver);
    }

    /**
     */
    public void create(com.huigod.namenode.rpc.model.CreateFileRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.CreateFileResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateMethod(), responseObserver);
    }

    /**
     */
    public void allocateDataNodes(com.huigod.namenode.rpc.model.AllocateDataNodesRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.AllocateDataNodesResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAllocateDataNodesMethod(), responseObserver);
    }

    /**
     */
    public void reportCompleteStorageInfo(com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getReportCompleteStorageInfoMethod(), responseObserver);
    }

    /**
     */
    public void informReplicaReceived(com.huigod.namenode.rpc.model.InformReplicaReceivedRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.InformReplicaReceivedResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getInformReplicaReceivedMethod(), responseObserver);
    }

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRegisterMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.RegisterRequest,
                com.huigod.namenode.rpc.model.RegisterResponse>(
                  this, METHODID_REGISTER)))
          .addMethod(
            getHeartbeatMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.HeartbeatRequest,
                com.huigod.namenode.rpc.model.HeartbeatResponse>(
                  this, METHODID_HEARTBEAT)))
          .addMethod(
            getMkdirMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.MkdirRequest,
                com.huigod.namenode.rpc.model.MkdirResponse>(
                  this, METHODID_MKDIR)))
          .addMethod(
            getShutdownMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.ShutdownRequest,
                com.huigod.namenode.rpc.model.ShutdownResponse>(
                  this, METHODID_SHUTDOWN)))
          .addMethod(
            getFetchEditsLogMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.FetchEditsLogRequest,
                com.huigod.namenode.rpc.model.FetchEditsLogResponse>(
                  this, METHODID_FETCH_EDITS_LOG)))
          .addMethod(
            getUpdateCheckpointTxidMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest,
                com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse>(
                  this, METHODID_UPDATE_CHECKPOINT_TXID)))
          .addMethod(
            getCreateMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.CreateFileRequest,
                com.huigod.namenode.rpc.model.CreateFileResponse>(
                  this, METHODID_CREATE)))
          .addMethod(
            getAllocateDataNodesMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.AllocateDataNodesRequest,
                com.huigod.namenode.rpc.model.AllocateDataNodesResponse>(
                  this, METHODID_ALLOCATE_DATA_NODES)))
          .addMethod(
            getReportCompleteStorageInfoMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest,
                com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse>(
                  this, METHODID_REPORT_COMPLETE_STORAGE_INFO)))
          .addMethod(
            getInformReplicaReceivedMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                com.huigod.namenode.rpc.model.InformReplicaReceivedRequest,
                com.huigod.namenode.rpc.model.InformReplicaReceivedResponse>(
                  this, METHODID_INFORM_REPLICA_RECEIVED)))
          .build();
    }
  }

  /**
   */
  public static final class NameNodeServiceStub extends io.grpc.stub.AbstractStub<NameNodeServiceStub> {
    private NameNodeServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NameNodeServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NameNodeServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NameNodeServiceStub(channel, callOptions);
    }

    /**
     */
    public void register(com.huigod.namenode.rpc.model.RegisterRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.RegisterResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartbeat(com.huigod.namenode.rpc.model.HeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.HeartbeatResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void mkdir(com.huigod.namenode.rpc.model.MkdirRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.MkdirResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getMkdirMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void shutdown(com.huigod.namenode.rpc.model.ShutdownRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.ShutdownResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getShutdownMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fetchEditsLog(com.huigod.namenode.rpc.model.FetchEditsLogRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.FetchEditsLogResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getFetchEditsLogMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateCheckpointTxid(com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getUpdateCheckpointTxidMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void create(com.huigod.namenode.rpc.model.CreateFileRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.CreateFileResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void allocateDataNodes(com.huigod.namenode.rpc.model.AllocateDataNodesRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.AllocateDataNodesResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAllocateDataNodesMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void reportCompleteStorageInfo(com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getReportCompleteStorageInfoMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void informReplicaReceived(com.huigod.namenode.rpc.model.InformReplicaReceivedRequest request,
        io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.InformReplicaReceivedResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getInformReplicaReceivedMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class NameNodeServiceBlockingStub extends io.grpc.stub.AbstractStub<NameNodeServiceBlockingStub> {
    private NameNodeServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NameNodeServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NameNodeServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NameNodeServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.RegisterResponse register(com.huigod.namenode.rpc.model.RegisterRequest request) {
      return blockingUnaryCall(
          getChannel(), getRegisterMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.HeartbeatResponse heartbeat(com.huigod.namenode.rpc.model.HeartbeatRequest request) {
      return blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.MkdirResponse mkdir(com.huigod.namenode.rpc.model.MkdirRequest request) {
      return blockingUnaryCall(
          getChannel(), getMkdirMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.ShutdownResponse shutdown(com.huigod.namenode.rpc.model.ShutdownRequest request) {
      return blockingUnaryCall(
          getChannel(), getShutdownMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.FetchEditsLogResponse fetchEditsLog(com.huigod.namenode.rpc.model.FetchEditsLogRequest request) {
      return blockingUnaryCall(
          getChannel(), getFetchEditsLogMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse updateCheckpointTxid(com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest request) {
      return blockingUnaryCall(
          getChannel(), getUpdateCheckpointTxidMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.CreateFileResponse create(com.huigod.namenode.rpc.model.CreateFileRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.AllocateDataNodesResponse allocateDataNodes(com.huigod.namenode.rpc.model.AllocateDataNodesRequest request) {
      return blockingUnaryCall(
          getChannel(), getAllocateDataNodesMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse reportCompleteStorageInfo(com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest request) {
      return blockingUnaryCall(
          getChannel(), getReportCompleteStorageInfoMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.huigod.namenode.rpc.model.InformReplicaReceivedResponse informReplicaReceived(com.huigod.namenode.rpc.model.InformReplicaReceivedRequest request) {
      return blockingUnaryCall(
          getChannel(), getInformReplicaReceivedMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class NameNodeServiceFutureStub extends io.grpc.stub.AbstractStub<NameNodeServiceFutureStub> {
    private NameNodeServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private NameNodeServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected NameNodeServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new NameNodeServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.RegisterResponse> register(
        com.huigod.namenode.rpc.model.RegisterRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getRegisterMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.HeartbeatResponse> heartbeat(
        com.huigod.namenode.rpc.model.HeartbeatRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.MkdirResponse> mkdir(
        com.huigod.namenode.rpc.model.MkdirRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getMkdirMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.ShutdownResponse> shutdown(
        com.huigod.namenode.rpc.model.ShutdownRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getShutdownMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.FetchEditsLogResponse> fetchEditsLog(
        com.huigod.namenode.rpc.model.FetchEditsLogRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getFetchEditsLogMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse> updateCheckpointTxid(
        com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getUpdateCheckpointTxidMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.CreateFileResponse> create(
        com.huigod.namenode.rpc.model.CreateFileRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.AllocateDataNodesResponse> allocateDataNodes(
        com.huigod.namenode.rpc.model.AllocateDataNodesRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAllocateDataNodesMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse> reportCompleteStorageInfo(
        com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getReportCompleteStorageInfoMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.huigod.namenode.rpc.model.InformReplicaReceivedResponse> informReplicaReceived(
        com.huigod.namenode.rpc.model.InformReplicaReceivedRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getInformReplicaReceivedMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER = 0;
  private static final int METHODID_HEARTBEAT = 1;
  private static final int METHODID_MKDIR = 2;
  private static final int METHODID_SHUTDOWN = 3;
  private static final int METHODID_FETCH_EDITS_LOG = 4;
  private static final int METHODID_UPDATE_CHECKPOINT_TXID = 5;
  private static final int METHODID_CREATE = 6;
  private static final int METHODID_ALLOCATE_DATA_NODES = 7;
  private static final int METHODID_REPORT_COMPLETE_STORAGE_INFO = 8;
  private static final int METHODID_INFORM_REPLICA_RECEIVED = 9;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final NameNodeServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(NameNodeServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REGISTER:
          serviceImpl.register((com.huigod.namenode.rpc.model.RegisterRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.RegisterResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((com.huigod.namenode.rpc.model.HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.HeartbeatResponse>) responseObserver);
          break;
        case METHODID_MKDIR:
          serviceImpl.mkdir((com.huigod.namenode.rpc.model.MkdirRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.MkdirResponse>) responseObserver);
          break;
        case METHODID_SHUTDOWN:
          serviceImpl.shutdown((com.huigod.namenode.rpc.model.ShutdownRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.ShutdownResponse>) responseObserver);
          break;
        case METHODID_FETCH_EDITS_LOG:
          serviceImpl.fetchEditsLog((com.huigod.namenode.rpc.model.FetchEditsLogRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.FetchEditsLogResponse>) responseObserver);
          break;
        case METHODID_UPDATE_CHECKPOINT_TXID:
          serviceImpl.updateCheckpointTxid((com.huigod.namenode.rpc.model.UpdateCheckpointTxidRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.UpdateCheckpointTxidResponse>) responseObserver);
          break;
        case METHODID_CREATE:
          serviceImpl.create((com.huigod.namenode.rpc.model.CreateFileRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.CreateFileResponse>) responseObserver);
          break;
        case METHODID_ALLOCATE_DATA_NODES:
          serviceImpl.allocateDataNodes((com.huigod.namenode.rpc.model.AllocateDataNodesRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.AllocateDataNodesResponse>) responseObserver);
          break;
        case METHODID_REPORT_COMPLETE_STORAGE_INFO:
          serviceImpl.reportCompleteStorageInfo((com.huigod.namenode.rpc.model.ReportCompleteStorageInfoRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.ReportCompleteStorageInfoResponse>) responseObserver);
          break;
        case METHODID_INFORM_REPLICA_RECEIVED:
          serviceImpl.informReplicaReceived((com.huigod.namenode.rpc.model.InformReplicaReceivedRequest) request,
              (io.grpc.stub.StreamObserver<com.huigod.namenode.rpc.model.InformReplicaReceivedResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class NameNodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    NameNodeServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return NameNodeServer.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("NameNodeService");
    }
  }

  private static final class NameNodeServiceFileDescriptorSupplier
      extends NameNodeServiceBaseDescriptorSupplier {
    NameNodeServiceFileDescriptorSupplier() {}
  }

  private static final class NameNodeServiceMethodDescriptorSupplier
      extends NameNodeServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    NameNodeServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (NameNodeServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new NameNodeServiceFileDescriptorSupplier())
              .addMethod(getRegisterMethod())
              .addMethod(getHeartbeatMethod())
              .addMethod(getMkdirMethod())
              .addMethod(getShutdownMethod())
              .addMethod(getFetchEditsLogMethod())
              .addMethod(getUpdateCheckpointTxidMethod())
              .addMethod(getCreateMethod())
              .addMethod(getAllocateDataNodesMethod())
              .addMethod(getReportCompleteStorageInfoMethod())
              .addMethod(getInformReplicaReceivedMethod())
              .build();
        }
      }
    }
    return result;
  }
}
