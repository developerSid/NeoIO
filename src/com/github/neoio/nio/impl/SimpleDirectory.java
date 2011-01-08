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
