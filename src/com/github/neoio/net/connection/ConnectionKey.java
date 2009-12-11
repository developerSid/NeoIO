package com.github.neoio.net.connection;

public interface ConnectionKey extends Comparable<ConnectionKey>
{
   public int uniqueKey();
   public boolean isValid();
}
