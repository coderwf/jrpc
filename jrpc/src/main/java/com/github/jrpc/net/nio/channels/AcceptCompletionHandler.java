package com.github.jrpc.net.nio.channels;

import java.nio.channels.SocketChannel;

public abstract class AcceptCompletionHandler{
	
    public abstract void complete(SocketChannel socket) ;
    
    public void accept() {
    	
    }
    
}
