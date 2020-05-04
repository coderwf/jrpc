package com.github.jrpc.net.nio.channels;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;


public class NioConnection {
	
	private Selector selector;
	
	private HandlerContext currentHandler;
	
	private SelectionKey key;
	
	private ByteBuffer buffer = ByteBuffer.allocate(1024);
	
	private SocketChannel socket;
	
	
	public NioConnection(Selector selector, HandlerContext invokeHandler) {
		this.currentHandler = invokeHandler;
		this.selector = selector;
	}
	
    public void setSelectionKey(SelectionKey key) {
    	this.key = key;
    }
    
    private void unRegisterOp(int ops) {
        key.interestOps(key.interestOps() & ~ops);
    }
    
    private void registerOp(int ops) {
    	key.interestOps(key.interestOps() | ops);
    }
    
    public void handlerEvent(SelectionKey key) {
    	
    }
    
    public void handlerRead(SelectionKey key) {
    	this.unRegisterOp(SelectionKey.OP_READ);
    	//read
    	socket.read(buffer);
    	this.currentHandler.justDoIt(this);
    	StringBuilder builder = new StringBuilder();
    	builder.toString();
    	builder.append(12);
    	ByteBuffer buffer = ByteBuffer.allocate(100);
    	buffer.slice()
    
    }
    
    public void handlerWrite(SelectionKey key) {
    	
    }
    
    public void readUtilN(int n,  HandlerContext invokeHandler) {
    	
    	//this.currentHandler = null;
    	
    }
    
}
