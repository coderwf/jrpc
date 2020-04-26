package jrpc.service;

public class Main {
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		Class<? extends Class> clazz = HelloService.class.getClass();
		Class helloService = clazz.newInstance();
		System.out.println(helloService.getName());
	}
}
