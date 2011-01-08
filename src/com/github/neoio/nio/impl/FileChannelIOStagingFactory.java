/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
