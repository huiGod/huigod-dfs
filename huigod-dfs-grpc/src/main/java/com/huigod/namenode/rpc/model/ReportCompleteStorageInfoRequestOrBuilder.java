// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.huigod.namenode.rpc.model;

public interface ReportCompleteStorageInfoRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.huigod.namenode.rpc.ReportCompleteStorageInfoRequest)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string ip = 1;</code>
   * @return The ip.
   */
  String getIp();
  /**
   * <code>string ip = 1;</code>
   * @return The bytes for ip.
   */
  com.google.protobuf.ByteString
      getIpBytes();

  /**
   * <code>string hostName = 2;</code>
   * @return The hostName.
   */
  String getHostName();
  /**
   * <code>string hostName = 2;</code>
   * @return The bytes for hostName.
   */
  com.google.protobuf.ByteString
      getHostNameBytes();

  /**
   * <code>string filenames = 3;</code>
   * @return The filenames.
   */
  String getFilenames();
  /**
   * <code>string filenames = 3;</code>
   * @return The bytes for filenames.
   */
  com.google.protobuf.ByteString
      getFilenamesBytes();

  /**
   * <code>int64 StoredDataSize = 4;</code>
   * @return The storedDataSize.
   */
  long getStoredDataSize();

  /**
   * <code>int64 fileLength = 5;</code>
   * @return The fileLength.
   */
  long getFileLength();
}