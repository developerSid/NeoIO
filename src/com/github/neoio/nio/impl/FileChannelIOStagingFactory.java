package com.github.neoio.nio.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.math.JVMRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.nio.Directory;
import com.github.neoio.nio.IOStaging;
import com.github.neoio.nio.IOStagingFactory;

public class FileChannelIOStagingFactory implements IOStagingFactory
{
   private static JVMRandom random=new JVMRandom();
   private Logger logger=LoggerFactory.getLogger(this.getClass()); 
   private File baseStagingDir;
   
   public FileChannelIOStagingFactory()
   {
      this(new SimpleDirectory(SystemUtils.JAVA_IO_TMPDIR));
   }
   public FileChannelIOStagingFactory(Directory baseStagingDir)
   {
      this.baseStagingDir=baseStagingDir.toFile();
   }
   @Override
   public IOStaging newInstance()throws NetIOException
   {
      File file;
      
      do
      {
         file=new File(baseStagingDir, random.nextLong() + ".stg");
      }while(file.exists() == true);
      
      logger.debug("Creating staging file: {}", file.getPath());
      
      return new FileChannelIOStaging(file);
   }
   @Override
   public void close() throws IOException
   {
      FileUtils.deleteDirectory(baseStagingDir);
   }
   
   //bean setters
   public void setBaseStagingDir(Directory baseStagingDir)
   {
      this.baseStagingDir=baseStagingDir.toFile();
   }
}
