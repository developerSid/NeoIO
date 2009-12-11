package com.github.neoio.net.reactor;

import java.net.SocketAddress;
import java.nio.channels.ReadableByteChannel;

import com.github.neoio.net.connection.ConnectionKey;


public interface ClientConnectionInformer
{
   public void received(ConnectionKey connectionKey, ReadableByteChannel message, long messageSize);
   public void closed(ConnectionKey connectionKey);
   public void unableToConnect(SocketAddress socketAddress);
   public void ableToConnect(SocketAddress endPointAddress);
}
