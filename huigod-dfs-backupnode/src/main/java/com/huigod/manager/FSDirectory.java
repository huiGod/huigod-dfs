package com.huigod.manager;

import com.alibaba.fastjson.JSONObject;
import com.huigod.entity.FSImage;
import com.huigod.entity.INode;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 负责管理内存中的文件目录树的核心组件
 */
public class FSDirectory {

  /**
   * 内存中的文件目录树
   */
  private INode dirTree;

  /**
   * 文件目录树的读写锁
   */
  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  /**
   * 当前文件目录树的更新到了哪个txid对应的editslog
   */
  private long maxTxid = 0;

  public FSDirectory() {
    this.dirTree = new INode("/");
  }

  /**
   * 创建目录
   */
  public void mkdir(long txid, String path) {
    // path = /usr/warehouse/hive
    try {
      lock.writeLock().lock();

      maxTxid = txid;

      String[] paths = path.split("/");
      INode parent = dirTree;

      for (String splitPath : paths) {
        if ("".equals(splitPath.trim())) {
          continue;
        }

        INode dir = findDirectory(parent, splitPath);
        if (dir != null) {
          parent = dir;
          continue;
        }

        INode child = new INode(splitPath);
        parent.addChild(child);
        parent = child;
      }
    } finally {
      lock.writeLock().unlock();
    }

    //this.printDirTree(dirTree, "");
  }

  /**
   * 打印目录树
   */
  private void printDirTree(INode dirTree, String blank) {
    if (CollectionUtils.isEmpty(dirTree.getChildren())) {
      return;
    }

    for (INode dirTemp : dirTree.getChildren()) {
      log.info(blank + ((INode) dirTemp).getPath());
      printDirTree((INode) dirTemp, blank + " ");
    }
  }

  /**
   * 对文件目录树递归查找目录
   */
  private INode findDirectory(INode dir, String path) {
    if (dir.getChildren().size() == 0) {
      return null;
    }

    for (INode child : dir.getChildren()) {
      if (child instanceof INode) {
        INode childDir = (INode) child;

        if ((childDir.getPath().equals(path))) {
          return childDir;
        }
      }
    }

    return null;
  }

  /**
   * 以json格式获取到fsimage内存元数据
   *
   * @return
   */
  public FSImage getFSImage() {
    FSImage fsimage;

    try {
      //通过读写锁获取内存日志数据
      lock.readLock().lock();
      String fsimageJson = JSONObject.toJSONString(dirTree);

      //需要记录此刻内存中的最大txid，后续nameNode在txid之后的数据都可以抛弃
      fsimage = new FSImage(maxTxid, fsimageJson);
    } finally {
      lock.readLock().unlock();
    }

    return fsimage;
  }

  /**
   * 创建文件
   *
   * @param filename 文件名
   * @return
   */
  public Boolean create(long txid, String filename) {
    // /image/product/img001.jpg
    // 其实完全可以把前面的路径部分截取出来，去找对应的目录
    try {
      lock.writeLock().lock();

      maxTxid = txid;

      String[] splitedFilename = filename.split("/");
      String realFilename = splitedFilename[splitedFilename.length - 1];

      INode parent = dirTree;

      for (int i = 0; i < splitedFilename.length - 1; i++) {
        if (i == 0) {
          continue;
        }

        INode dir = findDirectory(parent, splitedFilename[i]);

        if (dir != null) {
          parent = dir;
          continue;
        }

        INode child = new INode(splitedFilename[i]);
        parent.addChild(child);
        parent = child;
      }

      // 此时就已经获取到了文件的上一级目录
      // 可以查找一下当前这个目录下面是否有对应的文件了
      if (existFile(parent, realFilename)) {
        return false;
      }

      // 真正的在目录里创建一个文件出来
      INode file = new INode(realFilename);
      parent.addChild(file);
      return true;
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * 目录下是否存在这个文件
   *
   * @param dir
   * @param filename
   * @return
   */
  private Boolean existFile(INode dir, String filename) {
    if (dir.getChildren() != null && dir.getChildren().size() > 0) {
      for (INode child : dir.getChildren()) {
        if (child.getPath().equals(filename)) {
          return true;
        }
      }
    }
    return false;
  }

  public void setMaxTxid(long maxTxid) {
    this.maxTxid = maxTxid;
  }

  public void setDirTree(INode dirTree) {
    this.dirTree = dirTree;
  }
}
