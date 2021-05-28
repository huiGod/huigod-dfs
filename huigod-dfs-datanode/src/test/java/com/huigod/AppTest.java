package com.huigod;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception
    {
        FileOutputStream imageOut = new FileOutputStream("D:\\user\\文档\\cl\\dfs\\test\\tmp2\\root\\test1.png");
        FileChannel imageChannel = imageOut.getChannel();
        imageChannel.write(ByteBuffer.wrap("success".getBytes()));
        imageChannel.close();
        imageOut.close();
    }
}
