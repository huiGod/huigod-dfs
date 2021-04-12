package org.huigod.service.impl;

import lombok.Data;
import org.huigod.service.INode;

/**
 * 代表文件目录树中的一个文件
 */
@Data
public class INodeFile implements INode {

  private String name;
}
