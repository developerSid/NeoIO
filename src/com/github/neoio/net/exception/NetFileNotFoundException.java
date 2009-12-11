package com.github.neoio.net.exception;


public class NetFileNotFoundException extends NetIOException
{
   private static final long serialVersionUID=-8689587160920047168L;

   public NetFileNotFoundException()
   {
      super();
   }
   public NetFileNotFoundException(String message)
   {
      super(message);
   }
   public NetFileNotFoundException(Throwable cause)
   {
      super(cause);
   }
   public NetFileNotFoundException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
