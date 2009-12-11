package com.github.neoio.net.reactor;

import java.net.SocketAddress;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.Message;


public interface IOReactor
{
   public ConnectionKey connectClient(ClientConnectionInformer informer, SocketAddress endPointAddress)throws NetIOException;
   public void registerConnectionInformer(ConnectionKey key, ClientConnectionInformer informer);
   public ConnectionKey bindServer(ServerConnectionInformer informer, SocketAddress bindAddress)throws NetIOException;
   public void sendMessage(ConnectionKey destination, Message message)throws NetIOException;
   public void disconnect(ConnectionKey key)throws NetIOException;
   public void start();
   public void shutdown();
}
