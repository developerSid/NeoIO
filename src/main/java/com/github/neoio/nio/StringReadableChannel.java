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
package com.github.neoio.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class StringReadableChannel implements ReadableByteChannel
{
   private ReadableByteChannel channel;
   private long size;
   
   public StringReadableChannel(String string) throws IOException
   {
      this(string, null);
   }
   public StringReadableChannel(String string, String encoding) throws IOException
   {
      byte bytes[]=encoding != null ? string.getBytes(encoding) : string.getBytes();
      
      this.channel=Channels.newChannel(new ByteArrayInputStream(bytes));
      this.size=bytes.length;
   }
   @Override
   public int read(ByteBuffer dst) throws IOException
   {
      return channel.read(dst);
   }
   @Override
   public void close() throws IOException
   {
      channel.close();
   }
   @Override
   public boolean isOpen()
   {
      return channel.isOpen();
   }
   public long size()
   {
      return size;
   }
}
