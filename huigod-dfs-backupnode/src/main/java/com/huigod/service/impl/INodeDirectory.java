package com.huigod.service.impl;

import com.huigod.service.INode;
import java.util.LinkedList;
import java.util.List;
import lombok.Data;

/**
 * 代表文件目录树中的一个目录
 */
@Data
public class INodeDirectory implements INode {

  /**
   * 目录路径
   */
  private String path;

  /**
   * 目录下的节点
   */
  private List<INode> children;

  /**
   * 目录下添加节点
   * @param inode
   */
  public void addChild(INode inode) {
    this.children.add(inode);
  }

  public INodeDirectory(String path) {
    this.path = path;
    this.children = new LinkedList<INode>();
  }
}
