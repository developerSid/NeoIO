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
package com.github.neoio.net.reactor.request;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.neoio.net.reactor.mock.MockConnectionKey;
import com.github.neoio.net.reactor.mock.MockMessage;
import com.github.neoio.net.reactor.request.SendRequestQueue;
import com.github.neoio.net.reactor.request.impl.SendRequestQueueImpl;


public class TestSendRequestQueue
{
   private SendRequestQueue queue;
   
   @Before
   public void setUp()
   {
      this.queue=new SendRequestQueueImpl();
   }
   @Test
   public void testAdd()
   {
      MockConnectionKey key=new MockConnectionKey(1);
      MockMessage request1=new MockMessage();
      MockMessage request2=new MockMessage();
      
      queue.add(key, request1);
      queue.add(key, request2);
      
      Assert.assertTrue(queue.getCurrent(key) == request1);
   }
   @Test
   public void testAddRemove()
   {
      MockConnectionKey key=new MockConnectionKey(1);
      MockMessage request1=new MockMessage();
      MockMessage request2=new MockMessage();
      
      queue.add(key, request1);
      queue.add(key, request2);
      
      Assert.assertTrue(queue.getCurrent(key) == request1);
      Assert.assertTrue(queue.removeCurrent(key) == request1);
      Assert.assertTrue(queue.getCurrent(key) == request2);
   }
   @Test
   public void testAddRemoveMultiple()
   {
      MockConnectionKey key=new MockConnectionKey(1);
      MockMessage m1=new MockMessage();
      MockMessage m2=new MockMessage();
      MockMessage m3=new MockMessage();
      
      queue.add(key, m1);
      queue.add(key, m2);
      Assert.assertTrue(queue.getCurrent(key) == m1);
      Assert.assertTrue(m1 == queue.removeCurrent(key));
      queue.add(key, m3);
      Assert.assertTrue(queue.getCurrent(key) == m2);
      Assert.assertTrue(queue.removeCurrent(key) == m2);
      Assert.assertTrue(queue.getCurrent(key) == m3);
      Assert.assertTrue(queue.removeCurrent(key) == m3);
      Assert.assertNull(queue.getCurrent(key));
   }
   @Test
   public void testAddRemoveContains()
   {
      MockConnectionKey key=new MockConnectionKey(1);
      MockMessage request1=new MockMessage();
      MockMessage request2=new MockMessage();
      
      queue.add(key, request1);
      queue.add(key, request2);
      
      Assert.assertTrue(queue.getCurrent(key) == request1);
      Assert.assertTrue(queue.removeCurrent(key) == request1);
      Assert.assertTrue(queue.getCurrent(key) == request2);
      Assert.assertTrue(queue.removeCurrent(key) == request2);
      
      Assert.assertFalse(queue.contains(key));
   }
   @Test
   public void testNotFoundReturnsNull()
   {
      MockConnectionKey key=new MockConnectionKey(1);
      MockMessage request1=new MockMessage();
      MockMessage request2=new MockMessage();
      
      queue.add(key, request1);
      queue.add(key, request2);
      
      Assert.assertTrue(queue.getCurrent(key) == request1);
      Assert.assertTrue(queue.removeCurrent(key) == request1);
      Assert.assertTrue(queue.getCurrent(key) == request2);
      Assert.assertTrue(queue.removeCurrent(key) == request2);
      
      Assert.assertFalse(queue.contains(key));
      Assert.assertNull(queue.getCurrent(key));
   }
   @Test
   public void testClear()
   {
      MockConnectionKey key=new MockConnectionKey(1);
      MockMessage request1=new MockMessage();
      MockMessage request2=new MockMessage();
      
      queue.add(key, request1);
      queue.add(key, request2);
      
      Assert.assertTrue(queue.contains(key));
      queue.clear(key);
      Assert.assertFalse(queue.contains(key));
   }
   @After
   public void tearDown()
   {
      this.queue=null;
   }
}
