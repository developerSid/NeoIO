package com.github.neoio.nio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class StringReadableChannel implements ReadableByteChannel
{
   private ReadableByteChannel channel;
   private long size;
   
   public StringReadableChannel(String string) throws IOException
   {
      this(string, null);
   }
   public StringReadableChannel(String string, String encoding) throws IOException
   {
      byte bytes[]=encoding != null ? string.getBytes(encoding) : string.getBytes();
      
      this.channel=Channels.newChannel(new ByteArrayInputStream(bytes));
      this.size=bytes.length;
   }
   @Override
   public int read(ByteBuffer dst) throws IOException
   {
      return channel.read(dst);
   }
   @Override
   public void close() throws IOException
   {
      channel.close();
   }
   @Override
   public boolean isOpen()
   {
      return channel.isOpen();
   }
   public long size()
   {
      return size;
   }
}
