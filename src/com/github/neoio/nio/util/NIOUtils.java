package com.github.neoio.nio.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetFileNotFoundException;
import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.exception.NetSelectorException;
import com.github.neoio.net.exception.NetSocketException;
import com.github.neoio.nio.IOStaging;
import com.github.neoio.nio.IOStagingFactory;
import com.github.neoio.nio.StringReadableChannel;

public class NIOUtils
{
   private static Logger logger=LoggerFactory.getLogger(NIOUtils.class);
   
   public static Selector selectorOpen() throws NetSelectorException
   {
      try
      {
         return Selector.open();
      }
      catch(IOException e)
      {
         throw new NetSelectorException(e);
      }
   }
   public static void selectorClose(Selector selector) throws NetSelectorException
   {
      try
      {
         selector.close();
      }
      catch(IOException e)
      {
         throw new NetSelectorException(e);
      }
   }
   public static ServerSocketChannel openServerSocket(Selector selector, SocketAddress socketAddress) throws NetSocketException
   {
      ServerSocketChannel toReturn;
      
      try
      {
         toReturn=ServerSocketChannel.open();
         toReturn.socket().bind(socketAddress);
         toReturn.configureBlocking(false);
         toReturn.register(selector, SelectionKey.OP_ACCEPT);
      }
      catch(IOException e)
      {
         logger.error("IOException occurred while opening server socket", e);
         toReturn=null;
      }
      
      return toReturn;
   }
   public static SocketChannel openClientSocket(Selector selector, SocketAddress endPointAddress)throws NetSocketException
   {
      SocketChannel toReturn;
      
      try
      {
         toReturn=SocketChannel.open(endPointAddress);
         toReturn.configureBlocking(false);
         toReturn.register(selector, SelectionKey.OP_READ);
      }
      catch(IOException e)
      {
         logger.error("IOException occurred while opening client socket", e);
         toReturn=null;
      }
      
      return toReturn;
   }
   public static FileChannel openFileChannel(File file) throws NetFileNotFoundException
   {
      try
      {
         return new RandomAccessFile(file, "rw").getChannel();
      }
      catch(FileNotFoundException e)
      {
         throw new NetFileNotFoundException(file + " not found", e);
      }
   }
   public static int writeToChannel(WritableByteChannel channel, byte array[], int offset, int length) throws NetIOException
   {
      try
      {
         return channel.write(ByteBuffer.wrap(array, offset, length));
      }
      catch(IndexOutOfBoundsException e)
      {
         throw new NetIOException(e);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   public static void closeChannel(Channel channel) throws NetIOException
   {
      try
      {
         channel.close();
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   public static void resetFileChannel(FileChannel channel) throws NetIOException
   {
      try
      {
         channel.position(0);
         channel.truncate(0);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   public static void resetFileChannelForReading(FileChannel channel)throws NetIOException
   {
      try
      {
         channel.force(true);
         channel.position(0);
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   public static ByteBuffer readChannelToBuffer(ReadableByteChannel channel)throws NetIOException
   {
      ByteArrayOutputStream bos=new ByteArrayOutputStream();
      
      try
      {
         logger.debug("bytes read from channel: " + IOUtils.copy(Channels.newInputStream(channel), bos));
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
      
      return ByteBuffer.wrap(ArrayUtils.subarray(bos.toByteArray(), 0, bos.size()));
   }
   public static FileLock tryLock(File file)
   {
      FileLock toReturn=null;
      
      try
      {
         RandomAccessFile raf=new RandomAccessFile(file, "rw");
         
         try
         {
            FileChannel channel=raf.getChannel();
            toReturn=channel.tryLock();
            raf.writeBytes("lock file for: " + ManagementFactory.getRuntimeMXBean().getName());
         }
         finally
         {
            if(toReturn == null)
               raf.close();
         }
      }
      catch(OverlappingFileLockException e)
      {
         toReturn=null;
      }
      catch(FileNotFoundException e)
      {
         toReturn=null;
      }
      catch(IOException e)
      {
         toReturn=null;
      }
      
      return toReturn;
   }
   public static void releaseFileLock(FileLock lock) throws NetIOException
   {
      try
      {
         lock.release();
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   public static byte createBooleanByte(boolean convert)
   {
      return convert == true ? (byte)1 : (byte)0;
   }
   public static boolean boolValueOf(byte value)
   {
      return value == 1 ? true : false;
   }
   public static int readToBuffer(ReadableByteChannel channel, ByteBuffer buffer) throws NetIOException
   {
      try
      {
         return channel.read(buffer);
      }
      catch(IOException e)
      {
         return -1;
      }
   }
   public static int writeToChannel(WritableByteChannel channel, ByteBuffer buffer) throws NetIOException
   {
      try
      {
         return channel.write(buffer);
      }
      catch(IOException e)
      {
         return -1;
      }
   }
   public static void deleteDir(File dir)throws NetIOException
   {
      try
      {
         FileUtils.deleteDirectory(dir);
      }
      catch(IOException e)
      {
         throw new NetIOException("Error deleting directory", e);
      }
   }
   public static long fileSize(FileChannel channel)throws NetIOException
   {
      try
      {
         return channel.size();
      }
      catch(IOException e)
      {
         throw new NetIOException("Error getting file size", e);
      }
   }
   public static IOStaging createStringIOStaging(String message, Charset charset, IOStagingFactory factory) throws NetIOException
   {
      try
      {
         IOStaging staging=factory.newInstance();
         StringReadableChannel channel=new StringReadableChannel(message, charset.name());
         
         staging.transferFrom(channel, 0, channel.size());
         staging.reset();
         
         return staging;
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
   public static IOStaging createStagingAndCopyFromInputstream(InputStream is, long length, IOStagingFactory factory) throws NetIOException
   {
      IOStaging staging=factory.newInstance();
      ReadableByteChannel channel=Channels.newChannel(is);
      
      staging.transferFrom(channel, 0, length);
      staging.reset();
      
      return staging;
   }
}
