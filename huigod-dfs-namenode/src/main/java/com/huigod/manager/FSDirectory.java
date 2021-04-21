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
    // 你应该先判断一下，“/”根目录下有没有一个“usr”目录的存在
    // 如果说有的话，那么再判断一下，“/usr”目录下，有没有一个“/warehouse”目录的存在
    // 如果说没有，那么就得先创建一个“/warehosue”对应的目录，挂在“/usr”目录下
    // 接着再对“/hive”这个目录创建一个节点挂载上去

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
    this.printDirTree(dirTree, "");
  }

  /**
   * 打印目录树
   */
  private void printDirTree(INodeDirectory dirTree, String str) {

    System.out.println(str + dirTree.getPath());

    if (!CollectionUtils.isEmpty(dirTree.getChildren())) {

      str = " " + str;
      for (INode dirTemp : dirTree.getChildren()) {
        printDirTree((INodeDirectory) dirTemp, str);
      }
    }
  }

  /**
   * 对文件目录树递归查找目录
   */
  private INodeDirectory findDirectory(INodeDirectory dir, String path) {
    if (dir.getChildren().size() == 0) {
      return null;
    }

    INodeDirectory resultDir = null;

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
