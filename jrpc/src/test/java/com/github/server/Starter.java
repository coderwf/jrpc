package com.github.server;

import com.github.jrpc.server.annotation.RPCServerStarter;
import com.github.jrpc.server.application.RPCApplication;

@RPCServerStarter
public class Starter {
    public static void main(String[] args) {
		RPCApplication.run(Starter.class, args);
	}
}
