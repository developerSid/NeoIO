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

import java.net.InetSocketAddress;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.message.impl.StringMessage;
import com.github.neoio.net.message.staging.MessageStagingFactory;
import com.github.neoio.net.message.staging.memory.MemoryMessageStagingFactory;
import com.github.neoio.net.reactor.tcp.TCPIOReactor;

class TCPServer
{
   private static Logger logger=LoggerFactory.getLogger(TCPServer.class);
   private TCPIOReactor reactor;
   private int port;
   private TCPServerConnectionInformer informer;
   
   public TCPServer(int port, int messageCount)
   {
      this(port, messageCount, new MemoryMessageStagingFactory());
   }
   public TCPServer(int port, int messageCount, MessageStagingFactory messageStagingFactory)
   {
      this.port=port;
      this.reactor=new TCPIOReactor(messageStagingFactory);
      this.informer=new TCPServerConnectionInformer(reactor, messageCount);
   }
   public void start()
   {
      reactor.start();
      reactor.bindServer(informer, new InetSocketAddress(this.port));
      informer.awaitServerBind();
   }
   public void stop()
   {
      reactor.shutdown();
   }
   public void sendMessage(String message)
   {
      logger.debug(message);
      
      this.reactor.sendMessage(informer.getKey(), new StringMessage(message, TestTCPIOReactor.charset));
   }
   public List<String> getMessages()
   {
      return informer.getMessages();
   }
   public void awaitMessages()
   {
      informer.waitForAllMessages();
   }
}
