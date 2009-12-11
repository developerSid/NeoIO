package com.github.neoio.nio;

import java.io.IOException;


public interface IOStagingFactory
{
   public IOStaging newInstance();
   public void close() throws IOException;
}
