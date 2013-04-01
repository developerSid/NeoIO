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
import java.nio.channels.SelectableChannel;
import java.nio.channels.SocketChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.reactor.ClientConnectionInformer;
import com.github.neoio.nio.util.NIOUtils;


class TCPClientConnectionKey implements TCPConnectionKey
{
   private static Logger logger=LoggerFactory.getLogger(TCPClientConnectionKey.class);
   private ClientConnectionInformer informer;
   private SocketChannel client;
   private SocketAddress endPointAddress;
   private int uniqueKey;
   private MessageStaging messageStaging;
   private boolean valid=true;
   
   TCPClientConnectionKey(ClientConnectionInformer informer, SocketAddress endPointAddress, int uniqueKey, MessageStaging messageStaging)
   {
      this.client=null;
      this.informer=informer;
      this.endPointAddress=endPointAddress;
      this.uniqueKey=uniqueKey;
      this.messageStaging=messageStaging;
   }
   TCPClientConnectionKey(SocketChannel client, int uniqueKey, MessageStaging messageStaging)
   {
      this.client=client;
      this.endPointAddress=client.socket().getRemoteSocketAddress();
      this.informer=null;
      this.uniqueKey=uniqueKey;
      this.messageStaging=messageStaging;
   }
   SocketChannel getClient()
   {
      return client;
   }
   void setSocket(SocketChannel client)
   {
      this.client=client;
   }
   ClientConnectionInformer getInformer()
   {
      return informer;
   }
   public SocketAddress getEndPointAddress()
   {
      return endPointAddress;
   }
   @Override
   public SelectableChannel getSelectableChannel()
   {
      return client;
   }
   @Override
   public int uniqueKey()
   {
      return uniqueKey;
   }
   public MessageStaging getMessageStaging()
   {
      return messageStaging;
   }
   @Override
   public void close() throws NetIOException
   {
      valid=false;
      logger.debug("closing client connection");
      
      if(client != null)
         NIOUtils.closeChannel(client);
      
      messageStaging.close();
   }
   public void setInformer(ClientConnectionInformer informer)
   {
      this.informer=informer;
   }
   @Override
   public String toString()
   {
      return endPointAddress.toString();
   }
   @Override
   public int compareTo(ConnectionKey o)
   {
      return o.uniqueKey()-this.uniqueKey;
   }
   @Override
   public int hashCode()
   {
      return Integer.valueOf(uniqueKey).hashCode();
   }
   @Override
   public boolean isValid()
   {
      return valid;
   }
}
