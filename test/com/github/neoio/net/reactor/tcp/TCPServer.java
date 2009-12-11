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
