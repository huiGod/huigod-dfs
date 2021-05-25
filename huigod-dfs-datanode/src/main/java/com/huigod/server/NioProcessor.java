package com.huigod.server;

import com.huigod.entity.NetworkRequest;
import com.huigod.entity.NetworkRequestQueue;
import com.huigod.entity.NetworkResponse;
import com.huigod.entity.NetworkResponseQueues;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * 负责解析请求以及发送响应的线程
 */
@Slf4j
public class NioProcessor extends Thread {

  /**
   * 多路复用监听时的最大阻塞时间
   */
  public static final Long POLL_BLOCK_MAX_TIME = 1000L;

  /**
   * 唯一标识
   */
  private Integer processorId;

  /**
   * 等待注册的网络连接的队列
   */
  private ConcurrentLinkedQueue<SocketChannel> channelQueue = new ConcurrentLinkedQueue<>();

  /**
   * 每个Processor私有的Selector多路复用器
   */
  private Selector selector;

  /**
   * 没读取完的请求缓存在这里
   */
  private Map<String, NetworkRequest> cachedRequests = new HashMap<>();

  /**
   * 暂存起来的响应缓存在这里
   */
  private Map<String, NetworkResponse> cachedResponses = new HashMap<>();

  /**
   * processor负责维护的所有客户端的SelectionKey
   */
  private Map<String, SelectionKey> cachedKeys = new HashMap<>();


  public NioProcessor(Integer processorId) {
    try {
      this.processorId = processorId;
      this.selector = Selector.open();
    } catch (Exception e) {
      log.error("NioProcessor construct is error:", e);
    }
  }

  /**
   * 给这个Processor线程分配一个网络连接
   *
   * @param channel
   */
  public void addChannel(SocketChannel channel) {
    channelQueue.offer(channel);
    //唤醒阻塞在select的操作
    selector.wakeup();
  }

  @Override
  public void run() {
    while (true) {
      try {
        // 注册排队等待的连接
        registerQueuedClients();

        // 处理响应给客户端消息
        cacheQueuedResponse();

        //以多路复用的方式来监听各个连接的请求
        poll();
      } catch (Exception e) {
        log.error("NioProcessor run is error:", e);
      }
    }
  }

  /**
   * 将创建好的连接注册到Selector上，并且关注OP_READ来监听客户端发送的消息
   */
  private void registerQueuedClients() {
    SocketChannel channel;
    while ((channel = channelQueue.poll()) != null) {
      try {
        channel.register(selector, SelectionKey.OP_READ);
      } catch (Exception e) {
        log.error("registerQueuedClients is error:", e);
      }
    }
  }

  /**
   * 处理响应给客户端消息
   */
  private void cacheQueuedResponse() {
    NetworkResponseQueues responseQueues = NetworkResponseQueues.get();
    NetworkResponse response;

    while((response = responseQueues.poll(processorId)) != null) {
      String client = response.getClient();
      cachedResponses.put(client, response);
      //客户端连接关注OP_WRITE发送响应
      cachedKeys.get(client).interestOps(SelectionKey.OP_WRITE);
    }
  }

  /**
   * 以多路复用的方式来监听各个连接的请求 紧紧是处理请求解析，涉及到IO操作的会交给IO Thread处理
   */
  private void poll() {

    try {
      int keys = selector.select(POLL_BLOCK_MAX_TIME);
      if (keys <= 0) {
        return;
      }

      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();

        SocketChannel channel = (SocketChannel) key.channel();
        String client = channel.getRemoteAddress().toString();

        // 客户端发送请求处理
        if (key.isReadable()) {
          NetworkRequest request;
          if (cachedRequests.get(client) != null) {
            request = cachedRequests.get(client);
          } else {
            request = new NetworkRequest();
          }

          request.setChannel(channel);
          request.setKey(key);
          request.read();

          if (request.hasCompletedRead()) {
            // 此时就可以将一个请求分发到全局的请求队列里去了
            request.setProcessorId(processorId);
            request.setClient(client);
            NetworkRequestQueue.get().offer(request);

            cachedKeys.put(client, key);
            cachedRequests.remove(client);

            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
          } else {
            //如果请求未完成，缓存后，仍然关注着OP_READ事件，下一次继续处理
            cachedRequests.put(client, request);
          }
        } else if (key.isWritable()) {
          //发送给客户端响应处理
          NetworkResponse response = cachedResponses.get(client);
          channel.write(response.getBuffer());

          cachedResponses.remove(client);
          cachedKeys.remove(client);

          key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
        }
      }
    } catch (Exception e) {
      log.error("poll is error:", e);
    }
  }

  public Integer getProcessorId() {
    return processorId;
  }

  public void setProcessorId(Integer processorId) {
    this.processorId = processorId;
  }

}
