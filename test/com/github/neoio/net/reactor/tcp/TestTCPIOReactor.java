package com.github.neoio.net.reactor.tcp;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.BasicConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.neoio.net.message.staging.file.FileMessageStagingFactory;


public class TestTCPIOReactor
{
   private static final String smallMessage="Hello World";
   private static final String largeMessage=
   "Donec leo libero, convallis ac pellentesque faucibus, adipiscing at urna. Ut quis nibh lacus, sed aliquet tellus. " +
   "Maecenas dictum neque in libero ultricies lacinia. Praesent vitae est mollis risus porta vestibulum non et lorem. " +
   "Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Praesent malesuada tempus " +
   "odio quis consequat. Sed interdum mollis condimentum. Fusce tortor neque, condimentum in accumsan vel, feugiat nec" +
   " erat. Curabitur lacus sapien, placerat sit amet volutpat sed, pulvinar placerat mi. Nunc in erat libero. Maecenas" +
   " at nunc non nisl feugiat feugiat. Vivamus sit amet augue vitae purus mattis tempor. Cum sociis natoque penatibus " +
   "et magnis dis parturient montes, nascetur ridiculus mus. Curabitur blandit lacus eget orci tempor molestie. Vestib" +
   "ulum dolor justo, fringilla eu ullamcorper eu, ultricies ut mi. Quisque bibendum diam nec justo accumsan eget iacu" +
   "lis lacus venenatis.some extra stuffPellentesque sollicitudin adipiscing lectus ac eleifend. Ut sagittis lacus id " +
   "ipsum dignissim eleifend. Praesent enim dui, tempus nec interdum ac, porta quis diam. Nullam erat eros, fermentum " +
   "a rhoncus non, egestas eu massa. Morbi lectus turpis, aliquet eget pellentesque in, pellentesque ac quam. Aliquam " +
   "aliquet vehicula mattis. Sed quis sagittis diam. Donec a rhoncus libero. Curabitur neque eros, interdum vitae sagi" +
   "ttis id, consequat vitae odio. Ut vitae volutpat erat. Phasellus non hendrerit mauris.that goes hereDonec dictum i" +
   "psum et risus mattis gravida. Nullam mauris libero, dictum at fringilla in, hendrerit eleifend massa. In quis odio" +
   " nec nisi ullamcorper porttitor. Sed sed diam arcu. Mauris sollicitudin vestibulum tempor. Quisque tincidunt egest" +
   "as magna nec dapibus. Suspendisse potenti. Donec at leo sed libero egestas ullamcorper a quis ante. Mauris ut ipsu" +
   "m tellus, eu interdum nisl. In venenatis velit at turpis aliquet luctus. Etiam pellentesque felis a ligula cursus " +
   "a vestibulum quam pellentesque. Maecenas condimentum tincidunt volutpat. Sed quis hendrerit leo. Etiam venenatis j" +
   "usto sit amet arcu tincidunt pellentesque. Ut ornare mi at tortor hendrerit condimentum. Morbi nulla ligula, inter" +
   "dum eu iaculis eget, eleifend eu diam. Suspendisse potenti.how funCurabitur laoreet risus at ipsum congue in biben" +
   "dum mauris pulvinar. In volutpat ultricies eros eget suscipit. Curabitur hendrerit iaculis mauris. Duis non diam e" +
   "get turpis posuere ultricies. Aliquam dignissim, quam scelerisque facilisis hendrerit, risus libero varius metus, " +
   "a feugiat lectus augue faucibus tellus. Sed vitae odio tortor, ullamcorper elementum dui. Suspendisse potenti. Dui" +
   "s ullamcorper velit sed dui pellentesque lacinia. Nunc eleifend, nulla nec vehicula rutrum, mi orci ullamcorper qu" +
   "am, fringilla venenatis massa augue non felis. Integer ac massa et metus malesuada molestie vitae id velit. Aliqua" +
   "m erat volutpat. Pellentesque in mattis arcu. Fusce felis risus, pulvinar non commodo non, convallis vitae nulla. " +
   "Vestibulum ut est eget velit mattis tristique at eu felis. In ipsum elit, fermentum nec eleifend sed, rutrum a qua" +
   "m. Sed semper fringilla nunc, nec luctus urna ultrices eget. Donec ac justo ut dolor egestas iaculis. Suspendisse " +
   "nisi mauris, tempus ut dictum a, aliquam nec justo.Cool stuffCurabitur risus justo, dignissim vel porta a, ultrice" +
   "s nec nunc. Duis bibendum, elit sed egestas elementum, dolor dolor rhoncus urna, ut mollis nisl magna luctus nulla" +
   ". Sed aliquam nisl sed ante volutpat pellentesque. Donec et ipsum nisi. Duis fringilla sodales est at vehicula. Ve" +
   "stibulum eleifend magna in dui congue blandit. Pellentesque at purus at augue mattis semper ut sit amet magna. Qui" +
   "sque urna dui, elementum quis convallis nec, consectetur nec eros. Aliquam odio neque, suscipit fringilla lobortis" +
   " eu, bibendum vitae libero. Vestibulum lacus enim, pellentesque ut tempor ac, dictum id neque. Aliquam venenatis e" +
   "uismod nulla, ac faucibus leo consequat vitae. Cum sociis natoque penatibus et magnis dis parturient montes, nasce" +
   "tur ridiculus mus. Sed quis ante sed nulla tincidunt cursus. Morbi non erat ac nulla tempor iaculis sed in risus. " +
   "Fusce auctor, quam in fermentum turother cool stuff";
   public static String charsetName="UTF-16";
   public static Charset charset=Charset.forName(charsetName);
   
   @BeforeClass
   public static void beforeClass()
   {
      BasicConfigurator.configure();
   }
   @Test
   public void testSmallMessage()throws Exception
   {
      TCPServer server=new TCPServer(10001, 1);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 0);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(smallMessage);
         server.awaitMessages();
         Assert.assertEquals(smallMessage, server.getMessages().get(0));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testLargeMessage()throws Exception
   {
      TCPServer server=new TCPServer(10001, 1);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 0);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(largeMessage);
         server.awaitMessages();
         Assert.assertEquals(largeMessage, server.getMessages().get(0));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testTenSmallMessages()throws Exception
   {
      TCPServer server=new TCPServer(10001, 10);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 0);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(smallMessage + "1");
         client.sendMessage(smallMessage + "2");
         client.sendMessage(smallMessage + "3");
         client.sendMessage(smallMessage + "4");
         client.sendMessage(smallMessage + "5");
         client.sendMessage(smallMessage + "6");
         client.sendMessage(smallMessage + "7");
         client.sendMessage(smallMessage + "8");
         client.sendMessage(smallMessage + "9");
         client.sendMessage(smallMessage + "10");
         server.awaitMessages();
         Assert.assertEquals(10, server.getMessages().size());
         Assert.assertEquals(smallMessage + "1", server.getMessages().get(0));
         Assert.assertEquals(smallMessage + "2", server.getMessages().get(1));
         Assert.assertEquals(smallMessage + "3", server.getMessages().get(2));
         Assert.assertEquals(smallMessage + "4", server.getMessages().get(3));
         Assert.assertEquals(smallMessage + "5", server.getMessages().get(4));
         Assert.assertEquals(smallMessage + "6", server.getMessages().get(5));
         Assert.assertEquals(smallMessage + "7", server.getMessages().get(6));
         Assert.assertEquals(smallMessage + "8", server.getMessages().get(7));
         Assert.assertEquals(smallMessage + "9", server.getMessages().get(8));
         Assert.assertEquals(smallMessage + "10", server.getMessages().get(9));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testTenLargeMessages()throws Exception
   {
      TCPServer server=new TCPServer(10001, 10);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 0);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(largeMessage + "1");
         client.sendMessage(largeMessage + "2");
         client.sendMessage(largeMessage + "3");
         client.sendMessage(largeMessage + "4");
         client.sendMessage(largeMessage + "5");
         client.sendMessage(largeMessage + "6");
         client.sendMessage(largeMessage + "7");
         client.sendMessage(largeMessage + "8");
         client.sendMessage(largeMessage + "9");
         client.sendMessage(largeMessage + "10");
         server.awaitMessages();
         Assert.assertEquals(10, server.getMessages().size());
         Assert.assertEquals(largeMessage + "1", server.getMessages().get(0));
         Assert.assertEquals(largeMessage + "2", server.getMessages().get(1));
         Assert.assertEquals(largeMessage + "3", server.getMessages().get(2));
         Assert.assertEquals(largeMessage + "4", server.getMessages().get(3));
         Assert.assertEquals(largeMessage + "5", server.getMessages().get(4));
         Assert.assertEquals(largeMessage + "6", server.getMessages().get(5));
         Assert.assertEquals(largeMessage + "7", server.getMessages().get(6));
         Assert.assertEquals(largeMessage + "8", server.getMessages().get(7));
         Assert.assertEquals(largeMessage + "9", server.getMessages().get(8));
         Assert.assertEquals(largeMessage + "10", server.getMessages().get(9));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testSmallBackAndForth()
   {
      TCPServer server=new TCPServer(10001, 1);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 1);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(smallMessage);
         server.awaitMessages();
         
         Assert.assertEquals(1, server.getMessages().size());
         Assert.assertEquals(smallMessage, server.getMessages().get(0));
         
         server.sendMessage(smallMessage);
         client.awaitMessages();
         
         Assert.assertEquals(1, client.getMessages().size());
         Assert.assertEquals(smallMessage, client.getMessages().get(0));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testLargeBackAndForth()
   {
      TCPServer server=new TCPServer(10001, 1);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 1);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(largeMessage);
         server.awaitMessages();
         
         Assert.assertEquals(1, server.getMessages().size());
         Assert.assertEquals(largeMessage, server.getMessages().get(0));
         
         server.sendMessage(largeMessage);
         client.awaitMessages();
         
         Assert.assertEquals(1, client.getMessages().size());
         Assert.assertEquals(largeMessage, client.getMessages().get(0));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testSmallInterspercedWithLarge()
   {
      TCPServer server=new TCPServer(10001, 44);
      TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 44);
      server.start();
      client.start();
      
      try
      {
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(largeMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(largeMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(largeMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(largeMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         client.sendMessage(smallMessage);
         
         server.awaitMessages();
         Assert.assertEquals(44, server.getMessages().size());
         Assert.assertEquals(smallMessage, server.getMessages().get(0));
         Assert.assertEquals(smallMessage, server.getMessages().get(1));
         Assert.assertEquals(smallMessage, server.getMessages().get(2));
         Assert.assertEquals(smallMessage, server.getMessages().get(3));
         Assert.assertEquals(smallMessage, server.getMessages().get(4));
         Assert.assertEquals(smallMessage, server.getMessages().get(5));
         Assert.assertEquals(largeMessage, server.getMessages().get(6));
         Assert.assertEquals(smallMessage, server.getMessages().get(7));
         Assert.assertEquals(smallMessage, server.getMessages().get(8));
         Assert.assertEquals(smallMessage, server.getMessages().get(9));
         Assert.assertEquals(smallMessage, server.getMessages().get(10));
         Assert.assertEquals(smallMessage, server.getMessages().get(11));
         Assert.assertEquals(smallMessage, server.getMessages().get(12));
         Assert.assertEquals(smallMessage, server.getMessages().get(13));
         Assert.assertEquals(smallMessage, server.getMessages().get(14));
         Assert.assertEquals(smallMessage, server.getMessages().get(15));
         Assert.assertEquals(smallMessage, server.getMessages().get(16));
         Assert.assertEquals(largeMessage, server.getMessages().get(17));
         Assert.assertEquals(smallMessage, server.getMessages().get(18));
         Assert.assertEquals(smallMessage, server.getMessages().get(19));
         Assert.assertEquals(smallMessage, server.getMessages().get(20));
         Assert.assertEquals(smallMessage, server.getMessages().get(21));
         Assert.assertEquals(smallMessage, server.getMessages().get(22));
         Assert.assertEquals(smallMessage, server.getMessages().get(23));
         Assert.assertEquals(smallMessage, server.getMessages().get(24));
         Assert.assertEquals(smallMessage, server.getMessages().get(25));
         Assert.assertEquals(smallMessage, server.getMessages().get(26));
         Assert.assertEquals(smallMessage, server.getMessages().get(27));
         Assert.assertEquals(largeMessage, server.getMessages().get(28));
         Assert.assertEquals(smallMessage, server.getMessages().get(29));
         Assert.assertEquals(smallMessage, server.getMessages().get(30));
         Assert.assertEquals(smallMessage, server.getMessages().get(31));
         Assert.assertEquals(smallMessage, server.getMessages().get(32));
         Assert.assertEquals(smallMessage, server.getMessages().get(33));
         Assert.assertEquals(smallMessage, server.getMessages().get(34));
         Assert.assertEquals(smallMessage, server.getMessages().get(35));
         Assert.assertEquals(smallMessage, server.getMessages().get(36));
         Assert.assertEquals(smallMessage, server.getMessages().get(37));
         Assert.assertEquals(smallMessage, server.getMessages().get(38));
         Assert.assertEquals(largeMessage, server.getMessages().get(39));
         Assert.assertEquals(smallMessage, server.getMessages().get(40));
         Assert.assertEquals(smallMessage, server.getMessages().get(41));
         Assert.assertEquals(smallMessage, server.getMessages().get(42));
         Assert.assertEquals(smallMessage, server.getMessages().get(43));
      }
      finally
      {
         client.stop();
         server.stop();
      }
   }
   @Test
   public void testSmallInterspercedWithLargeWithFileStaging() throws IOException
   {
      File serverStaging=new File(SystemUtils.getJavaIoTmpDir(), "fileStagingTestServer");
      File clientStaging=new File(SystemUtils.getJavaIoTmpDir(), "fileStagingTestClient");
      
      try
      {
         TCPServer server=new TCPServer(10001, 44, new FileMessageStagingFactory(serverStaging));
         TCPClient client=new TCPClient(new InetSocketAddress("localhost", 10001), 44, new FileMessageStagingFactory(clientStaging));
         server.start();
         client.start();
         
         try
         {
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(largeMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(largeMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(largeMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(largeMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            client.sendMessage(smallMessage);
            
            server.awaitMessages();
            Assert.assertEquals(44, server.getMessages().size());
            Assert.assertEquals(smallMessage, server.getMessages().get(0));
            Assert.assertEquals(smallMessage, server.getMessages().get(1));
            Assert.assertEquals(smallMessage, server.getMessages().get(2));
            Assert.assertEquals(smallMessage, server.getMessages().get(3));
            Assert.assertEquals(smallMessage, server.getMessages().get(4));
            Assert.assertEquals(smallMessage, server.getMessages().get(5));
            Assert.assertEquals(largeMessage, server.getMessages().get(6));
            Assert.assertEquals(smallMessage, server.getMessages().get(7));
            Assert.assertEquals(smallMessage, server.getMessages().get(8));
            Assert.assertEquals(smallMessage, server.getMessages().get(9));
            Assert.assertEquals(smallMessage, server.getMessages().get(10));
            Assert.assertEquals(smallMessage, server.getMessages().get(11));
            Assert.assertEquals(smallMessage, server.getMessages().get(12));
            Assert.assertEquals(smallMessage, server.getMessages().get(13));
            Assert.assertEquals(smallMessage, server.getMessages().get(14));
            Assert.assertEquals(smallMessage, server.getMessages().get(15));
            Assert.assertEquals(smallMessage, server.getMessages().get(16));
            Assert.assertEquals(largeMessage, server.getMessages().get(17));
            Assert.assertEquals(smallMessage, server.getMessages().get(18));
            Assert.assertEquals(smallMessage, server.getMessages().get(19));
            Assert.assertEquals(smallMessage, server.getMessages().get(20));
            Assert.assertEquals(smallMessage, server.getMessages().get(21));
            Assert.assertEquals(smallMessage, server.getMessages().get(22));
            Assert.assertEquals(smallMessage, server.getMessages().get(23));
            Assert.assertEquals(smallMessage, server.getMessages().get(24));
            Assert.assertEquals(smallMessage, server.getMessages().get(25));
            Assert.assertEquals(smallMessage, server.getMessages().get(26));
            Assert.assertEquals(smallMessage, server.getMessages().get(27));
            Assert.assertEquals(largeMessage, server.getMessages().get(28));
            Assert.assertEquals(smallMessage, server.getMessages().get(29));
            Assert.assertEquals(smallMessage, server.getMessages().get(30));
            Assert.assertEquals(smallMessage, server.getMessages().get(31));
            Assert.assertEquals(smallMessage, server.getMessages().get(32));
            Assert.assertEquals(smallMessage, server.getMessages().get(33));
            Assert.assertEquals(smallMessage, server.getMessages().get(34));
            Assert.assertEquals(smallMessage, server.getMessages().get(35));
            Assert.assertEquals(smallMessage, server.getMessages().get(36));
            Assert.assertEquals(smallMessage, server.getMessages().get(37));
            Assert.assertEquals(smallMessage, server.getMessages().get(38));
            Assert.assertEquals(largeMessage, server.getMessages().get(39));
            Assert.assertEquals(smallMessage, server.getMessages().get(40));
            Assert.assertEquals(smallMessage, server.getMessages().get(41));
            Assert.assertEquals(smallMessage, server.getMessages().get(42));
            Assert.assertEquals(smallMessage, server.getMessages().get(43));
         }
         finally
         {
            client.stop();
            server.stop();
         }
      }
      finally
      {
         FileUtils.deleteDirectory(serverStaging);
         FileUtils.deleteDirectory(clientStaging);
      }
   }
}
