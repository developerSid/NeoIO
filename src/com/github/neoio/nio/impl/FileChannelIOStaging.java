package com.github.neoio.nio.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.nio.IOStaging;

public class FileChannelIOStaging implements IOStaging
{
   private static Logger logger=LoggerFactory.getLogger(FileChannelIOStaging.class);
   private FileChannel channel;
   private File file;
   
   public FileChannelIOStaging(File file) throws NetIOException
   {
      try
      {
         this.file=file;
         this.channel=new RandomAccessFile(file, "rw").getChannel();
      }
      catch(FileNotFoundException e)
      {
         throw new NetIOException(e);
      }
   }
   @Override
   public int read(ByteBuffer dst) throws IOException
   {
      return channel.read(dst);
   }
   @Override
   public void close() throws IOException
   {
      logger.debug("Closing and deleting staging file: {}", file.getPath());
      channel.close();
      FileUtils.forceDelete(file);
   }
   @Override
   public boolean isOpen()
   {
      return channel.isOpen();
   }
   @Override
   public int write(ByteBuffer src) throws NetIOException
   {
      try
      {
         return channel.write(src);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   @Override
   public void reset() throws NetIOException
   {
      try
      {
         channel.force(false);
         channel.position(0);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   @Override
   public void clear() throws NetIOException
   {
      try
      {
         channel.truncate(0);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   @Override
   public long size() throws NetIOException
   {
      try
      {
         return channel.size();
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   @Override
   public long transferFrom(ReadableByteChannel src, long position, long count) throws NetIOException
   {
      try
      {
         return channel.transferFrom(src, position, count);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   @Override
   public long transferTo(long position, long count, WritableByteChannel target) throws NetIOException
   {
      try
      {
         return channel.transferTo(position, count, target);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
}
