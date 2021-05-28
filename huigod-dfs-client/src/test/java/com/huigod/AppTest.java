package com.huigod;

import com.huigod.entity.FileInfo;
import com.huigod.service.FileSystem;
import com.huigod.service.impl.FileSystemImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

  private static FileSystem filesystem = new FileSystemImpl();

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

  @Test
  public void testUpload() throws Exception {
    FileSystem fileSystem = new FileSystemImpl();
    File file = new File("test.png");

    FileInputStream in = null;
    FileChannel fileChannel = null;
    try {
      in = new FileInputStream(file);
      fileChannel = in.getChannel();

      ByteBuffer buffer = ByteBuffer.allocate((int)file.length());

      fileChannel.read(buffer);
      buffer.flip();
      FileInfo fileInfo = new FileInfo();
      fileInfo.setFilename("/root/test1.png");
      fileInfo.setFileLength((int)file.length());
      fileInfo.setFile(buffer.array());
      fileSystem.upload(fileInfo, null);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      fileChannel.close();
      in.close();
    }
  }

  @Test
  public void testDownload() throws Exception {
    byte[] image = filesystem.download("/root/test1.png");
    ByteBuffer buffer = ByteBuffer.wrap(image);

    File file = new File("test1.png");
    if (!file.exists()) {
      file.createNewFile();
    }
    FileOutputStream imageOut = new FileOutputStream(file);
    FileChannel imageChannel = imageOut.getChannel();
    imageChannel.write(buffer);

    imageChannel.close();
    imageOut.close();

  }
}
