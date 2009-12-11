package com.github.neoio.net.message.staging.spi;

import java.nio.channels.ReadableByteChannel;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.staging.MessageStaging;


public abstract class AbstractMessageStaging implements MessageStaging
{
   private boolean writeLastPacket;
   
   protected abstract ReadableByteChannel getPrimaryStageChannel();

   @Override
   public void setWriteLastPacket(boolean writeLastPacket)
   {
      this.writeLastPacket=writeLastPacket;
   }
   @Override
   public boolean isWriteLastPacket()
   {
      return writeLastPacket;
   }
   @Override
   public ReadableByteChannel getPrimaryStage() throws NetIOException
   {
      return new UnCloseableReadableByteChannel(getPrimaryStageChannel());
   }
}
