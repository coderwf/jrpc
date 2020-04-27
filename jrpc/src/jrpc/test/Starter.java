package jrpc.test;

import jrpc.server.annotation.RPCServerStarter;
import jrpc.server.application.RPCApplication;

@RPCServerStarter
public class Starter {
    public static void main(String[] args) {
		RPCApplication.run(Starter.class, args);
	}
}
