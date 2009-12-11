package com.github.neoio.net.message.staging.spi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

class UnCloseableReadableByteChannel implements ReadableByteChannel
{
   private ReadableByteChannel channel;
   
   UnCloseableReadableByteChannel(ReadableByteChannel channel)throws NullPointerException
   {
      if(channel == null)
         throw new NullPointerException("channel cannot be nul;");
      
      this.channel=channel;
   }
   public int read(ByteBuffer dst) throws IOException
   {
      return channel.read(dst);
   }
   public void close() throws IOException
   {

   }
   public boolean isOpen()
   {
      return channel.isOpen();
   }
}
