package com.huigod.entity;

import java.util.LinkedList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代表的是文件目录树中的一个节点
 */
@Data
@NoArgsConstructor
public class INode {

  private String path;
  private List<INode> children;

  public INode(String path) {
    this.path = path;
    this.children = new LinkedList<>();
  }
  public void addChild(INode inode) {
    this.children.add(inode);
  }

}
