package com.github.jrpc.server.provider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.github.jrpc.core.bean.BeanFactory;
import com.github.jrpc.core.props.Props;
import com.github.jrpc.core.utils.JsonUtil;
import com.github.jrpc.server.beans.RPCRequest;
import com.github.jrpc.server.beans.RPCResponse;
import com.github.jrpc.server.dispatcher.Dispatcher;
import com.github.jrpc.server.dispatcher.Handler;



public class RPCProvider {
	
	private static String encoding = "utf-8";	
	
	//java NIO
	
    public static void provide() throws IOException {
    	encoding = Props.get("rpc.server.encoding");
        int bufferSize = Integer.parseInt(Props.get("rpc.server.buffersize"));
        
    	ServerSocketChannel socketChannel = ServerSocketChannel.open();
    	
        // 设置socket为非阻塞状态
        socketChannel.configureBlocking(false);
        // 绑定到9999端口
        
        socketChannel.bind(new InetSocketAddress(Integer.parseInt(Props.get("rpc.server.port"))));
        Selector selector = Selector.open();
        
        // 为服务器注册接收事件
        socketChannel.register(selector, SelectionKey.OP_ACCEPT);
        
        // SocketProcessor processor = new SocketProcessorSeq(ByteBuffer.allocateDirect(bufferSize), new byte[1024], encoding);
        
        SocketProcessor processor = new SocketProcessorExecutor(10, 1024, bufferSize, 1024, encoding);

        
        while (true) {
            if (selector.select(100) <= 0) 
            	continue;
            
            Iterator<SelectionKey> iterable = selector.selectedKeys().iterator();
            
            while (iterable.hasNext()) {
            	
                SelectionKey key = iterable.next();
                try {
                	 if (key.isAcceptable()) {
                         // 如果accept被激活，说明有客户端连接
                         SocketChannel newSocketChannel = socketChannel.accept();
                         newSocketChannel.configureBlocking(false);
                         // 为客户端连接添注册读写事件
                         newSocketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                         
                     } else if (key.isReadable()) {
                    	 
                         // 客户端可读
                         SocketChannel client = (SocketChannel) key.channel();
                         processor.handlerSocket(client);
     					
                         
                     } else if(key.isWritable()) {
                     	//写数据
                     }//
                }catch (CancelledKeyException e) {
					key.cancel();
				}//
                
                iterable.remove();
            }//while
            
        }//while(true)
  
    }//provide
}
