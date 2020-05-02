package com.github.jrpc.net.nio.channels;

public interface CompletionHandler <T, V>{
    public void complete(T t, V v);
    
    public void failed(Throwable exc, T t);
    
}
