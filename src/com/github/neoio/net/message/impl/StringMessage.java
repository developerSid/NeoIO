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
package com.github.neoio.net.message.impl;

import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetCharacterCodingException;
import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.Message;
import com.github.neoio.net.message.MessageSendFinished;
import com.github.neoio.nio.util.NIOUtils;


public class StringMessage implements Message
{
   private static Logger logger=LoggerFactory.getLogger(StringMessage.class);
   private ByteArrayInputStream is;
   private ReadableByteChannel channel;
   private long size;
   private MessageSendFinished finished;
   
   public StringMessage(String string, Charset charset)throws NetCharacterCodingException
   {
      ByteBuffer buffer=charset.encode(CharBuffer.wrap(string));
      is=new ByteArrayInputStream(buffer.array(), 0, buffer.limit());
      channel=Channels.newChannel(is);
      size=buffer.limit();
   }
   public StringMessage(String string, Charset charset, MessageSendFinished finished)throws NetCharacterCodingException
   {
      this(string, charset);
      this.finished=finished;
   }
   @Override
   public long bytesLeft()
   {
      logger.debug("bytes left: {}", is.available());
      return is.available();
   }
   @Override
   public void finished() throws NetIOException
   {
      if(finished != null)
      {
         logger.debug("Firing finish");
         finished.finished();
      }
   }
   @Override
   public int writeToBuffer(ByteBuffer buffer) throws NetIOException
   {
      int bytesRead=NIOUtils.readToBuffer(channel, buffer);
      int toReturn;
      
      if(bytesRead > -1)   //apparently the encoder will encode the empty string to a byte array of length zero
         toReturn=bytesRead; //when it is read by the read method of the ReadableByteChannel it returns -1 indicating end of stream
      else
         toReturn=0;
      
      logger.debug("bytes written to buffer: {}", toReturn);
      
      return toReturn;
   }
   @Override
   public long size()
   {
      return size;
   }
   @Override
   public void erroredWhileSending(Exception e) throws NetIOException
   {
      logger.error("Error occurred while sending", e);
      this.finished();
   }
}
