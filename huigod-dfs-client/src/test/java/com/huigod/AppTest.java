package com.huigod;

import com.huigod.service.FileSystem;
import com.huigod.service.impl.FileSystemImpl;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

  /**
   * Rigorous Test :-)
   */
  @Test
  public void shouldAnswerWithTrue() throws Exception{
    FileSystem fileSystem = new FileSystemImpl();
    fileSystem.mkdir("/hello/world");
    fileSystem.mkdir("/hello/you");
    fileSystem.mkdir("/you");
  }
}
