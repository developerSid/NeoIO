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
package com.github.neoio.net.reactor;

import java.net.SocketAddress;

import com.github.neoio.net.connection.ConnectionKey;


public interface ServerConnectionInformer
{
   /**
    * When an {@link IOReactor} receives a new client connection this method is called.
    * @param key the {@link ConnectionKey} that was created when the connection was made.
    * @param socketAddress the port that the connection was received on.
    */
   public void clientConnected(ConnectionKey key, SocketAddress socketAddress);
   /**
    * When the {@link IOReactor} is unable to bind a server to the provided address this
    * method is called to inform the requester of the problem.
    * @param socketAddress the address that the server attempted to bind to.
    */
   public void unableToBindServer(SocketAddress socketAddress);
   /**
    * When an {@link IOReactor} is successfully able to bind a server to the provided {@link SocketAddress}
    * @param bindAddress the address that the server was bound to
    */
   public void serverBound(SocketAddress bindAddress);
}
