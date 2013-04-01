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
package com.github.neoio.net.message.staging;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.github.neoio.net.exception.NetIOException;


public interface MessageStaging
{
   public void resetPrimaryStage()throws NetIOException;
   public void close()throws NetIOException;
   public boolean hasTempWriteBytes()throws NetIOException;
   public int readTempWriteBytes(ByteBuffer buffer)throws NetIOException;
   public int writeTempWriteBytes(ByteBuffer buffer)throws NetIOException;
   public void setWriteLastPacket(boolean writeLastPacket);
   boolean isWriteLastPacket();
   public void resetTempWriteBytes()throws NetIOException;
   public boolean hasTempReadBytes()throws NetIOException;
   public int readTempReadBytes(ByteBuffer buffer)throws NetIOException;
   public void resetTempReadBytes()throws NetIOException;
   public int writeTempReadBytes(ByteBuffer buffer)throws NetIOException;
   public void writePrimaryStaging(ByteBuffer buffer, int bodyLength)throws NetIOException;
   public ReadableByteChannel getPrimaryStage()throws NetIOException;
   public long getPrimaryStageSize()throws NetIOException;
}
