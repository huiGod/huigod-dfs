package com.huigod.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 代表了一条edits log
 */
@Data
public class EditLog {

  private Long txid;
  private String content;

  public EditLog(long txid, String content) {
    this.txid = txid;
    JSONObject jsonObject = JSONObject.parseObject(content);
    jsonObject.put("txid", txid);

    this.content = jsonObject.toJSONString();
  }
}
