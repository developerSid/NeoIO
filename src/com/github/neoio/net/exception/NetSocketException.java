package com.github.neoio.net.exception;


public class NetSocketException extends NetIOException
{
   private static final long serialVersionUID=-226305710869794909L;
   
   public NetSocketException()
   {
      super();
   }
   public NetSocketException(String message)
   {
      super(message);
   }
   public NetSocketException(Throwable cause)
   {
      super(cause);
   }
   public NetSocketException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
