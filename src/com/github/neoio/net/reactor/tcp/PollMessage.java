package com.github.neoio.net.reactor.tcp;

import java.nio.ByteBuffer;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.Message;

class PollMessage implements Message
{
   @Override
   public long bytesLeft()
   {
      return 0;
   }
   @Override
   public void erroredWhileSending(Exception e) throws NetIOException
   {
      
   }
   @Override
   public void finished() throws NetIOException
   {
      
   }
   @Override
   public long size()
   {
      return 0;
   }
   @Override
   public int writeToBuffer(ByteBuffer buffer) throws NetIOException
   {
      return 0;
   }
}
