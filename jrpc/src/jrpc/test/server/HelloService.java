package jrpc.test.server;

import jrpc.server.annotation.RPCMapping;
import jrpc.server.annotation.RPCService;

@RPCService(name = "Hello")
public class HelloService {
    
	@RPCMapping(mapping = "com.SayHello")
	public void sayHello() {
		System.out.println("hello!");
	}
}
