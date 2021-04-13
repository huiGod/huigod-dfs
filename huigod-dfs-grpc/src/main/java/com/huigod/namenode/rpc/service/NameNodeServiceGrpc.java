package com.huigod.namenode.rpc.service;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
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
  }

  private static final int METHODID_REGISTER = 0;
  private static final int METHODID_HEARTBEAT = 1;
  private static final int METHODID_MKDIR = 2;

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
              .build();
        }
      }
    }
    return result;
  }
}
