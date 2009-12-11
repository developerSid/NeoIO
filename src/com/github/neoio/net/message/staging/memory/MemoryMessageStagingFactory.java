package com.github.neoio.net.message.staging.memory;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.message.staging.MessageStagingFactory;

public class MemoryMessageStagingFactory implements MessageStagingFactory
{
   @Override
   public MessageStaging newInstance()throws NetIOException
   {
      return new MemoryMessageStaging();
   }
}
