package com.huigod.entity;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表文件目录树中的一个目录
 */
@Data
@NoArgsConstructor
public class INode {

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
   *
   * @param inode
   */
  public void addChild(INode inode) {
    this.children.add(inode);
  }

  public INode(String path) {
    this.path = path;
    this.children = new LinkedList<>();
  }
}
