package com.huigod.manager;

import com.huigod.entity.INode;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 负责管理内存中的文件目录树的核心组件
 */
@Data
public class FSDirectory {

  /**
   * 内存中的文件目录树
   */
  private INode dirTree;

  public FSDirectory() {
    this.dirTree = new INode("/");
  }

  /**
   * 创建目录
   */
  public void mkdir(String path) {
    // path = /usr/warehouse/hive
    synchronized (dirTree) {
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
      System.out.println(blank + dirTemp.getPath());
      printDirTree(dirTemp, blank + " ");
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

      if ((child.getPath().equals(path))) {
        return child;
      }
    }

    return null;
  }
}
