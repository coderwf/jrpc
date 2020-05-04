package com.github.jrpc.net.nio.channels;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


public class NioAsyncDispatcherServer {
	
	private Map<SocketChannel, NioConnection> connsMap = new LinkedHashMap<SocketChannel, NioConnection>(1024);
	
	//default 8080
	private int port = 8080;
	
	private AcceptCompleteHandler acceptCompletionHandler;
		
	private ServerSocketChannel serverSocketChannel ;
	
	private Selector selector ;
	
	public NioAsyncDispatcherServer(int port, AcceptCompleteHandler acceptCompletionHandler){
		this.port = port;
		this.acceptCompletionHandler = acceptCompletionHandler;
	}
	
	private void initServer() throws IOException {
		this.serverSocketChannel  = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(this.port);
		this.serverSocketChannel.bind(address);
		//设置地址可以重用
    	// this.serverSocketChannel.socket().setReuseAddress(true);
    	this.serverSocketChannel.configureBlocking(false);
    	
        this.selector = Selector.open();
        
        // 为服务器注册接收连接请求
        this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        
	}
	
	private void dispatchAccept(SelectionKey key) {
		
		for(int i=0;i<24;i++) {
			SocketChannel client;
			
			try {
				client =  ((ServerSocketChannel)(key.channel())).accept();
				if(client == null)
					return ;
				this.acceptCompletionHandler.accept(client);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void dispatchReadAndWrite(SelectionKey key) {
	    SocketChannel socket = (SocketChannel) key.channel();
	    NioConnection conn = this.acceptCompletionHandler.getConn(socket);
	    if(key.isReadable())
	    	conn.handlerRead(key);
	    else if (key.isWritable())
            conn.handlerWrite(key);	    	
    }
	
	
	private void dispatch() throws IOException   {
		
		 if (selector.select(100) <= 0) 
             return ;// 没有任何事件
         
         Iterator<SelectionKey> iterable = selector.selectedKeys().iterator();
         
         while (iterable.hasNext()) {
             SelectionKey key = iterable.next();
             iterable.remove();
             
             //
             
             if(! key.isValid())
            	 continue;
             
             if (key.isAcceptable()) {
                 // 处理客户端连接
                 dispatchAccept(key);
             }else {
            	 dispatchReadAndWrite(key);
             }
             
         }//while
	}
	
	private void loop() {
		while(true) {
			
			try {
				dispatch();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public void start() {
		try {
			initServer();
		}catch(IOException e) {
			e.printStackTrace();
			return ;
		}//
		
		loop();

	}
}
