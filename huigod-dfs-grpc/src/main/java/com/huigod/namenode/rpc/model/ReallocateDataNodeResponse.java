// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: NameNodeRpcModel.proto

package com.huigod.namenode.rpc.model;

/**
 * Protobuf type {@code com.huigod.namenode.rpc.ReallocateDataNodeResponse}
 */
public  final class ReallocateDataNodeResponse extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.huigod.namenode.rpc.ReallocateDataNodeResponse)
    ReallocateDataNodeResponseOrBuilder {
private static final long serialVersionUID = 0L;
  // Use ReallocateDataNodeResponse.newBuilder() to construct.
  private ReallocateDataNodeResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private ReallocateDataNodeResponse() {
    datanode_ = "";
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new ReallocateDataNodeResponse();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private ReallocateDataNodeResponse(
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

            datanode_ = s;
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
    return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_ReallocateDataNodeResponse_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_ReallocateDataNodeResponse_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            ReallocateDataNodeResponse.class, Builder.class);
  }

  public static final int DATANODE_FIELD_NUMBER = 1;
  private volatile Object datanode_;
  /**
   * <code>string datanode = 1;</code>
   * @return The datanode.
   */
  public String getDatanode() {
    Object ref = datanode_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      datanode_ = s;
      return s;
    }
  }
  /**
   * <code>string datanode = 1;</code>
   * @return The bytes for datanode.
   */
  public com.google.protobuf.ByteString
      getDatanodeBytes() {
    Object ref = datanode_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      datanode_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
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
    if (!getDatanodeBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, datanode_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getDatanodeBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, datanode_);
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
    if (!(obj instanceof ReallocateDataNodeResponse)) {
      return super.equals(obj);
    }
    ReallocateDataNodeResponse other = (ReallocateDataNodeResponse) obj;

    if (!getDatanode()
        .equals(other.getDatanode())) return false;
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
    hash = (37 * hash) + DATANODE_FIELD_NUMBER;
    hash = (53 * hash) + getDatanode().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static ReallocateDataNodeResponse parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ReallocateDataNodeResponse parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ReallocateDataNodeResponse parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static ReallocateDataNodeResponse parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static ReallocateDataNodeResponse parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ReallocateDataNodeResponse parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static ReallocateDataNodeResponse parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static ReallocateDataNodeResponse parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static ReallocateDataNodeResponse parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static ReallocateDataNodeResponse parseFrom(
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
  public static Builder newBuilder(ReallocateDataNodeResponse prototype) {
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
   * Protobuf type {@code com.huigod.namenode.rpc.ReallocateDataNodeResponse}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.huigod.namenode.rpc.ReallocateDataNodeResponse)
      ReallocateDataNodeResponseOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_ReallocateDataNodeResponse_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_ReallocateDataNodeResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              ReallocateDataNodeResponse.class, Builder.class);
    }

    // Construct using com.huigod.namenode.rpc.model.ReallocateDataNodeResponse.newBuilder()
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
      datanode_ = "";

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return NameNodeRpcModel.internal_static_com_huigod_namenode_rpc_ReallocateDataNodeResponse_descriptor;
    }

    @Override
    public ReallocateDataNodeResponse getDefaultInstanceForType() {
      return ReallocateDataNodeResponse.getDefaultInstance();
    }

    @Override
    public ReallocateDataNodeResponse build() {
      ReallocateDataNodeResponse result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public ReallocateDataNodeResponse buildPartial() {
      ReallocateDataNodeResponse result = new ReallocateDataNodeResponse(this);
      result.datanode_ = datanode_;
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
      if (other instanceof ReallocateDataNodeResponse) {
        return mergeFrom((ReallocateDataNodeResponse)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(ReallocateDataNodeResponse other) {
      if (other == ReallocateDataNodeResponse.getDefaultInstance()) return this;
      if (!other.getDatanode().isEmpty()) {
        datanode_ = other.datanode_;
        onChanged();
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
      ReallocateDataNodeResponse parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (ReallocateDataNodeResponse) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private Object datanode_ = "";
    /**
     * <code>string datanode = 1;</code>
     * @return The datanode.
     */
    public String getDatanode() {
      Object ref = datanode_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        datanode_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string datanode = 1;</code>
     * @return The bytes for datanode.
     */
    public com.google.protobuf.ByteString
        getDatanodeBytes() {
      Object ref = datanode_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        datanode_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string datanode = 1;</code>
     * @param value The datanode to set.
     * @return This builder for chaining.
     */
    public Builder setDatanode(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      datanode_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string datanode = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearDatanode() {
      
      datanode_ = getDefaultInstance().getDatanode();
      onChanged();
      return this;
    }
    /**
     * <code>string datanode = 1;</code>
     * @param value The bytes for datanode to set.
     * @return This builder for chaining.
     */
    public Builder setDatanodeBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      datanode_ = value;
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


    // @@protoc_insertion_point(builder_scope:com.huigod.namenode.rpc.ReallocateDataNodeResponse)
  }

  // @@protoc_insertion_point(class_scope:com.huigod.namenode.rpc.ReallocateDataNodeResponse)
  private static final ReallocateDataNodeResponse DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new ReallocateDataNodeResponse();
  }

  public static ReallocateDataNodeResponse getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<ReallocateDataNodeResponse>
      PARSER = new com.google.protobuf.AbstractParser<ReallocateDataNodeResponse>() {
    @Override
    public ReallocateDataNodeResponse parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new ReallocateDataNodeResponse(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<ReallocateDataNodeResponse> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<ReallocateDataNodeResponse> getParserForType() {
    return PARSER;
  }

  @Override
  public ReallocateDataNodeResponse getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

