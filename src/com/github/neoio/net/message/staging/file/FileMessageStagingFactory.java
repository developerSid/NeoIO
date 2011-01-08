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
package com.github.neoio.net.message.staging.file;

import java.io.File;


import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.message.staging.MessageStagingFactory;

public class FileMessageStagingFactory implements MessageStagingFactory
{
   private Logger logger=LoggerFactory.getLogger(this.getClass());
   private File stagingDir;
   
   public FileMessageStagingFactory()throws NetIOException
   {
      this(SystemUtils.getJavaIoTmpDir());
   }
   public FileMessageStagingFactory(File stagingDir)throws NetIOException
   {
      logger.debug("Factory staging directory: {}", stagingDir.getAbsolutePath());
      
      if(stagingDir.exists() == true && stagingDir.isDirectory() == false)
         throw new NetIOException("stagingDir [" + stagingDir.getName() + "] exists but is not a directory");
      else if(stagingDir.exists() == false)
         stagingDir.mkdirs();
      
      this.stagingDir=stagingDir;
   }
   @Override
   public MessageStaging newInstance()throws NetIOException
   {
      return new FileMessageStaging(stagingDir);
   }
}
