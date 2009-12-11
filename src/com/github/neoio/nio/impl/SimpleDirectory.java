package com.github.neoio.nio.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.nio.Directory;

public class SimpleDirectory implements Directory
{
   private String dir;
   
   /**
    * A directory will be created based of the simple name provided.
    * @param name
    */
   public SimpleDirectory(String name)throws NetIOException
   {
      this.dir=name;
      checkDirectory();
   }
   /**
    * A directory will be created based off calling <code>toString()</code> on a list of Objects and placing a / after the value returned.
    * Use this method with care
    * @param names
    */
   public SimpleDirectory(List<?> names) throws NetIOException
   {
      StringBuilder dir=new StringBuilder();
      
      for(Object object : names)
         dir.append(object.toString()).append('/');
      
      this.dir=dir.toString();
      
      checkDirectory();
   }
   private void checkDirectory()throws NetIOException
   {
      File check=new File(this.dir);
      
      if(check.exists() == true && check.isDirectory() == false)
         throw new NetIOException(this.dir + " exists and is not a directory");
   }
   @Override
   public File toFile()throws NetIOException
   {
      try
      {
         File file=new File(dir);
         FileUtils.forceMkdir(file);
         return file;
      }
      catch(IOException e)
      {
         throw new NetIOException(e);
      }
   }
}
