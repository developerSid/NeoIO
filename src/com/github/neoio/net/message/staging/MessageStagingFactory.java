package com.github.neoio.net.message.staging;

import com.github.neoio.net.exception.NetIOException;

public interface MessageStagingFactory
{
   public MessageStaging newInstance()throws NetIOException;
}
