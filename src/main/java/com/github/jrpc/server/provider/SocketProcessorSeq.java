package com.github.jrpc.server.provider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.github.jrpc.core.bean.BeanFactory;
import com.github.jrpc.core.utils.JsonUtil;
import com.github.jrpc.server.beans.RPCRequest;
import com.github.jrpc.server.beans.RPCResponse;
import com.github.jrpc.server.dispatcher.Dispatcher;
import com.github.jrpc.server.dispatcher.Handler;

public class SocketProcessorSeq implements SocketProcessor{
    
	private byte[] bytes = null;
	private ByteBuffer buffer;
	private String encoding;
	
	
	public SocketProcessorSeq(ByteBuffer buffer, byte[] bytes, String encoding) {
		this.buffer = buffer;
		this.bytes = bytes;
		this.encoding = encoding;
	}
	
	
	public void close(SocketChannel socket) {
	    try {
	    	socket.close();
	    }catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void handlerSocket(SocketChannel socket){
		
		if(! socket.isOpen())
			return ;
		
        //读取数据
        RPCRequest request = null;

        try {
            request = readRequest(socket);
        }catch (Exception e) {
		    close(socket);
		    return ;
		}
         
        RPCResponse response;
        
        try {
        	response = invoke(request);
        }catch (Exception e) {
        	response = new RPCResponse(400, e.getMessage());
		}
        
        //将response写入socket
        try {
        	writeResponse(socket, response);
        }catch (Exception e) {
			close(socket);
			return ;
		}
        
	}//handlerSocket
	
    
    private void writeResponse(SocketChannel socket, RPCResponse response) throws IOException {
    	String responseJson = JsonUtil.objectToJson(response);
    	
    	//todo null
    	ByteBuffer buffer = ByteBuffer.wrap(responseJson.getBytes(this.encoding));
    	
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
    
    private RPCRequest readRequest(SocketChannel socket) throws IOException {
    	// ByteBuffer buffer = ByteBuffer.allocate(this.bufferSize);// must > 4
    	
    	int msgLen = readLength(socket, this.buffer);
    	byte[] msgBytes = readMsg(socket, this.buffer, msgLen);
    	
    	//解码
    	String msg = new String(msgBytes, 0, msgLen, this.encoding);
    	    	
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
    
    private byte[] readMsg(SocketChannel socket, ByteBuffer buffer, int readLen) throws IOException {
    	int n = 0, readBytesN = 0;
    	byte[] readBytes = null;
    	
//    	//重用同一个bytes
    	if(readLen > this.bytes.length)
    	    readBytes = new byte[readLen];
    	else
    		readBytes = this.bytes;
    	
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
    	Handler handler = Dispatcher.getSingletonDispatcher().getHandler(request.getService());
    	
    	if(handler == null)
    	    return new RPCResponse(400, String.format("Can not find handler for %s", request.getService()));
    	
    	Object service = null;
    	
		try {
			service = BeanFactory.getBean(handler.getObj());
			if(service == null)
				return new RPCResponse(400, String.format("Can not find service for %s", request.getService()));
			
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		    return new RPCResponse(400, e.getMessage());
		}
    	
		Object result = null;
		
    		
		//result = handlerInfo.getMethod().invoke(handler, request.getParameters());
		try {
			result = handler.invoke(service, request.getData());
		} catch (IllegalAccessException | InvocationTargetException | RuntimeException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new RPCResponse(400, e.getMessage());
		}
    	
    	return new RPCResponse(200, "Success", result);
    	
    }//invoke

}

