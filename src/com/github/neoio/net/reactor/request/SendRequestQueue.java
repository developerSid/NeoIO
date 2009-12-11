package com.github.neoio.net.reactor.request;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.message.Message;

public interface SendRequestQueue
{
   public Message getCurrent(ConnectionKey connectionKey);
   public Message removeCurrent(ConnectionKey connectionKey);
   public void add(ConnectionKey connectionKey, Message message);
   public boolean contains(ConnectionKey connectionKey);
   public void clear(ConnectionKey connectionKey);
}
