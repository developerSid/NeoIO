package com.github.neoio.net.reactor.tcp;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.neoio.net.connection.ConnectionKey;
import com.github.neoio.net.exception.NetIOException;
import com.github.neoio.net.message.Message;
import com.github.neoio.net.message.staging.MessageStaging;
import com.github.neoio.net.message.staging.MessageStagingFactory;
import com.github.neoio.net.reactor.ClientConnectionInformer;
import com.github.neoio.net.reactor.IOReactor;
import com.github.neoio.net.reactor.ServerConnectionInformer;
import com.github.neoio.net.reactor.request.SendRequestQueue;
import com.github.neoio.net.reactor.request.impl.SendRequestQueueImpl;
import com.github.neoio.net.reactor.tcp.ChangeRequest.ChangeRequestType;
import com.github.neoio.nio.util.NIOUtils;

public class TCPIOReactor implements IOReactor, Runnable
{
   private Logger logger=LoggerFactory.getLogger(this.getClass());
   private volatile boolean run=false;
   private AtomicInteger uniqueId=new AtomicInteger(0);
   private ConcurrentLinkedQueue<ChangeRequest> socketChangeRequestQueue=new ConcurrentLinkedQueue<ChangeRequest>();
   private Selector selector;
   private MessageStagingFactory messageStagingFactory;
   private SendRequestQueue sendRequest=new SendRequestQueueImpl();
   private ByteBuffer packetBuffer=ByteBuffer.allocate(PacketDescriptor.PACKET.value());
   private ByteBuffer packetBody=ByteBuffer.allocate(PacketDescriptor.BODY.value());
   private CountDownLatch startupLatch=new CountDownLatch(2);
   private Thread reactorThread;
   private Timer pollTimer=new Timer("Connection Polling", true);
   
   public TCPIOReactor(MessageStagingFactory messageStagingFactory)
   {
      this.messageStagingFactory=messageStagingFactory;
   }
   @Override
   public ConnectionKey bindServer(ServerConnectionInformer informer, SocketAddress bindAddress) throws NetIOException
   {
      TCPServerConnectionKey key;
      
      if(selector == null || run == false || selector.isOpen() == false)
         throw new NetIOException("Reactor not running unable to bind server");
      else
      {
         logger.debug("Binding of TCP server to: {} has been requested", bindAddress.toString());
         key=new TCPServerConnectionKey(informer, bindAddress, uniqueId.incrementAndGet());
         socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.BIND_SERVER, key));
         selector.wakeup();
      }
      
      return key;
   }
   @Override
   public ConnectionKey connectClient(ClientConnectionInformer informer, SocketAddress endPointAddress) throws NetIOException
   {
      TCPClientConnectionKey key;
      
      if(selector == null || selector.isOpen() == false || run == false)
         throw new NetIOException("Reactor not running unable to connect client");
      else
      {
         logger.debug("Connecting client to: {}", endPointAddress.toString());
         key=new TCPClientConnectionKey(informer, endPointAddress, uniqueId.incrementAndGet(), messageStagingFactory.newInstance());
         socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.OPEN_CHANNEL, key));
         selector.wakeup();
      }
      
      return key;
   }
   @Override
   public void registerConnectionInformer(ConnectionKey key, ClientConnectionInformer informer)
   {
      logger.debug("Registering connection informer with: " + key.toString());
      
      if(key instanceof TCPClientConnectionKey)
         ((TCPClientConnectionKey)key).setInformer(informer);
   }
   @Override
   public void disconnect(ConnectionKey key) throws NetIOException
   {
      if(key instanceof TCPConnectionKey)
      {
         socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.CLOSE_CHANNEL, (TCPConnectionKey)key));
         selector.wakeup();
      }
      else
         throw new NetIOException("Unknown key type");
   }
   @Override
   public void sendMessage(ConnectionKey destination, Message message) throws NetIOException
   {
      if(destination.isValid() == true)
      {
         if(destination instanceof TCPClientConnectionKey)
         {
            logger.debug("sending message");
            socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.SEND_MESSAGE, (TCPClientConnectionKey)destination));
            sendRequest.add(destination, message);
            selector.wakeup();
         }
         else
            throw new NetIOException("Unknown destination");
      }
      else
         throw new NetIOException("Destination is not valid");
   }
   @Override
   public void start()
   {
      reactorThread=new Thread(this);
      
      reactorThread.setName("TCP IO Reactor Thread");
      reactorThread.setDaemon(true);
      reactorThread.start();
      startupLatch.countDown();
      try{startupLatch.await();}catch(InterruptedException e){}
      pollTimer.schedule(new PollTask(this, selector), TimeUnit.MINUTES.toMillis(30), TimeUnit.MINUTES.toMillis(30));
      logger.debug("IO Reactor running");
   }
   @Override
   public void run()
   {
      selector=NIOUtils.selectorOpen();
      run=true;
      
      startupLatch.countDown();
      while(run == true)
      {
         try
         {
            logger.debug("Selector awake.  Number of selected keys: {}", selector.select());
            Set<SelectionKey> selectedKeys=selector.selectedKeys();
            
            try
            {
               if(selectedKeys.size() > 0)
               {
                  logger.debug("number of keys: {}", selectedKeys.size());
                  for(Iterator<SelectionKey> i=selectedKeys.iterator(); i.hasNext();)
                  {
                     try
                     {
                        SelectionKey currentKey=i.next();
                        i.remove();
                        logger.debug("SelectableChannel: {}", currentKey.channel().toString());
                        
                        processKey(currentKey);
                     }
                     finally
                     {
                        packetBody.clear();
                        packetBuffer.clear();
                     }
                  }
               }
            }
            finally
            {
               selectedKeys.clear();
               
               processChanges();
            }
         }
         catch(Exception e)
         {
            logger.error("Exception occurred in processing loop", e);
         }
      }
      
      pollTimer.cancel();
      closeConnections(selector.keys());
      
      NIOUtils.selectorClose(selector);
      selector=null;
   }
   private void processKey(SelectionKey currentKey) throws IOException, ClosedChannelException, InterruptedException
   {
      if(currentKey.isValid() == true)
      {
         if(currentKey.isAcceptable() == true)
         {
            logger.debug("Key was acceptable");
            TCPServerConnectionKey server=(TCPServerConnectionKey)currentKey.attachment();
            SocketChannel client=server.getServer().accept();
            
            client.configureBlocking(false);
            SelectionKey key=client.register(selector, SelectionKey.OP_READ, new TCPClientConnectionKey(client, uniqueId.incrementAndGet(), messageStagingFactory.newInstance()));
            client.finishConnect();
            server.getInformer().clientConnected((ConnectionKey)key.attachment(), server.getBindAddress());
         }
         else if(currentKey.isConnectable() == true)
         {
            logger.debug("Key is connectable");
            SocketChannel client=((SocketChannel)currentKey.channel());
            int trys=0;
            
            for(; client.finishConnect() == false && trys < 10; trys++)
               TimeUnit.SECONDS.sleep(2);
            
            if(trys >= 10)
            {
               logger.error("Unable to finish connection for client [{}]. Closing.", client);
               currentKey.cancel();
               client.close();
            }
            else
            {
               currentKey.interestOps(SelectionKey.OP_READ);
               logger.debug("Finished connection");
            }
         }
         else if(currentKey.isReadable() == true)
         {
            logger.debug("Key is readable");
            readPacket((TCPClientConnectionKey)currentKey.attachment());
         }
         else if(currentKey.isWritable() == true)
         {
            logger.debug("Key is writable");
            writePacket((TCPClientConnectionKey)currentKey.attachment());
         }
      }
   }
   private void closeConnections(Set<SelectionKey> keys)
   {
      for(SelectionKey key : keys)
      {
         if(key.attachment() != null)
            ((TCPConnectionKey)key.attachment()).close();
      }
   }
   private void readPacket(TCPClientConnectionKey connectionKey)
   {
      MessageStaging staging=connectionKey.getMessageStaging();

      if(staging.hasTempReadBytes() == true)
      {
         staging.readTempReadBytes(packetBuffer);
         staging.resetTempReadBytes();
      }
      
      SocketChannel socket=connectionKey.getClient();
      int bytesRead=NIOUtils.readToBuffer(socket, packetBuffer);
      
      if(bytesRead == -1) //it was probably closed
      {
         logger.debug("Connection was closed during read");
         sendRequest.clear(connectionKey);
         socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.CLOSE_CHANNEL, connectionKey));
         connectionKey.close();
         
         if(connectionKey.getInformer() != null)
            connectionKey.getInformer().closed(connectionKey);
      }
      else if(packetBuffer.remaining() > 0) //unable to read the entire packet
      {
         packetBuffer.flip();
         staging.writeTempReadBytes(packetBuffer);
      }
      else
      {
         packetBuffer.flip();
         boolean lastPacket=NIOUtils.boolValueOf(packetBuffer.get());
         boolean pollPacket=NIOUtils.boolValueOf(packetBuffer.get());
         int bodyLength=packetBuffer.getInt();
         
         logger.debug("last packet: {}", lastPacket);
         logger.debug("poll packet: {}", pollPacket);
         logger.debug("body length: {}", bodyLength);
         staging.writePrimaryStaging(packetBuffer, bodyLength);
         
         if(pollPacket == false)
         {
            if(lastPacket == true)
            {
               connectionKey.getInformer().received(connectionKey, staging.getPrimaryStage(), staging.getPrimaryStageSize());
               staging.resetPrimaryStage();
            }
         }
         else
            staging.resetPrimaryStage();
      }
   }
   private void writePacket(TCPClientConnectionKey connectionKey)
   {
      Message message=sendRequest.getCurrent(connectionKey);
      MessageStaging staging=connectionKey.getMessageStaging();
      boolean lastPacket=false;
      
      try
      {
         if(staging.hasTempWriteBytes() == true)
         {
            staging.readTempWriteBytes(packetBuffer);
            lastPacket=staging.isWriteLastPacket(); //this was calculated on the previous attempt to write to the channel
            staging.resetTempWriteBytes();
            staging.setWriteLastPacket(false);
         }
         else
         {
            int bodyLength=message.writeToBuffer(packetBody);
            
            logger.debug("bytes left: {}", message.bytesLeft());
            logger.debug("body length: {}", bodyLength);
            packetBody.rewind(); //set packetBody for appending to the packetBuffer
            lastPacket=message.bytesLeft() == 0;

            //build the packet that is to be sent over the wire
            packetBuffer.put(NIOUtils.createBooleanByte(lastPacket)); //last packet flag
            packetBuffer.put(NIOUtils.createBooleanByte(message instanceof PollMessage)); //if it was a poll packet
            packetBuffer.putInt(bodyLength); //set the length of the body
            packetBuffer.put(packetBody); //add the body of the packet
            
            while(packetBuffer.position() < packetBuffer.limit()) //fill the buffer
               packetBuffer.put((byte)0);
         }
         
         packetBuffer.flip(); //make the buffer ready to be read by the socket
         int packetLength=packetBuffer.limit()-packetBuffer.position();
         
         SocketChannel channel=connectionKey.getClient();
   
         int bytesWritten=NIOUtils.writeToChannel(channel, packetBuffer);
         logger.debug("bytes written to channel: {}", bytesWritten);
         
         if(bytesWritten == -1)
         {
            logger.debug("Connection closed during write");
            socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.CLOSE_CHANNEL, connectionKey));
            sendRequest.clear(connectionKey);
            connectionKey.close();
            connectionKey.getInformer().closed(connectionKey);
         }
         else if(bytesWritten < packetLength)
         {
            staging.writeTempWriteBytes(packetBuffer);
            staging.setWriteLastPacket(lastPacket);
         }
         else if(lastPacket == true || message instanceof PollMessage)
         {
            sendRequest.removeCurrent(connectionKey);
            message.finished();
            
            if(sendRequest.contains(connectionKey) == false)
               socketChangeRequestQueue.add(new ChangeRequest(ChangeRequestType.SEND_MESSAGE_FINISHED, connectionKey));
         }
      }
      catch(Exception e)
      {
         message.erroredWhileSending(e);
         sendRequest.removeCurrent(connectionKey);
      }
   }
   private void processChanges()
   {
      ChangeRequest request;
      
      while(socketChangeRequestQueue.isEmpty() == false)
      {
         try
         {
            request=socketChangeRequestQueue.poll();
            if(request != null)
            {
               switch(request.getType())
               {
                  case CLOSE_CHANNEL:
                     logger.debug("Closing channel");
                     SelectionKey key=request.getKey().getSelectableChannel().keyFor(selector);
                     
                     if(key != null)
                     {
                        key.attach(null);
                        key.cancel();
                        NIOUtils.closeChannel(request.getKey().getSelectableChannel());
                     }
                     
                     break;
                  case OPEN_CHANNEL:
                     logger.debug("opening channel");
                     TCPClientConnectionKey client=(TCPClientConnectionKey)request.getKey();
                     SocketChannel clientChannel=NIOUtils.openClientSocket(selector, client.getEndPointAddress());
                     
                     if(clientChannel != null)
                     {
                        client.setSocket(clientChannel);
                        client.getSelectableChannel().keyFor(selector).attach(client);
                        client.getInformer().ableToConnect(client.getEndPointAddress());
                     }
                     else
                     {
                        client.getInformer().unableToConnect(client.getEndPointAddress());
                        client.close();
                     }
                     
                     break;
                  case BIND_SERVER:
                     logger.debug("Binding TCP server");
                     TCPServerConnectionKey server=(TCPServerConnectionKey)request.getKey();
                     ServerSocketChannel serverChannel=NIOUtils.openServerSocket(selector, server.getBindAddress());
                     
                     if(serverChannel != null)
                     {
                        server.setSocket(serverChannel);
                        server.getSelectableChannel().keyFor(selector).attach(server);
                        server.getInformer().serverBound(server.getBindAddress());
                     }
                     else
                        server.getInformer().unableToBindServer(server.getBindAddress());
                        
                     break;
                  case SEND_MESSAGE:
                     logger.debug("SEND_MESSAGE configure for OP_READ and OP_WRITE");
                     request.getKey().getSelectableChannel().keyFor(selector).interestOps(SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                     break;
                  case SEND_MESSAGE_FINISHED:
                     logger.debug("SEND_MESSAGE_FINISHED configure for OP_READ");
                     request.getKey().getSelectableChannel().keyFor(selector).interestOps(SelectionKey.OP_READ);
                     break;
               }
            }
         }
         catch(NetIOException e)
         {
            logger.error("NetIOException", e);
         }
      }
   }
   @Override
   public void shutdown()
   {
      this.run=false;
      this.selector.wakeup();
      try{reactorThread.join();}catch(InterruptedException e){}
   }
}
