package com.huigod.manager;

import com.huigod.service.INode;
import com.huigod.service.impl.INodeDirectory;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 负责管理内存中的文件目录树的核心组件
 */
public class FSDirectory {

  /**
   * 内存中的文件目录树
   */
  private INodeDirectory dirTree;

  public FSDirectory() {
    this.dirTree = new INodeDirectory("/");
  }

  /**
   * 创建目录
   */
  public void mkdir(String path) {
    // path = /usr/warehouse/hive
    synchronized (dirTree) {
      String[] paths = path.split("/");
      INodeDirectory parent = dirTree;

      for (String splitPath : paths) {
        if ("".equals(splitPath.trim())) {
          continue;
        }

        INodeDirectory dir = findDirectory(parent, splitPath);
        if (dir != null) {
          parent = dir;
          continue;
        }

        INodeDirectory child = new INodeDirectory(splitPath);
        parent.addChild(child);
        parent = child;
      }

    }
    //this.printDirTree(dirTree, "");
  }

  /**
   * 打印目录树
   */
  private void printDirTree(INodeDirectory dirTree, String blank) {
    if (CollectionUtils.isEmpty(dirTree.getChildren())) {
      return;
    }

    for (INode dirTemp : dirTree.getChildren()) {
      System.out.println(blank + ((INodeDirectory) dirTemp).getPath());
      printDirTree((INodeDirectory) dirTemp, blank + " ");
    }
  }

  /**
   * 对文件目录树递归查找目录
   */
  private INodeDirectory findDirectory(INodeDirectory dir, String path) {
    if (dir.getChildren().size() == 0) {
      return null;
    }

    for (INode child : dir.getChildren()) {
      if (child instanceof INodeDirectory) {
        INodeDirectory childDir = (INodeDirectory) child;

        if ((childDir.getPath().equals(path))) {
          return childDir;
        }
      }
    }

    return null;
  }
}
