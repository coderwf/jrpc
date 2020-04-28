package jrpc.test;

import java.nio.ByteBuffer;

public class TestByteBuffer {
    public static void main(String[] args) {
    	
    	byte[] a = "123".getBytes();
    	
		ByteBuffer buffer = ByteBuffer.wrap(a);
		System.out.println(buffer.position());
		System.out.println(buffer.limit());
	}//main
}
