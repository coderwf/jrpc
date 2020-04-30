package com.github.jrpc.server.provider;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SocketProcessorExecutor implements SocketProcessor{
    
	private class Worker implements Runnable{
		private BlockingQueue<SocketChannel> socketQueue = null;
		
		private SocketProcessorSeq processor = null;
		
		Worker(int queueSize, ByteBuffer buffer, byte[]bytes, String encoding) {
			this.processor = new SocketProcessorSeq(buffer, bytes, encoding);
			
		    this.socketQueue = new LinkedBlockingQueue<SocketChannel>(queueSize);
		}
        
		public void submit(SocketChannel socket) {
			//放入第hash个队列中
	    	try {
	    		if(! this.socketQueue.offer(socket, 1, TimeUnit.SECONDS))
		    		throw new RuntimeException("Connection Refused");
			} catch (Exception e) {
				// e.printStackTrace();
				processor.close(socket);
			}
		}
		
		@Override
		public void run() {
			while(true) {
				try {
					SocketChannel socket = this.socketQueue.take();
					this.processor.handlerSocket(socket);
					
				}catch (InterruptedException e) {
				    e.printStackTrace();
				}
				
			}//while
		}//run
	}//worker
	
    private ExecutorService executor = null;
	private Worker workers[] ;
	private int nWorker;
	
    public SocketProcessorExecutor(int nWorker, int queueSize, int bufferSize, int bytesSize, String encoding) {
    	this.nWorker = nWorker;
		init(nWorker, queueSize, bufferSize, bytesSize, encoding);
	}
    
	private void init(int nWorker, int queueSize, int bufferSize, int bytesSize, String encoding) {
		executor = Executors.newFixedThreadPool(nWorker);

		workers = new Worker[nWorker];
		for(int i=0;i<nWorker;i++) {
			workers[i] = new Worker(queueSize, ByteBuffer.allocate(bufferSize), new byte[bytesSize], encoding);
			executor.execute(workers[i]);
		}
	}
	
	@Override
	public void handlerSocket(SocketChannel socket) {
		//hash
		int hash = socket.hashCode() % this.nWorker;
		workers[hash].submit(socket);
	}
	
}
