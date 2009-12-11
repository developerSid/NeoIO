package com.github.neoio.net.message;

import java.nio.ByteBuffer;

import com.github.neoio.net.exception.NetIOException;


public interface Message
{
   public long size();
   public long bytesLeft();
   public int writeToBuffer(ByteBuffer buffer)throws NetIOException;
   public void finished()throws NetIOException;
   public void erroredWhileSending(Exception e)throws NetIOException;
}
