package demo.server.service;

import com.github.jrpc.server.annotation.RPCMapping;
import com.github.jrpc.server.annotation.RPCService;

@RPCService
public class HelloService {
    
	@RPCMapping
	public String sayHello(String name, int age) {
		return String.format("Hello, %s, You are %d years old", name, age);
	}
}
