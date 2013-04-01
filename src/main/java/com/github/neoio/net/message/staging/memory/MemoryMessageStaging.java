/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.neoio.net.message.staging.memory;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.io.output.CountingOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.message.staging.spi.AbstractMessageStaging;
import com.github.neoio.nio.util.NIOUtils;

class MemoryMessageStaging extends AbstractMessageStaging implements MessageStaging
{
   private Logger logger=LoggerFactory.getLogger(this.getClass());
   private ByteArrayOutputStream primaryStage;
   private ByteArrayOutputStream readTempStage;
   private ByteArrayOutputStream writeTempStage;
   private CountingOutputStream readTempStageCount;
   private CountingOutputStream writeTempStageCount;
   
   MemoryMessageStaging()
   {
      logger.debug("creating memory message staging");
      this.primaryStage=new ByteArrayOutputStream();
      this.readTempStage=new ByteArrayOutputStream();
      this.writeTempStage=new ByteArrayOutputStream();
      this.readTempStageCount=new CountingOutputStream(readTempStage);
      this.writeTempStageCount=new CountingOutputStream(writeTempStage);
   }
   @Override
   public void close()throws NetIOException
   {
      IOUtils.closeQuietly(primaryStage);
      IOUtils.closeQuietly(readTempStage);
      IOUtils.closeQuietly(writeTempStage);
   }
   @Override
   public boolean hasTempWriteBytes()throws NetIOException
   {
      return writeTempStage.size() > 0;
   }
   @Override
   public int readTempWriteBytes(ByteBuffer buffer)throws NetIOException
   {
      byte bytes[]=writeTempStage.toByteArray();
      
      buffer.put(bytes, 0, writeTempStageCount.getCount());
      
      return bytes.length;
   }
   @Override
   public int writeTempWriteBytes(ByteBuffer buffer)throws NetIOException
   {
      return NIOUtils.writeToChannel(Channels.newChannel(writeTempStageCount), buffer);
   }
   @Override
   public void resetTempWriteBytes()throws NetIOException
   {
      writeTempStage.reset();
      writeTempStageCount.resetCount();
   }
   @Override
   public boolean hasTempReadBytes()throws NetIOException
   {
      return readTempStage.size() > 0;
   }
   @Override
   public int readTempReadBytes(ByteBuffer buffer) throws NetIOException
   {
      byte bytes[]=readTempStage.toByteArray();
      
      buffer.put(bytes, 0, readTempStageCount.getCount());
      
      return readTempStageCount.getCount();
   }
   @Override
   public void resetTempReadBytes() throws NetIOException
   {
      readTempStage.reset();
      readTempStageCount.resetCount();
   }
   @Override
   public int writeTempReadBytes(ByteBuffer buffer) throws NetIOException
   {
      return NIOUtils.writeToChannel(Channels.newChannel(readTempStageCount), buffer);
   }
   @Override
   public void writePrimaryStaging(ByteBuffer buffer, int length) throws NetIOException
   {
      primaryStage.write(buffer.array(), buffer.position(), length);
      buffer.position(buffer.limit());
   }
   @Override
   public void resetPrimaryStage()throws NetIOException
   {
      logger.debug("Reseting Memory message primary stage");
      primaryStage.reset();
   }
   @Override
   public long getPrimaryStageSize() throws NetIOException
   {
      return primaryStage.size();
   }
   @Override
   protected ReadableByteChannel getPrimaryStageChannel()
   {
      return Channels.newChannel(new ByteArrayInputStream(primaryStage.toByteArray()));
   }
}
