package com.github.neoio.net.reactor.tcp;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.reactor.IOReactor;

class PollTask extends TimerTask
{
   private static Logger logger=LoggerFactory.getLogger(PollTask.class);
   private IOReactor reactor;
   private Selector selector;
   
   public PollTask(IOReactor reactor, Selector selector)
   {
      this.reactor=reactor;
      this.selector=selector;
   }
   @Override
   public void run()
   {
      logger.debug("Running poll task");
      if(selector.isOpen() == true)
      {
         Set<SelectionKey> keys=selector.keys();
         
         for(SelectionKey key : keys)
         {
            logger.debug("key channel: {}", key.channel().getClass().getName());
            if(key.channel() instanceof SocketChannel)
            {
               ConnectionKey connKey=(ConnectionKey) key.attachment();
               logger.debug("sending poll to: {}", connKey);
               reactor.sendMessage(connKey, new PollMessage());
            }
         }
      }
      logger.debug("Finished poll task");
   }
}
