package com.github.neoio.net.exception;


public class NetSelectorException extends NetIOException
{
   private static final long serialVersionUID=3919431770516135253L;

   public NetSelectorException()
   {
      super();
   }
   public NetSelectorException(String message)
   {
      super(message);
   }
   public NetSelectorException(Throwable cause)
   {
      super(cause);
   }
   public NetSelectorException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
