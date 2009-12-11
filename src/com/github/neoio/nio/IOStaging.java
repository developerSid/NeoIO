package com.github.neoio.nio;

import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import com.github.neoio.net.exception.NetIOException;

public interface IOStaging extends Channel, ReadableByteChannel, WritableByteChannel
{
   public void clear() throws NetIOException;
   public void reset() throws NetIOException;
   public long size() throws NetIOException;
   public long transferTo(long position, long count, WritableByteChannel target) throws NetIOException;
   public long transferFrom(ReadableByteChannel src, long position, long count) throws NetIOException;
}
