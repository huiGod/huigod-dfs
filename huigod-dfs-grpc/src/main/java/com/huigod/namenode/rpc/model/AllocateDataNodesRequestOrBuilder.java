// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.huigod.namenode.rpc.model;

public interface AllocateDataNodesRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.huigod.namenode.rpc.AllocateDataNodesRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string fileName = 1;</code>
   * @return The fileName.
   */
  String getFileName();
  /**
   * <code>string fileName = 1;</code>
   * @return The bytes for fileName.
   */
  com.google.protobuf.ByteString
      getFileNameBytes();

  /**
   * <code>int64 fileSize = 2;</code>
   * @return The fileSize.
   */
  long getFileSize();
}