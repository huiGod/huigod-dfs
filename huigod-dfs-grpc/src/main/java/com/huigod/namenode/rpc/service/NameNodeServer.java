// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcServer.proto

package com.huigod.namenode.rpc.service;

public final class NameNodeServer {
  private NameNodeServer() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\027NameNodeRpcServer.proto\022\027com.huigod.na" +
      "menode.rpc\032\026NameNodeRpcModel.proto2\377\003\n\017N" +
      "ameNodeService\022_\n\010register\022(.com.huigod." +
      "namenode.rpc.RegisterRequest\032).com.huigo" +
      "d.namenode.rpc.RegisterResponse\022b\n\theart" +
      "beat\022).com.huigod.namenode.rpc.Heartbeat" +
      "Request\032*.com.huigod.namenode.rpc.Heartb" +
      "eatResponse\022V\n\005mkdir\022%.com.huigod.nameno" +
      "de.rpc.MkdirRequest\032&.com.huigod.namenod" +
      "e.rpc.MkdirResponse\022_\n\010shutdown\022(.com.hu" +
      "igod.namenode.rpc.ShutdownRequest\032).com." +
      "huigod.namenode.rpc.ShutdownResponse\022n\n\r" +
      "fetchEditsLog\022-.com.huigod.namenode.rpc." +
      "FetchEditsLogRequest\032..com.huigod.nameno" +
      "de.rpc.FetchEditsLogResponseB3\n\037com.huig" +
      "od.namenode.rpc.serviceB\016NameNodeServerP" +
      "\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.huigod.namenode.rpc.model.NameNodeRpcModel.getDescriptor(),
        });
    com.huigod.namenode.rpc.model.NameNodeRpcModel.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
