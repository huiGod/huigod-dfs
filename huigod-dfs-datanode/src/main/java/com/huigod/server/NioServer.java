package com.huigod.server;

import com.huigod.config.DataNodeConfig;
import com.huigod.entity.NetworkResponseQueues;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据节点的NIOServer
 */
@Slf4j
public class NioServer extends Thread {

  public static final Integer PROCESSOR_THREAD_NUM = 10;
  public static final Integer IO_THREAD_NUM = 10;


  /**
   * NIO的selector，负责多路复用监听多个连接的请求
   */
  private Selector selector;

  /**
   * 负责解析请求和发送响应的Processor线程
   */
  private List<NioProcessor> processors = new ArrayList<>();

  /**
   * 与NameNode进行通信的客户端
   */
  private NameNodeRpcClient nameNodeRpcClient;

  /**
   * NIOServer的初始化，监听端口、队列初始化、线程初始化
   */
  public NioServer(NameNodeRpcClient nameNodeRpcClient) {
    this.nameNodeRpcClient = nameNodeRpcClient;
  }

  public void init() {
    try {
      selector = Selector.open();

      ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
      serverSocketChannel.configureBlocking(false);
      serverSocketChannel.socket().bind(new InetSocketAddress(DataNodeConfig.getNioPort()), 100);
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

      log.info("NioServer已经启动，开始监听端口：{}", DataNodeConfig.getNioPort());

      NetworkResponseQueues responseQueues = NetworkResponseQueues.get();

      // 启动固定数量的Processor线程，并初始化对应的响应队列
      for (int i = 0; i < PROCESSOR_THREAD_NUM; i++) {
        NioProcessor processor = new NioProcessor(i);
        processors.add(processor);
        processor.start();

        responseQueues.initResponseQueue(i);
      }

      // 启动固定数量的IO线程
      for (int i = 0; i < IO_THREAD_NUM; i++) {
        new IoThread(nameNodeRpcClient).start();
      }

    } catch (Exception e) {
      log.error("DataNodeNIOServer is error:", e);
    }
  }

  /**
   * NioServer线程只负责创建客户端连接SocketChannel，其他的流程交给NioProcessor线程处理
   */
  @Override
  public void run() {
    //等待IO多路复用方式监听请求
    while (true) {
      try {
        selector.select();
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
          SelectionKey key = keyIterator.next();
          keyIterator.remove();

          if (key.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            // 跟每个客户端建立连接
            SocketChannel channel = serverSocketChannel.accept();

            if (channel != null) {
              channel.configureBlocking(false);

              int processorIndex = new Random().nextInt(PROCESSOR_THREAD_NUM);
              NioProcessor processor = processors.get(processorIndex);
              processor.addChannel(channel);
            }
          }
        }
      } catch (Exception e) {
        log.error("DataNodeNIOServer run is error:", e);
      }
    }
  }
}
