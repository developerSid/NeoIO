package com.github.neoio.net.exception;

public class NetIOException extends RuntimeException
{
   private static final long serialVersionUID=-1033630038408975629L;

   public NetIOException()
   {
      super();
   }
   public NetIOException(String message)
   {
      super(message);
   }
   public NetIOException(Throwable cause)
   {
      super(cause);
   }
   public NetIOException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
