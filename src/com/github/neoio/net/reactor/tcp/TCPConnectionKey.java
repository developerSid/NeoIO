package com.github.neoio.net.reactor.tcp;

import java.nio.channels.SelectableChannel;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.exception.NetIOException;


interface TCPConnectionKey extends ConnectionKey
{
   public SelectableChannel getSelectableChannel();
   public void close()throws NetIOException;
}
