package com.github.jrpc.server.provider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.github.jrpc.core.bean.BeanFactory;
import com.github.jrpc.core.utils.JsonUtil;
import com.github.jrpc.server.beans.HandlerInfo;
import com.github.jrpc.server.beans.RPCRequest;
import com.github.jrpc.server.beans.RPCResponse;
import com.github.jrpc.server.dispatcher.Dispatcher;



public class RPCProvider {
	
	//java NIO
	
    public static void provide() throws IOException {
    	ServerSocketChannel socketChannel = ServerSocketChannel.open();
    	
        // 设置socket为非阻塞状态
        socketChannel.configureBlocking(false);
        // 绑定到9999端口
        
        socketChannel.bind(new InetSocketAddress(9999));
        Selector selector = Selector.open();
        
        // 为服务器注册接收事件
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            if (selector.select(500) <= 0) 
            	continue;
            
            Iterator<SelectionKey> iterable = selector.selectedKeys().iterator();
            
            while (iterable.hasNext()) {
            	
                SelectionKey key = iterable.next();
                if (key.isAcceptable()) {
                    // 如果accept被激活，说明有客户端连接
                    SocketChannel newSocketChannel = socketChannel.accept();
                    newSocketChannel.configureBlocking(false);
                    // 为客户端连接添注册读写事件
                    newSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                } else if (key.isReadable()) {
                    // 如果读到客户端的数据
                    SocketChannel clientSocket = (SocketChannel) key.channel();
                    //读取数据
                    
                    RPCRequest request = null;
                    
                    try {
                    	 request = readRequest(clientSocket);
                         RPCResponse response = invoke(request);
                         //将response写入socket
                         writeResponse(clientSocket, response);
                         
                    }catch (IOException e) {
						clientSocket.close();
					}                  
                    
                    
                } else if(key.isWritable()) {
                	//写数据
                }//
                
                iterable.remove();
            }//while
            
        }//while(true)
  
    }//provide
    
    private static void writeResponse(SocketChannel socket, RPCResponse response) throws IOException {
    	String responseJson = JsonUtil.objectToJson(response);
    	//todo null
    	ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes("utf-8"));
    	
    	int n,  writeLen = buffer.limit();
    	
    	while(writeLen > 0) {
    		n = socket.write(buffer);
    		
    		if(n == 0)
    			continue;
    		
    		if(n == -1)
    			throw new SocketException("Client closed the connection");
    		writeLen -= n;
    	}
    }
    
    private static RPCRequest readRequest(SocketChannel socket) throws IOException {
    	ByteBuffer buffer = ByteBuffer.allocate(1024);// must > 4
    	
    	int msgLen = readLength(socket, buffer);
    	byte[] msgBytes = readMsg(socket, buffer, msgLen);
    	
    	//解码
    	String msg = new String(msgBytes, "utf-8");
    	    	
    	RPCRequest request = JsonUtil.jsonToPojo(msg, RPCRequest.class);
    	
    	return request;
    }
    
    //读取消息的长度
    private static int readLength(SocketChannel socket, ByteBuffer buffer) throws IOException {
    	int readLen = 4, n=0;
    	
    	//read 返回0 -1或者接受到字节长度n
    	//0 表示在NIO情况下未读取到数据
    	//-1 表示对方已经断开连接
    	
    	//一直到读取4个字节长度
    	buffer.clear();
    	buffer.limit(4);
    	
    	while(readLen > 0) {
    		n = socket.read(buffer);
    		if(n == 0) {
    			continue;
    		}
    		
    		if(n == -1)
    			throw new SocketException("Client closed the connection");
    			
    		readLen -= n;
    		
    	}
    	
    	buffer.flip();
    	return buffer.getInt();
    }
    
    private static byte[] readMsg(SocketChannel socket, ByteBuffer buffer, int readLen) throws IOException {    	
    	int n = 0, readBytesN = 0;
    	byte[] readBytes = new byte[readLen];
    	
    	
    	while(readLen > 0) {
    		buffer.clear();
    		buffer.limit(Math.min(readLen, buffer.limit()));
    		n = socket.read(buffer);
    		
    		if(n == 0)
    			continue;
    		
    		if(n == -1)
    			throw new SocketException("Client closed the connection");
    		
    		buffer.flip();
    		
    		buffer.get(readBytes, readBytesN, n);
    		readBytesN += n;
    		readLen -= n;
    	}//
    	
    	return readBytes;
    }
    
    private static RPCResponse invoke(RPCRequest request) {
    	//
    	HandlerInfo handlerInfo = Dispatcher.getSingletonDispatcher().getHandler(request.getService());
    	if(handlerInfo == null)
    	    return new RPCResponse(400, String.format("Can not find handler for %s", request.getService()));
    	
    	Object handler = null;
    	
		try {
			handler = BeanFactory.getBean(handlerInfo.getObj());
			if(handler == null)
				return new RPCResponse(400, String.format("Can not find handler for %s", request.getService()));
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		    return new RPCResponse(400, e.getMessage());
		}
    	
		Object result = null;
		
    		
		//result = handlerInfo.getMethod().invoke(handler, request.getParameters());
		try {
			result = handlerInfo.invoke(handler, request.getData());
		} catch (IllegalAccessException | InvocationTargetException | RuntimeException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RPCResponse(400, e.getMessage());
		}
    	
    	return new RPCResponse(200, "Success", result);
    	
    }//invoke
    
}
