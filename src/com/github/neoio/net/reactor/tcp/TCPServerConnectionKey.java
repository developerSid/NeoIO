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
