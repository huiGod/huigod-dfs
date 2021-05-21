package com.huigod.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端的一个NIOClient，负责跟数据节点进行网络通信
 */
@Slf4j
public class NIOClient {

  public static final Integer SEND_FILE = 1;
  public static final Integer READ_FILE = 2;

  /**
   * 发送文件至dataNode
   */
  public static void sendFile(String hostname, int nioPort,
      byte[] file, String filename, long fileSize) {
    // 建立一个短连接，发送完一个文件就释放网络连接
    SocketChannel channel = null;
    Selector selector = null;
    ByteBuffer buffer = null;

    try {
      selector = Selector.open();

      channel = SocketChannel.open();
      channel.configureBlocking(false);
      channel.connect(new InetSocketAddress(hostname, nioPort));

      channel.register(selector, SelectionKey.OP_CONNECT);

      boolean sending = true;

      while (sending) {
        // 同步的模式，NIO同步非阻塞
        // Selector在底层监听多个SocketChannel的时候是采取的是非阻塞的方式
        // 同步的方式，对于调用Selector的线程而言，必须在这里
        // 需要进行同步等待的模式，等待说，必须得得等到有某个SocketChannel有事件处理

        // BIO，同步阻塞，一个线程必须阻塞在一个Socket上去等待里面的请求接收或者是等待发送响应出去
        // 而且因为阻塞，必然是同步的，同步等待，阻塞在一个Socket上看是否有请求或者响应
        // 所以必须是每个连接都有一个独立的线程去维护

        // AIO，NIO2，异步非阻塞，也就是发起一个请求，去看某个连接是否有请求或者响应
        // 接着就不管了，提供一个回调函数给AIO接口即可
        // 后续在一个连接上收到了请求或者响应之后，会回调对应的回调函数即可
        selector.select();

        // 在NIO技术体系里，有一个核心的概念，叫做SelectionKey
        // 你大概可以认为每个SocketChannel就对应了一个SelectionKey，就对应了一个Socket连接
        // 一个Socket连接就对应了底层的TCP连接，封装数据包和传输过去的
        Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
        while (keysIterator.hasNext()) {
          SelectionKey key = keysIterator.next();

          //如果不调用remove操作，会导致这个SelectionKey继续留存在Set集合里
          keysIterator.remove();

          // NIOServer允许进行连接的话
          // 调用到isConnectable的时候有三种情况
          // 第一种情况：就是连接已经建立成功了，此时在下面不会有任何的阻塞直接返回true
          // 第二种情况：连接建立失败了，此时在下面会抛出IOException异常
          // 第三种情况：连接还没彻底成功，在进行中
          if (key.isConnectable()) {
            channel = (SocketChannel) key.channel();

            if (channel.isConnectionPending()) {
              // 三次握手做完，TCP连接建立好
              while (!channel.finishConnect()) {
                Thread.sleep(100);
              }
            }

            log.info("完成与服务端的连接的建立......");

            buffer = ByteBuffer.allocate(4 + 4 + filename.getBytes().length + 8 + file.length);

            log.info("准备发送的数据包大小为：" + buffer.capacity());

            //封装数据报文二进制
            buffer.putInt(SEND_FILE);
            buffer.putInt(filename.getBytes().length);
            buffer.put(filename.getBytes(StandardCharsets.UTF_8));
            buffer.putLong(fileSize);
            buffer.put(file);
            buffer.flip();

            //此时的写可能出现拆包的问题
            int sent = channel.write(buffer);
            log.info("已经发送了" + sent + "字节的数据到" + hostname);

            if (buffer.hasRemaining()) {
              log.info("本次数据包没有发送完毕，下次会继续发送.......");
              key.interestOps(SelectionKey.OP_WRITE);
            } else {
              log.info("本次数据包发送完毕，准备读取服务端的响应......");
              key.interestOps(SelectionKey.OP_READ);
            }

          }
          // 对于大数据包的拆包，再次尝试发送数据出去
          else if (key.isWritable()) {
            channel = (SocketChannel) key.channel();
            int sent = channel.write(buffer);
            log.info("上一次数据包没有发送完毕，本次继续发送了" + sent + " bytes");
            if (!buffer.hasRemaining()) {
              log.info("本次数据包发送完毕");
              key.interestOps(SelectionKey.OP_READ);
            }
          }
          // 接收到NIOServer的响应
          else if (key.isReadable()) {
            channel = (SocketChannel) key.channel();

            buffer = ByteBuffer.allocate(1024);
            int len = channel.read(buffer);
            buffer.flip();
            if (len > 0) {
              log.info("[" + Thread.currentThread().getName()
                  + "]收到" + hostname + "的响应：" + new String(buffer.array(), 0, len));
              //连接发送完数据，并且接收到响应后断开连接
              sending = false;
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (channel != null) {
        try {
          channel.close();
        } catch (Exception e) {
          log.error("sendFile is error:", e);
        }
      }

      if (selector != null) {
        try {
          selector.close();
        } catch (Exception e) {
          log.error("sendFile close is error:", e);
        }
      }
    }
  }

  /**
   * 读取文件
   *
   * @param hostname 数据节点的hostname
   * @param nioPort  数据节点的nio端口号
   * @param filename 文件名
   */
  public byte[] readFile(String hostname, int nioPort, String filename) {

    ByteBuffer fileLengthBuffer = null;
    Long fileLength = null;
    ByteBuffer fileBuffer = null;

    byte[] file = null;

    SocketChannel channel = null;
    Selector selector = null;

    try {
      channel = SocketChannel.open();
      channel.configureBlocking(false);
      channel.connect(new InetSocketAddress(hostname, nioPort));

      selector = Selector.open();
      channel.register(selector, SelectionKey.OP_CONNECT);

      boolean reading = true;

      while (reading) {
        selector.select();

        Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
        while (keysIterator.hasNext()) {
          SelectionKey key = keysIterator.next();
          keysIterator.remove();

          // NIOServer允许进行连接的话
          if (key.isConnectable()) {
            channel = (SocketChannel) key.channel();
            if (channel.isConnectionPending()) {
              //完成三次握手创建好连接
              channel.finishConnect();
            }

            //如果创建好连接，则直接发送读取文件请求，请求的文件名也需要用约定好的二进制协议
            //请求发送完后，需要立即关注OP_READ事件，来获取服务端返回的文件数据

            byte[] filenameBytes = filename.getBytes(StandardCharsets.UTF_8);

            //封装二进制协议
            ByteBuffer readFileRequest = ByteBuffer.allocate(4 + 4 + filenameBytes.length);
            readFileRequest.putInt(READ_FILE);
            readFileRequest.putInt(filenameBytes.length);
            readFileRequest.put(filenameBytes);
            readFileRequest.flip();

            //可能拆包，省略处理
            channel.write(readFileRequest);
            log.info("发送文件下载的请求过去......");
            key.interestOps(SelectionKey.OP_READ);
          } else if (key.isReadable()) {
            //接收到NIOServer的响应
            channel = (SocketChannel) key.channel();
            if (fileLength == null) {
              if (fileLengthBuffer == null) {
                fileLengthBuffer = ByteBuffer.allocate(8);
              }
              channel.read(fileLengthBuffer);
              if (!fileLengthBuffer.hasRemaining()) {
                fileLengthBuffer.rewind();
                fileLength = fileLengthBuffer.getLong();
                log.info("从服务端返回数据中解析文件大小：" + fileLength);
              }
            }

            if (fileLength != null) {
              if (fileBuffer == null) {
                fileBuffer = ByteBuffer.allocate(
                    Integer.parseInt(String.valueOf(fileLength)));
              }

              int hasRead = channel.read(fileBuffer);
              log.info("从服务端读取了" + hasRead + " bytes的数据出来到内存中");

              if (!fileBuffer.hasRemaining()) {
                fileBuffer.rewind();
                file = fileBuffer.array();
                log.info("最终获取到的文件的大小为" + file.length + " bytes");
                reading = false;
              }
            }
          }
        }
      }
      return file;
    } catch (Exception e) {
      log.error("readFile is error:", e);
    } finally {
      if (channel != null) {
        try {
          channel.close();
        } catch (Exception e) {
          log.error("readFile close error:", e);
        }
      }

      if (selector != null) {
        try {
          selector.close();
        } catch (Exception e) {
          log.error("readFile close error:", e);
        }
      }
    }
    return null;
  }
}
