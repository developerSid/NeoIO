package com.github.neoio.net.message.staging.file;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.message.staging.spi.AbstractMessageStaging;
import com.github.neoio.nio.IOStaging;
import com.github.neoio.nio.impl.FileChannelIOStaging;
import com.github.neoio.nio.util.NIOUtils;

class FileMessageStaging extends AbstractMessageStaging implements MessageStaging
{
   private Logger logger=LoggerFactory.getLogger(this.getClass());
   private File stagingDir;
   private FileLock lock;
   private IOStaging primaryStageFile;
   private IOStaging readStageFile;
   private IOStaging writeStageFile;
   private File lockFile;
   
   FileMessageStaging(File baseStagingDir)throws NetIOException
   {
      int counter=0;
      
      logger.debug("Creating file message staging");
      lock=null;
      
      do
      {
         stagingDir=new File(baseStagingDir, "DistributedServicesIOStaging" + counter);
         logger.debug("checking directory: {}", stagingDir.getAbsolutePath());
         
         if(stagingDir.exists() == false)
         {
            logger.debug("Staging directory did not exist.  Creating: {}", stagingDir.getAbsolutePath());
            stagingDir.mkdirs();
         }
         
         lockFile=new File(stagingDir, "lock.lock");
         logger.debug("Attempting lock of file: {}", lockFile.getAbsolutePath());
         lock=NIOUtils.tryLock(lockFile);
         
         if(lock == null)
            logger.debug("Unable to get lock on file: {}", lockFile.getAbsolutePath());
         
         counter++;
      }while(lock == null);
      
      logger.debug("Locked file: {}", lockFile.getAbsolutePath());
      logger.debug("creating primary.bin in: {}", stagingDir.getAbsolutePath());
      primaryStageFile=new FileChannelIOStaging(new File(stagingDir, "primary.bin"));
      logger.debug("creating readStage.bin in: {}", stagingDir.getAbsolutePath());
      readStageFile=new FileChannelIOStaging(new File(stagingDir, "readStage.bin"));
      logger.debug("creating writeStage.bin in: {}", stagingDir.getAbsolutePath());
      writeStageFile=new FileChannelIOStaging(new File(stagingDir, "writeStage.bin"));
   }
   @Override
   public void close()throws NetIOException
   {
      logger.debug("Cleaning staging dir: {}", stagingDir.getAbsolutePath());
      NIOUtils.closeChannel(primaryStageFile);
      NIOUtils.closeChannel(readStageFile);
      NIOUtils.closeChannel(writeStageFile);
      FileChannel channel=lock.channel();
      NIOUtils.releaseFileLock(lock);
      NIOUtils.closeChannel(channel);
      logger.debug(channel.toString());
      logger.debug(lock.toString());
      NIOUtils.deleteDir(stagingDir);
   }
   private int readChannel(IOStaging channel, ByteBuffer buffer)throws NetIOException
   {
      channel.reset();
      return NIOUtils.readToBuffer(channel, buffer);
   }
   private boolean hasTempBytes(IOStaging staging)throws NetIOException
   {
      return staging.size() > 0;
   }
   @Override
   public boolean hasTempWriteBytes()throws NetIOException
   {
      return hasTempBytes(writeStageFile);
   }
   @Override
   public int readTempWriteBytes(ByteBuffer buffer)throws NetIOException
   {
      return readChannel(writeStageFile, buffer);
   }
   @Override
   public int writeTempWriteBytes(ByteBuffer buffer)throws NetIOException
   {
      return NIOUtils.writeToChannel(writeStageFile, buffer);
   }
   @Override
   public void resetTempWriteBytes()throws NetIOException
   {
      writeStageFile.clear();
   }
   
   @Override
   public boolean hasTempReadBytes()throws NetIOException
   {
      return hasTempBytes(readStageFile);
   }
   @Override
   public int readTempReadBytes(ByteBuffer buffer) throws NetIOException
   {
      return readChannel(readStageFile, buffer);
   }
   @Override
   public void resetTempReadBytes() throws NetIOException
   {
      readStageFile.clear();
   }
   @Override
   public int writeTempReadBytes(ByteBuffer buffer) throws NetIOException
   {
      return NIOUtils.writeToChannel(readStageFile, buffer);
   }
   
   @Override
   public void writePrimaryStaging(ByteBuffer buffer, int length) throws NetIOException
   {
      NIOUtils.writeToChannel(primaryStageFile, buffer.array(), buffer.position(), length);
   }
   @Override
   public void resetPrimaryStage()throws NetIOException
   {
      primaryStageFile.clear();
   }
   @Override
   public long getPrimaryStageSize() throws NetIOException
   {
      return primaryStageFile.size();
   }
   @Override
   protected ReadableByteChannel getPrimaryStageChannel()
   {
      primaryStageFile.reset();
      return primaryStageFile;
   }
}
