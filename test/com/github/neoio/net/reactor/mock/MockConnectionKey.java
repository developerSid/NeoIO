package com.github.neoio.net.reactor.mock;

import com.github.neoio.net.connection.ConnectionKey;

public class MockConnectionKey implements ConnectionKey
{
   private int uniqueKey;
   
   public MockConnectionKey(int uniqueKey)
   {
      this.uniqueKey=uniqueKey;
   }
   @Override
   public int uniqueKey()
   {
      return uniqueKey;
   }
   @Override
   public int compareTo(ConnectionKey o)
   {
      return o.uniqueKey()-uniqueKey;
   }
   @Override
   public boolean isValid()
   {
      return false;
   }
}
