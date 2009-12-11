package com.github.neoio.net.reactor.tcp;

enum PacketDescriptor
{
   LAST_PACKET(1),
   POLL_PACKET(1),
   BODY_LENGTH(4),
   BODY(4096),
   PACKET(LAST_PACKET.value+POLL_PACKET.value+BODY_LENGTH.value+BODY.value);
   
   private int value;
   
   private PacketDescriptor(int value)
   {
      this.value=value;
   }
   public int value()
   {
      return this.value;
   }
}
