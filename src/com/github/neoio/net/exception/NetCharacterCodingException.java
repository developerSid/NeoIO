package com.github.neoio.net.exception;

public class NetCharacterCodingException extends NetIOException
{
   private static final long serialVersionUID = -4507136817309081688L;

   public NetCharacterCodingException()
   {
      super();
   }
   public NetCharacterCodingException(String message)
   {
      super(message);
   }
   public NetCharacterCodingException(Throwable cause)
   {
      super(cause);
   }
   public NetCharacterCodingException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
