package com.huigod;

import com.huigod.service.FileSystem;
import com.huigod.service.impl.FileSystemImpl;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

  /**
   * 内存目录树单测
   */
  @Test
  public void testWriteDir() throws Exception {
    FileSystem fileSystem = new FileSystemImpl();
    fileSystem.mkdir("/hello/world");
    fileSystem.mkdir("/hello/you");
    fileSystem.mkdir("/you");
    fileSystem.mkdir("/you/hello");
    fileSystem.mkdir("/ni/hello");
  }

  @Test
  public void testWriteData() throws Exception {
    FileSystem fileSystem = new FileSystemImpl();

    for (int i = 1; i <= 10; i++) {

      new Thread(() -> {
        for (int j = 1; j <= 200; j++) {
          try {
            fileSystem.mkdir("/hive/path/" + j + "-" + Thread.currentThread().getName());
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }).start();
    }

    Thread.sleep(50000);
  }

  @Test
  public void testShutDown() throws Exception {
    FileSystem fileSystem = new FileSystemImpl();
    fileSystem.shutdown();
  }
}
