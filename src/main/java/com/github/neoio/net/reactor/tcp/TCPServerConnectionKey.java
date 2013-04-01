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
import java.nio.channels.ServerSocketChannel;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.reactor.ServerConnectionInformer;
import com.github.neoio.nio.util.NIOUtils;


class TCPServerConnectionKey implements TCPConnectionKey
{
   private ServerSocketChannel server;
   private ServerConnectionInformer informer;
   private SocketAddress bindAddress;
   private int uniqueKey;
   private boolean valid=true;
   
   TCPServerConnectionKey(ServerConnectionInformer informer, SocketAddress bindAddress, int uniqueId)
   {
      this.server=null;
      this.informer=informer;
      this.bindAddress=bindAddress;
      this.uniqueKey=uniqueId;
   }
   ServerSocketChannel getServer()
   {
      return server;
   }
   void setSocket(ServerSocketChannel server)
   {
      this.server=server;
   }
   ServerConnectionInformer getInformer()
   {
      return informer;
   }
   public SocketAddress getBindAddress()
   {
      return bindAddress;
   }
   @Override
   public SelectableChannel getSelectableChannel()
   {
      return server;
   }
   public int uniqueKey()
   {
      return uniqueKey;
   }
   @Override
   public void close() throws NetIOException
   {
      valid=false;
      NIOUtils.closeChannel(server);
   }
   @Override
   public String toString()
   {
      return bindAddress.toString();
   }
   @Override
   public int compareTo(ConnectionKey o)
   {
      return o.uniqueKey()-uniqueKey;
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
