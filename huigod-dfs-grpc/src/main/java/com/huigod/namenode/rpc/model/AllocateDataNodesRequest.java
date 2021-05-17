// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.huigod.namenode.rpc.model;

/**
 * Protobuf type {@code com.huigod.namenode.rpc.AllocateDataNodesRequest}
 */
public  final class AllocateDataNodesRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.huigod.namenode.rpc.AllocateDataNodesRequest)
    AllocateDataNodesRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use AllocateDataNodesRequest.newBuilder() to construct.
  private AllocateDataNodesRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private AllocateDataNodesRequest() {
    fileName_ = "";
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new AllocateDataNodesRequest();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private AllocateDataNodesRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            String s = input.readStringRequireUtf8();

            fileName_ = s;
            break;
          }
          case 16: {

            fileSize_ = input.readInt64();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_AllocateDataNodesRequest_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_AllocateDataNodesRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            AllocateDataNodesRequest.class, Builder.class);
  }

  public static final int FILENAME_FIELD_NUMBER = 1;
  private volatile Object fileName_;
  /**
   * <code>string fileName = 1;</code>
   * @return The fileName.
   */
  public String getFileName() {
    Object ref = fileName_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      fileName_ = s;
      return s;
    }
  }
  /**
   * <code>string fileName = 1;</code>
   * @return The bytes for fileName.
   */
  public com.google.protobuf.ByteString
      getFileNameBytes() {
    Object ref = fileName_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      fileName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int FILESIZE_FIELD_NUMBER = 2;
  private long fileSize_;
  /**
   * <code>int64 fileSize = 2;</code>
   * @return The fileSize.
   */
  public long getFileSize() {
    return fileSize_;
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getFileNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, fileName_);
    }
    if (fileSize_ != 0L) {
      output.writeInt64(2, fileSize_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getFileNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, fileName_);
    }
    if (fileSize_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(2, fileSize_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof AllocateDataNodesRequest)) {
      return super.equals(obj);
    }
    AllocateDataNodesRequest other = (AllocateDataNodesRequest) obj;

    if (!getFileName()
        .equals(other.getFileName())) return false;
    if (getFileSize()
        != other.getFileSize()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + FILENAME_FIELD_NUMBER;
    hash = (53 * hash) + getFileName().hashCode();
    hash = (37 * hash) + FILESIZE_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getFileSize());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static AllocateDataNodesRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static AllocateDataNodesRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static AllocateDataNodesRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static AllocateDataNodesRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static AllocateDataNodesRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static AllocateDataNodesRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static AllocateDataNodesRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static AllocateDataNodesRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static AllocateDataNodesRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static AllocateDataNodesRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static AllocateDataNodesRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static AllocateDataNodesRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(AllocateDataNodesRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.huigod.namenode.rpc.AllocateDataNodesRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.huigod.namenode.rpc.AllocateDataNodesRequest)
      AllocateDataNodesRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_AllocateDataNodesRequest_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_AllocateDataNodesRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              AllocateDataNodesRequest.class, Builder.class);
    }

    // Construct using com.huigod.namenode.rpc.model.AllocateDataNodesRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      fileName_ = "";

      fileSize_ = 0L;

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_AllocateDataNodesRequest_descriptor;
    }

    @Override
    public AllocateDataNodesRequest getDefaultInstanceForType() {
      return AllocateDataNodesRequest.getDefaultInstance();
    }

    @Override
    public AllocateDataNodesRequest build() {
      AllocateDataNodesRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public AllocateDataNodesRequest buildPartial() {
      AllocateDataNodesRequest result = new AllocateDataNodesRequest(this);
      result.fileName_ = fileName_;
      result.fileSize_ = fileSize_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof AllocateDataNodesRequest) {
        return mergeFrom((AllocateDataNodesRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(AllocateDataNodesRequest other) {
      if (other == AllocateDataNodesRequest.getDefaultInstance()) return this;
      if (!other.getFileName().isEmpty()) {
        fileName_ = other.fileName_;
        onChanged();
      }
      if (other.getFileSize() != 0L) {
        setFileSize(other.getFileSize());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      AllocateDataNodesRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (AllocateDataNodesRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private Object fileName_ = "";
    /**
     * <code>string fileName = 1;</code>
     * @return The fileName.
     */
    public String getFileName() {
      Object ref = fileName_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        fileName_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string fileName = 1;</code>
     * @return The bytes for fileName.
     */
    public com.google.protobuf.ByteString
        getFileNameBytes() {
      Object ref = fileName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        fileName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string fileName = 1;</code>
     * @param value The fileName to set.
     * @return This builder for chaining.
     */
    public Builder setFileName(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      fileName_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string fileName = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearFileName() {
      
      fileName_ = getDefaultInstance().getFileName();
      onChanged();
      return this;
    }
    /**
     * <code>string fileName = 1;</code>
     * @param value The bytes for fileName to set.
     * @return This builder for chaining.
     */
    public Builder setFileNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      fileName_ = value;
      onChanged();
      return this;
    }

    private long fileSize_ ;
    /**
     * <code>int64 fileSize = 2;</code>
     * @return The fileSize.
     */
    public long getFileSize() {
      return fileSize_;
    }
    /**
     * <code>int64 fileSize = 2;</code>
     * @param value The fileSize to set.
     * @return This builder for chaining.
     */
    public Builder setFileSize(long value) {
      
      fileSize_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 fileSize = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearFileSize() {
      
      fileSize_ = 0L;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.huigod.namenode.rpc.AllocateDataNodesRequest)
  }

  // @@protoc_insertion_point(class_scope:com.huigod.namenode.rpc.AllocateDataNodesRequest)
  private static final AllocateDataNodesRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new AllocateDataNodesRequest();
  }

  public static AllocateDataNodesRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<AllocateDataNodesRequest>
      PARSER = new com.google.protobuf.AbstractParser<AllocateDataNodesRequest>() {
    @Override
    public AllocateDataNodesRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new AllocateDataNodesRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<AllocateDataNodesRequest> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<AllocateDataNodesRequest> getParserForType() {
    return PARSER;
  }

  @Override
  public AllocateDataNodesRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

