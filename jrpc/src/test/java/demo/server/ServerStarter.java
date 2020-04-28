package demo.server;

import com.github.jrpc.server.annotation.RPCServerStarter;
import com.github.jrpc.server.application.RPCApplication;

@RPCServerStarter
public class ServerStarter {
    public static void main(String[] args) {
		RPCApplication.run(ServerStarter.class, args);
	}
}