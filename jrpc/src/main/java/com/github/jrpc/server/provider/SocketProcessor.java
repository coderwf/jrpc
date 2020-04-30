package com.github.jrpc.server.provider;

import java.nio.channels.SocketChannel;

public interface SocketProcessor {
    public void handlerSocket(SocketChannel socket);
}
