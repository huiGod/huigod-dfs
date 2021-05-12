package com.huigod.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责fsimage文件上传的server
 */
@Slf4j
public class FSImageUploadServer extends Thread {

  private Selector selector;

  public FSImageUploadServer() {
    this.init();
  }

  private void init() {
    ServerSocketChannel serverSocketChannel;
    try {
      selector = Selector.open();
      serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.socket().bind(new InetSocketAddress(9000), 100);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    } catch (Exception e) {
      log.error("FSImageUploadServer init is error:", e);
    }
  }

  @Override
  public void run() {
    log.info("FSImageUploadServer启动，监听9000端口......");

    while (true) {
      try {
        selector.select();
        Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

        while (keysIterator.hasNext()) {
          SelectionKey key = keysIterator.next();
          keysIterator.remove();

          try {
            handleRequest(key);
          } catch (Exception e) {
            log.error("handleRequest is error:", e);
          }
        }
      } catch (Exception e) {
        log.error("FSImageUploadServer run is error:", e);
      }
    }
  }

  private void handleRequest(SelectionKey key) throws Exception {
    if (key.isAcceptable()) {
      handleConnectRequest(key);
    } else if (key.isReadable()) {
      handleReadableRequest(key);
    } else if (key.isWritable()) {
      handleWritableRequest(key);
    }
  }

  /**
   * 处理BackupNode连接请求
   *
   * @param key
   */
  private void handleConnectRequest(SelectionKey key) throws Exception {
    SocketChannel socketChannel = null;

    try {
      ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
      socketChannel = serverSocketChannel.accept();

      if (socketChannel != null) {
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
      }
    } catch (Exception e) {
      log.error("handleConnectRequest is error:", e);
      if (socketChannel != null) {
        socketChannel.close();
      }
    }
  }

  /**
   * 处理发送fsimage文件的请求
   *
   * @param key
   */
  private void handleReadableRequest(SelectionKey key) throws Exception {
    SocketChannel channel = null;

    String fsimageFilePath = "backupnode/fsimage.meta";

    RandomAccessFile fsimageImageRAF = null;
    FileOutputStream fsimageOut = null;
    FileChannel fsimageFileChannel = null;

    try {
      channel = (SocketChannel) key.channel();

      ByteBuffer buffer = ByteBuffer.allocate(1024);

      int total = 0;
      int count;

      if ((count = channel.read(buffer)) > 0) {
        File file = new File(fsimageFilePath);
        if (file.exists()) {
          file.delete();
        }

        fsimageImageRAF = new RandomAccessFile(fsimageFilePath, "rw");
        fsimageOut = new FileOutputStream(fsimageImageRAF.getFD());
        fsimageFileChannel = fsimageOut.getChannel();

        total += count;

        buffer.flip();

        fsimageFileChannel.write(buffer);
        buffer.clear();

        while ((count = channel.read(buffer)) > 0) {
          total += count;
          buffer.flip();
          fsimageFileChannel.write(buffer);
          buffer.clear();
        }

        if (total > 0) {
          log.info("接收fsimage文件以及写入本地磁盘完毕......");
          fsimageFileChannel.force(false);
          channel.register(selector, SelectionKey.OP_WRITE);
        }
      } else {
        channel.close();
      }

    } catch (Exception e) {
      log.error("handleReadableRequest is error:", e);
      if (channel != null) {
        channel.close();
      }
    } finally {
      if (fsimageOut != null) {
        fsimageOut.close();
      }
      if (fsimageImageRAF != null) {
        fsimageImageRAF.close();
      }
      if (fsimageFileChannel != null) {
        fsimageFileChannel.close();
      }
    }
  }

  /**
   * 处理返回响应给BackupNode
   *
   * @param key
   */
  private void handleWritableRequest(SelectionKey key) throws Exception {

    SocketChannel channel;
    try {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      buffer.put("SUCCESS".getBytes(StandardCharsets.UTF_8));
      buffer.flip();

      channel = (SocketChannel) key.channel();
      channel.write(buffer);

      log.info("fsimage上传完毕，返回响应SUCCESS给backupnode......");
      channel.register(selector, SelectionKey.OP_READ);
    } catch (Exception e) {
      log.error("handleWritableRequest is error:", e);
    }

  }
}
