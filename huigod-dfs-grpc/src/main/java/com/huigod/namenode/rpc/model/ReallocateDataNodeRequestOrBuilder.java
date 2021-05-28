// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.huigod.namenode.rpc.model;

public interface ReallocateDataNodeRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.huigod.namenode.rpc.ReallocateDataNodeRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>int64 fileSize = 1;</code>
   * @return The fileSize.
   */
  long getFileSize();

  /**
   * <code>string excludedDataNodeId = 2;</code>
   * @return The excludedDataNodeId.
   */
  String getExcludedDataNodeId();
  /**
   * <code>string excludedDataNodeId = 2;</code>
   * @return The bytes for excludedDataNodeId.
   */
  com.google.protobuf.ByteString
      getExcludedDataNodeIdBytes();

  /**
   * <code>string fileName = 3;</code>
   * @return The fileName.
   */
  String getFileName();
  /**
   * <code>string fileName = 3;</code>
   * @return The bytes for fileName.
   */
  com.google.protobuf.ByteString
      getFileNameBytes();
}
