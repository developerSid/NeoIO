package com.github.neoio.net.message.staging;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

import com.github.neoio.net.exception.NetIOException;


public interface MessageStaging
{
   public void resetPrimaryStage()throws NetIOException;
   public void close()throws NetIOException;
   public boolean hasTempWriteBytes()throws NetIOException;
   public int readTempWriteBytes(ByteBuffer buffer)throws NetIOException;
   public int writeTempWriteBytes(ByteBuffer buffer)throws NetIOException;
   public void setWriteLastPacket(boolean writeLastPacket);
   boolean isWriteLastPacket();
   public void resetTempWriteBytes()throws NetIOException;
   public boolean hasTempReadBytes()throws NetIOException;
   public int readTempReadBytes(ByteBuffer buffer)throws NetIOException;
   public void resetTempReadBytes()throws NetIOException;
   public int writeTempReadBytes(ByteBuffer buffer)throws NetIOException;
   public void writePrimaryStaging(ByteBuffer buffer, int bodyLength)throws NetIOException;
   public ReadableByteChannel getPrimaryStage()throws NetIOException;
   public long getPrimaryStageSize()throws NetIOException;
}
