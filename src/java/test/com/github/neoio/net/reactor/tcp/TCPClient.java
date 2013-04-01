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
package com.github.neoio.net.reactor.tcp;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.message.impl.StringMessage;
import com.github.neoio.net.message.staging.MessageStagingFactory;
import com.github.neoio.net.message.staging.memory.MemoryMessageStagingFactory;
import com.github.neoio.net.reactor.ClientConnectionInformer;
import com.github.neoio.net.reactor.tcp.TCPIOReactor;
import com.github.neoio.nio.util.NIOUtils;

class TCPClient
{
   private static Logger logger=LoggerFactory.getLogger(TCPClient.class);
   private TCPIOReactor reactor;
   private SocketAddress serverAddress;
   private ConnectionKey key;
   private CountDownLatch messageLatch;
   private List<String> messages=new ArrayList<String>();
   
   public TCPClient(SocketAddress serverAddress, int messageCount)
   {
      this(serverAddress, messageCount, new MemoryMessageStagingFactory());
   }
   public TCPClient(SocketAddress serverAddress, int messageCount, MessageStagingFactory messageStagingFactory)
   {
      this.serverAddress=serverAddress;
      this.reactor=new TCPIOReactor(messageStagingFactory);
      this.messageLatch=new CountDownLatch(messageCount);
   }
   public void start()
   {
      reactor.start();
      this.key=reactor.connectClient(new ClientConnectionInformer()
      {
         @Override
         public void unableToConnect(SocketAddress socketAddress)
         {
            System.err.println("Unable to connect: " + socketAddress);
         }
         @Override
         public void received(ConnectionKey connectionKey, ReadableByteChannel message, long messageSize)
         {
            logger.debug("TCPClientConnectionInformer received messageLength: {}", messageSize);
            ByteBuffer buffer=ByteBuffer.allocate((int) messageSize);
            
            logger.debug("TCPClientConnectionInformer received bytes read: {}", NIOUtils.readToBuffer(message, buffer));
            buffer.flip();
            messages.add(TestTCPIOReactor.charset.decode(buffer).toString());
            messageLatch.countDown();
         }
         @Override
         public void closed(ConnectionKey connectionKey)
         {
            System.out.println("Client closed: " + connectionKey);
         }
         @Override
         public void ableToConnect(SocketAddress endPointAddress)
         {
            
         }
      }, serverAddress);
   }
   public void stop()
   {
      reactor.shutdown();
   }
   public void sendMessage(String message)
   {
      reactor.sendMessage(key, new StringMessage(message, TestTCPIOReactor.charset));
   }
   public void awaitMessages()
   {
      try
      {
         messageLatch.await();
      }
      catch(InterruptedException e)
      {
         e.printStackTrace();
      }
   }
   public List<String> getMessages()
   {
      return messages;
   }
}
