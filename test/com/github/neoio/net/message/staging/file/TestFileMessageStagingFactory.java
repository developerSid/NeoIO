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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.message.staging.file.FileMessageStagingFactory;


public class TestFileMessageStagingFactory
{
   private File tempDir=new File(SystemUtils.getJavaIoTmpDir(), "TempDir");
   private FileMessageStagingFactory factory=new FileMessageStagingFactory(tempDir);
   
   @Test
   public void testSimple()
   {
      MessageStaging staging=factory.newInstance();
      File stagingDir=new File(tempDir, "DistributedServicesIOStaging0");
      
      try
      {
         Assert.assertTrue(stagingDir.exists());
         Assert.assertTrue(new File(stagingDir, "lock.lock").exists());
         Assert.assertTrue(new File(stagingDir, "primary.bin").exists());
         Assert.assertTrue(new File(stagingDir, "readStage.bin").exists());
         Assert.assertTrue(new File(stagingDir, "writeStage.bin").exists());
      }
      finally
      {
         staging.close();
      }
   }
   @Test
   public void testTwo()
   {
      MessageStaging staging0=factory.newInstance();
      File stagingDir0=new File(tempDir, "DistributedServicesIOStaging0");
      
      try
      {
         MessageStaging staging1=factory.newInstance();
         File stagingDir1=new File(tempDir, "DistributedServicesIOStaging1");
         
         try
         {
            Assert.assertTrue(stagingDir0.exists());
            Assert.assertTrue(new File(stagingDir0, "lock.lock").exists());
            Assert.assertTrue(new File(stagingDir0, "primary.bin").exists());
            Assert.assertTrue(new File(stagingDir0, "readStage.bin").exists());
            Assert.assertTrue(new File(stagingDir0, "writeStage.bin").exists());
            
            Assert.assertTrue(stagingDir1.exists());
            Assert.assertTrue(new File(stagingDir1, "lock.lock").exists());
            Assert.assertTrue(new File(stagingDir1, "primary.bin").exists());
            Assert.assertTrue(new File(stagingDir1, "readStage.bin").exists());
            Assert.assertTrue(new File(stagingDir1, "writeStage.bin").exists());
         }
         finally
         {
            staging1.close();
         }
      }
      finally
      {
         staging0.close();
      }
   }
   @After
   public void after() throws IOException
   {
      FileUtils.deleteDirectory(tempDir);
   }
}
