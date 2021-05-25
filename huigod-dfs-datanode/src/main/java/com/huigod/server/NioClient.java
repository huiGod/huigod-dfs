package com.huigod.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

/**
 * 客户端的一个NIOClient，负责跟数据节点进行网络通信
 */
@Slf4j
public class NioClient {

  public static final Integer READ_FILE = 2;


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
              channel.finishConnect(); // 把三次握手做完，TCP连接建立好了
            }

            byte[] filenameBytes = filename.getBytes();

            //封装二进制数据协议
            ByteBuffer readFileRequest = ByteBuffer.allocate(4 + 4 + filenameBytes.length);
            readFileRequest.putInt(READ_FILE);
            readFileRequest
                .putInt(filenameBytes.length);
            readFileRequest.put(filenameBytes);
            readFileRequest.flip();

            channel.write(readFileRequest);

            System.out.println("发送文件下载的请求过去......");

            key.interestOps(SelectionKey.OP_READ);
          }
          // 接收到NIOServer的响应
          else if (key.isReadable()) {
            channel = (SocketChannel) key.channel();

            if (fileLength == null) {
              if (fileLengthBuffer == null) {
                fileLengthBuffer = ByteBuffer.allocate(8);
              }
              channel.read(fileLengthBuffer);
              if (!fileLengthBuffer.hasRemaining()) {
                fileLengthBuffer.rewind();
                fileLength = fileLengthBuffer.getLong();
                System.out.println("从服务端返回数据中解析文件大小：" + fileLength);
              }
            }

            if (fileLength != null) {
              if (fileBuffer == null) {
                fileBuffer = ByteBuffer.allocate(
                    Integer.valueOf(String.valueOf(fileLength)));
              }
              int hasRead = channel.read(fileBuffer);
              System.out.println("从服务端读取了" + hasRead + " bytes的数据出来到内存中");

              if (!fileBuffer.hasRemaining()) {
                fileBuffer.rewind();
                file = fileBuffer.array();
                System.out.println("最终获取到的文件的大小为" + file.length + " bytes");
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
