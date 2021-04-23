// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.huigod.namenode.rpc.model;

public final class NameNodeRpcModel {
  private NameNodeRpcModel() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_RegisterRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_RegisterRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_RegisterResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_RegisterResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_HeartbeatRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_HeartbeatRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_HeartbeatResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_HeartbeatResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_MkdirRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_MkdirRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_MkdirResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_MkdirResponse_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_ShutdownRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_ShutdownRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_huigod_namenode_rpc_ShutdownResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_huigod_namenode_rpc_ShutdownResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\026NameNodeRpcModel.proto\022\027com.huigod.nam" +
      "enode.rpc\"/\n\017RegisterRequest\022\n\n\002ip\030\001 \001(\t" +
      "\022\020\n\010hostName\030\002 \001(\t\"\"\n\020RegisterResponse\022\016" +
      "\n\006status\030\001 \001(\005\"0\n\020HeartbeatRequest\022\n\n\002ip" +
      "\030\001 \001(\t\022\020\n\010hostName\030\002 \001(\t\"#\n\021HeartbeatRes" +
      "ponse\022\016\n\006status\030\001 \001(\005\"\034\n\014MkdirRequest\022\014\n" +
      "\004path\030\001 \001(\t\"\037\n\rMkdirResponse\022\016\n\006status\030\001" +
      " \001(\005\"\037\n\017ShutdownRequest\022\014\n\004code\030\001 \001(\005\"\"\n" +
      "\020ShutdownResponse\022\016\n\006status\030\001 \001(\005B3\n\035com" +
      ".huigod.namenode.rpc.modelB\020NameNodeRpcM" +
      "odelP\001b\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_com_huigod_namenode_rpc_RegisterRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_huigod_namenode_rpc_RegisterRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_RegisterRequest_descriptor,
        new String[] { "Ip", "HostName", });
    internal_static_com_huigod_namenode_rpc_RegisterResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_huigod_namenode_rpc_RegisterResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_RegisterResponse_descriptor,
        new String[] { "Status", });
    internal_static_com_huigod_namenode_rpc_HeartbeatRequest_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_com_huigod_namenode_rpc_HeartbeatRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_HeartbeatRequest_descriptor,
        new String[] { "Ip", "HostName", });
    internal_static_com_huigod_namenode_rpc_HeartbeatResponse_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_com_huigod_namenode_rpc_HeartbeatResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_HeartbeatResponse_descriptor,
        new String[] { "Status", });
    internal_static_com_huigod_namenode_rpc_MkdirRequest_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_com_huigod_namenode_rpc_MkdirRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_MkdirRequest_descriptor,
        new String[] { "Path", });
    internal_static_com_huigod_namenode_rpc_MkdirResponse_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_com_huigod_namenode_rpc_MkdirResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_MkdirResponse_descriptor,
        new String[] { "Status", });
    internal_static_com_huigod_namenode_rpc_ShutdownRequest_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_com_huigod_namenode_rpc_ShutdownRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_ShutdownRequest_descriptor,
        new String[] { "Code", });
    internal_static_com_huigod_namenode_rpc_ShutdownResponse_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_com_huigod_namenode_rpc_ShutdownResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_huigod_namenode_rpc_ShutdownResponse_descriptor,
        new String[] { "Status", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
