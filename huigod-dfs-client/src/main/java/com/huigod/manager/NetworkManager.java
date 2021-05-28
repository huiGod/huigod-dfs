package com.huigod.manager;

import com.huigod.entity.Host;
import com.huigod.entity.NetworkRequest;
import com.huigod.entity.NetworkResponse;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 网络连接池子
 */
@Slf4j
public class NetworkManager {

  /**
   * 正在连接中
   */
  public static final Integer CONNECTING = 1;
  /**
   * 已经建立连接
   */
  public static final Integer CONNECTED = 2;
  /**
   * 断开连接
   */
  public static final Integer DISCONNECTED = 3;
  /**
   * 响应状态：成功
   */
  public static final Integer RESPONSE_SUCCESS = 1;
  /**
   * 响应状态：失败
   */
  public static final Integer RESPONSE_FAILURE = 2;
  /**
   * 网络poll操作的超时时间
   */
  public static final Long POLL_TIMEOUT = 500L;
  /**
   * 请求超时检测间隔
   */
  public static final long REQUEST_TIMEOUT_CHECK_INTERVAL = 1000;
  /**
   * 请求超时时长
   */
  public static final long REQUEST_TIMEOUT = 30 * 1000;

  /**
   * 多路复用Selector
   */
  private Selector selector;

  /**
   * 每个数据节点的连接状态
   */
  private Map<String, Integer> connectState;

  /**
   * 所有的连接
   */
  private Map<String, SelectionKey> connections;

  /**
   * 等待建立连接的机器
   */
  private ConcurrentLinkedQueue<Host> waitingConnectHosts;

  /**
   * 排队等待发送的网络请求
   */
  private Map<String, ConcurrentLinkedQueue<NetworkRequest>> waitingRequests;

  /**
   * 马上准备要发送的网络请求
   */
  private Map<String, NetworkRequest> toSendRequests;

  /**
   * 已经完成请求的响应
   */
  private Map<String, NetworkResponse> finishedResponses;

  /**
   * 还没读取完毕的响应
   */
  private Map<String, NetworkResponse> unfinishedResponses;

  public NetworkManager() {
    try {
      this.selector = Selector.open();
    } catch (IOException e) {
      e.printStackTrace();
    }

    this.connections = new ConcurrentHashMap<>();
    this.connectState = new ConcurrentHashMap<>();
    this.waitingConnectHosts = new ConcurrentLinkedQueue<>();
    this.waitingRequests = new ConcurrentHashMap<>();
    this.toSendRequests = new ConcurrentHashMap<>();
    this.finishedResponses = new ConcurrentHashMap<>();
    this.unfinishedResponses = new ConcurrentHashMap<>();

    //IO处理线程
    new NetworkPollThread().start();

    //请求超时处理线程
    new RequestTimeoutCheckThread().start();
  }

  /**
   * 尝试连接到数据节点的端口上去
   *
   * @param hostName
   * @param nioPort
   * @return
   */
  public boolean maybeConnect(String hostName, Integer nioPort) {
    synchronized (this) {
      if (!connectState.containsKey(hostName) || connectState.get(hostName).equals(DISCONNECTED)) {
        connectState.put(hostName, CONNECTING);
        waitingConnectHosts.offer(new Host(hostName, nioPort));
      }

      while (connectState.get(hostName).equals(CONNECTING)) {
        try {
          wait(100);
        } catch (InterruptedException e) {
          log.error("");
        }
      }

      return !connectState.get(hostName).equals(DISCONNECTED);
    }
  }

  /**
   * 发送网络请求
   *
   * @param request
   */
  public void sendRequest(NetworkRequest request) {
    ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.get(request.getHostname());
    requestQueue.offer(request);
  }

  /**
   * 等待指定请求的响应
   * @param requestId
   * @return
   */
  public NetworkResponse waitResponse(String requestId) throws Exception {
    NetworkResponse response;
    while((response = finishedResponses.get(requestId)) == null) {
      Thread.sleep(100);
    }

    toSendRequests.remove(response.getHostname());
    finishedResponses.remove(requestId);

    return response;
  }

  /**
   * 网络连接的核心线程
   */
  class NetworkPollThread extends Thread {

    @Override
    public void run() {
      while (true) {

        //将队列中的请求创建对应连接
        tryConnect();

        //缓存好需要发送的请求
        prepareRequests();

        //尝试完成网络连接、请求发送、响应读取
        poll();

      }
    }

    /**
     * 将队列中的请求创建对应连接
     */
    private void tryConnect() {
      Host host;
      SocketChannel socketChannel;

      while ((host = waitingConnectHosts.poll()) != null) {
        try {
          socketChannel = SocketChannel.open();
          socketChannel.configureBlocking(false);
          socketChannel.connect(new InetSocketAddress(host.getHostName(), host.getNioPort()));
          socketChannel.register(selector, SelectionKey.OP_CONNECT);
        } catch (Exception e) {
          log.error("tryConnect is error:", e);
          connectState.put(host.getHostName(), DISCONNECTED);
        }
      }
    }

    /**
     * 准备好需要发送的请求
     */
    private void prepareRequests() {
      for (String hostname : waitingRequests.keySet()) {
        ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.get(hostname);

        if (!CollectionUtils.isEmpty(requestQueue) && !toSendRequests.containsKey(hostname)) {
          //从客户端请求队列获取一个请求
          NetworkRequest request = requestQueue.poll();
          //将一个请求暂存，后续发送出去
          toSendRequests.put(hostname, request);

          SelectionKey key = connections.get(hostname);
          key.interestOps(SelectionKey.OP_WRITE);
        }
      }
    }

    /**
     * 尝试完成网络连接、请求发送、响应读取
     */
    private void poll() {

      SocketChannel channel = null;

      try {
        int selectedKeys = selector.select(POLL_TIMEOUT);

        if (selectedKeys <= 0) {
          return;
        }

        Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();

        while (keysIterator.hasNext()) {
          SelectionKey key = keysIterator.next();
          keysIterator.remove();

          channel = (SocketChannel) key.channel();

          // 如果是网络连接操作
          if (key.isConnectable()) {
            finishConnect(key, channel);
          } else if (key.isWritable()) {
            sendRequest(key, channel);
          } else if (key.isReadable()) {
            readResponse(key, channel);
          }
        }

      } catch (Exception e) {
        log.error("poll is error:", e);

        if (channel != null) {
          try {
            channel.close();
          } catch (IOException e1) {
            log.error("poll close is error:", e1);
          }
        }
      }
    }

    /**
     * 完成连接
     *
     * @param key
     * @param channel
     */
    private void finishConnect(SelectionKey key, SocketChannel channel) {

      InetSocketAddress remoteAddress = null;

      try {

        remoteAddress = (InetSocketAddress) channel.getRemoteAddress();

        if (channel.isConnectionPending()) {
          while (!channel.finishConnect()) {
            Thread.sleep(100);
          }
        }

        log.info("完成与服务端的连接的建立......");

        //创建好连接后，客户端才可以发送消息，这里初始化队列
        waitingRequests.put(remoteAddress.getHostName(), new ConcurrentLinkedQueue<>());
        connections.put(remoteAddress.getHostName(), key);
        connectState.put(remoteAddress.getHostName(), CONNECTED);
      } catch (Exception e) {
        log.error("finishConnect is error:", e);

        if (remoteAddress != null) {
          connectState.put(remoteAddress.getHostName(), DISCONNECTED);
        }
      }
    }

    /**
     * 发送请求
     *
     * @param key
     * @param channel
     */
    private void sendRequest(SelectionKey key, SocketChannel channel) {

      InetSocketAddress remoteAddress = null;

      try {
        remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
        String hostname = remoteAddress.getHostName();

        //获取即将要发送的数据请求
        NetworkRequest request = toSendRequests.get(hostname);
        ByteBuffer buffer = request.getBuffer();

        channel.write(buffer);

        //粘包与拆包，继续发送
        while (buffer.hasRemaining()) {
          channel.write(buffer);
        }

        log.info("本次请求发送完毕......");

        request.setSendTime(System.currentTimeMillis());

        //关注处理响应信息
        key.interestOps(SelectionKey.OP_READ);
      } catch (Exception e) {
        //异常情况的处理
        log.error("sendRequest is error:", e);

        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);

        if (remoteAddress != null) {
          String hostname = remoteAddress.getHostName();
          NetworkRequest request = toSendRequests.get(hostname);

          NetworkResponse response = new NetworkResponse();
          response.setHostname(hostname);
          response.setRequestId(request.getId());
          response.setIp(request.getIp());
          response.setError(true);
          response.setFinished(true);

          if (request.getNeedResponse()) {
            finishedResponses.put(request.getId(), response);
          } else {
            if (request.getCallback() != null) {
              request.getCallback().process(response);
            }
            toSendRequests.remove(hostname);
          }
        }
      }
    }

    /**
     * 读取响应信息
     *
     * @param key
     * @param channel
     */
    private void readResponse(SelectionKey key, SocketChannel channel) throws Exception {
      InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
      String hostname = remoteAddress.getHostName();

      NetworkRequest request = toSendRequests.get(hostname);
      NetworkResponse response = null;

      if (request.getRequestType().equals(NetworkRequest.REQUEST_SEND_FILE)) {
        response = getSendFileResponse(request.getId(), hostname, channel);
      } else if (request.getRequestType().equals(NetworkRequest.REQUEST_READ_FILE)) {
        response = getReadFileResponse(request.getId(), hostname, channel);
      }

      if (!response.getFinished()) {
        return;
      }

      key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
      if (request.getNeedResponse()) {
        finishedResponses.put(request.getId(), response);
      } else {
        if (request.getCallback() != null) {
          request.getCallback().process(response);
        }
        toSendRequests.remove(hostname);
      }
    }

    /**
     * 读取上传文件的响应
     */
    private NetworkResponse getSendFileResponse(String requestId, String hostname,
        SocketChannel channel) throws Exception {
      ByteBuffer buffer = ByteBuffer.allocate(1024);
      channel.read(buffer);
      buffer.flip();

      NetworkResponse response = new NetworkResponse();
      response.setRequestId(requestId);
      response.setHostname(hostname);
      response.setBuffer(buffer);
      response.setError(false);
      response.setFinished(true);

      return response;
    }

    /**
     * 读取下载的文件响应
     */
    private NetworkResponse getReadFileResponse(String requestId, String hostname,
        SocketChannel channel) throws Exception {

      NetworkResponse response;

      if (!unfinishedResponses.containsKey(hostname)) {
        response = new NetworkResponse();
        response.setRequestId(requestId);
        response.setHostname(hostname);
        response.setError(false);
        response.setFinished(false);
      } else {
        response = unfinishedResponses.get(hostname);
      }

      Long fileLength = null;

      if (response.getBuffer() == null) {
        ByteBuffer lengthBuffer;
        if (response.getLengthBuffer() == null) {
          lengthBuffer = ByteBuffer.allocate(NetworkRequest.FILE_LENGTH);
          response.setLengthBuffer(lengthBuffer);
        } else {
          lengthBuffer = response.getLengthBuffer();
        }

        channel.read(lengthBuffer);
        if (!lengthBuffer.hasRemaining()) {
          lengthBuffer.rewind();
          fileLength = lengthBuffer.getLong();
        } else {
          unfinishedResponses.put(hostname, response);
        }
      }

      if (fileLength != null || response.getBuffer() != null) {
        ByteBuffer buffer;

        if (response.getBuffer() == null) {
          buffer = ByteBuffer.allocate(Integer.parseInt(String.valueOf(fileLength)));
          response.setBuffer(buffer);
        } else {
          buffer = response.getBuffer();
        }

        channel.read(buffer);

        if (!buffer.hasRemaining()) {
          buffer.rewind();
          response.setFinished(true);
          unfinishedResponses.remove(hostname);
        } else {
          unfinishedResponses.put(hostname, response);
        }
      }
      return response;
    }
  }

  /**
   * 超时检测线程
   */
  class RequestTimeoutCheckThread extends Thread {

    @Override
    public void run() {
      while (true) {
        try {
          long now = System.currentTimeMillis();
          for (NetworkRequest request : toSendRequests.values()) {
            if (now - request.getSendTime() > REQUEST_TIMEOUT) {
              String hostname = request.getHostname();
              NetworkResponse response = new NetworkResponse();
              response.setHostname(hostname);
              response.setIp(request.getIp());
              response.setRequestId(request.getId());
              response.setError(true);
              response.setFinished(true);

              if (request.getNeedResponse()) {
                finishedResponses.put(request.getId(), response);
              } else {
                if (request.getCallback() != null) {
                  request.getCallback().process(response);
                }
                toSendRequests.remove(hostname);
              }
            }
          }
          Thread.sleep(REQUEST_TIMEOUT_CHECK_INTERVAL);
        } catch (Exception e) {
          log.error("RequestTimeoutCheckThread run is error:", e);
        }
      }
    }
  }
}
