package jrpc.test.server;

import jrpc.server.annotation.RPCMapping;
import jrpc.server.annotation.RPCService;

@RPCService
public class HelloService {
    
	@RPCMapping
	public void sayHello() {
		System.out.println("hello!");
	}
	
}
