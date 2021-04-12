package org.huigod.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 代表了一条edits log
 */
@Data
@AllArgsConstructor
public class EditLog {

  private Long txid;
  private String content;
}
