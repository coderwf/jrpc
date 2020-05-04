package com.github.jrpc.net.nio.channels;

import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AcceptCompleteHandler{
	
	private Map<SocketChannel, NioConnection> connsMap = new LinkedHashMap<SocketChannel, NioConnection>(1024);

    public abstract void acceptComplete(SocketChannel socket) ;
    
    public void accept(SocketChannel client, ) {
    	//注册连接
    	this.registerConn(client, new NioConnection());
    	
    	//调用
    	acceptComplete(client);
    	
    	//调用handler
    	
    }
    
    public final NioConnection getConn(SocketChannel socket) {
        NioConnection conn = connsMap.get(socket);
        if(conn == null)
        	throw new RuntimeException("NioConn for socket is null");
        return conn;
    }
    
    public final void registerConn(SocketChannel socket, NioConnection conn) {
    	connsMap.put(socket, conn);
    }
    
    public final void unRegisterConn(SocketChannel socket) {
    	connsMap.remove(socket);
    }
}
