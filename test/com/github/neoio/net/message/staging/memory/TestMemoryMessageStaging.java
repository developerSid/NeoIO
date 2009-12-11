package com.github.neoio.net.message.staging.memory;

import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Test;


public class TestMemoryMessageStaging
{
   private MemoryMessageStaging staging=new MemoryMessageStaging();
   
   @Test
   public void test_tempRead()
   {
      ByteBuffer buffer=ByteBuffer.allocate(1024);
      
      buffer.put("Hello World".getBytes());
      buffer.rewind();
      staging.writeTempReadBytes(buffer);
      Assert.assertTrue(staging.hasTempReadBytes());
      
      buffer.clear();
      staging.readTempReadBytes(buffer);
      Assert.assertEquals("Hello World", new String(ArrayUtils.subarray(buffer.array(), 0, "Hello World".getBytes().length)));
      staging.resetTempReadBytes();
      
      Assert.assertFalse(staging.hasTempReadBytes());
   }
   @Test
   public void test_tempWrite()
   {
      ByteBuffer buffer=ByteBuffer.allocate(1024);
      
      buffer.put("Hello World".getBytes());
      buffer.rewind();
      staging.writeTempWriteBytes(buffer);
      Assert.assertTrue(staging.hasTempWriteBytes());
      
      buffer.clear();
      staging.readTempWriteBytes(buffer);
      Assert.assertEquals("Hello World", new String(ArrayUtils.subarray(buffer.array(), 0, "Hello World".getBytes().length)));
      staging.resetTempWriteBytes();
      
      Assert.assertFalse(staging.hasTempWriteBytes());
   }
   @Test
   public void test_primaryStage() throws Exception
   {
      ByteBuffer buffer=ByteBuffer.allocate(1024);
      
      buffer.put("Hello World".getBytes());
      buffer.flip();
      staging.writePrimaryStaging(buffer, "Hello World".getBytes().length);
      
      buffer.clear();
      staging.getPrimaryStage().read(buffer);
      Assert.assertEquals("Hello World".getBytes().length, staging.getPrimaryStageSize());
      Assert.assertEquals("Hello World", new String(buffer.array(), 0, buffer.position()));
      
      staging.resetPrimaryStage();
      Assert.assertEquals(0, staging.getPrimaryStageSize());
   }
   @After
   public void after()
   {
      staging.close();
   }
}
