package jrpc.test;

import java.util.List;

import jrpc.utils.ClassUtil;

public class Main {
    public static void main(String[] args) {
		List<Class<?>> classList = ClassUtil.getClasses("jrpc");
		System.out.println(classList.size());
		
		for(Class<?> clazz: classList) {
		    System.out.println(clazz.getName());	
		}//for
	}
}
