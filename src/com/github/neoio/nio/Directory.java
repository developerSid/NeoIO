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
