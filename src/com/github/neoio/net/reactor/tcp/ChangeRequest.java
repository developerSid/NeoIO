package com.github.neoio.net.reactor.tcp;


class ChangeRequest
{
   enum ChangeRequestType{OPEN_CHANNEL, CLOSE_CHANNEL, BIND_SERVER, SEND_MESSAGE, SEND_MESSAGE_FINISHED};
   
   private ChangeRequestType type;
   private TCPConnectionKey key;
   
   ChangeRequest(ChangeRequestType type, TCPConnectionKey key)
   {
      super();
      this.type=type;
      this.key=key;
   }
   ChangeRequestType getType()
   {
      return type;
   }
   TCPConnectionKey getKey()
   {
      return key;
   }
}
