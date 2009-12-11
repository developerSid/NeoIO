package com.github.neoio.net.reactor.mock;

import java.nio.ByteBuffer;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.Message;


public class MockMessage implements Message
{
   @Override
   public long bytesLeft()
   {
      return 0;
   }
   @Override
   public void finished() throws NetIOException
   {

   }
   @Override
   public int writeToBuffer(ByteBuffer packetBuffer) throws NetIOException
   {
      return 0;
   }
   @Override
   public long size()
   {
      return 0;
   }
   @Override
   public void erroredWhileSending(Exception e) throws NetIOException
   {
      
   }
}
