package com.github.neoio.nio;

import java.io.File;

import com.github.neoio.net.exception.NetIOException;

/**
 * This class is used in place of a {@link File} pointing at a directory mostly for organizational purposes to signify 
 * that this is an actual directory since the {@link File} doesn't really differentiate that without a method call.
 * @author myersgw
 *
 */
public interface Directory
{
   /**
    * Returns a {@link File} pointed at a directory that has been created.
    * @return
    */
   public File toFile()throws NetIOException;
}
