package com.github.jrpc.server.dispatcher;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

public class Handler {
    private String obj;
    private Method method;
    
    private String[] params;
    
    public Handler() {};
    
    private void parseMethodParams(Method method) {
    	Parameter []parameters = method.getParameters();
    	params = new String[parameters.length];
    	for(int i=0;i<parameters.length;i++)
    		params[i] = parameters[i].getName();
    }
    
    public Handler(String obj, Method method) {
    	this.obj = obj;
    	this.method = method;
    	this.parseMethodParams(this.method);
    }
    
	public String getObj() {
		return obj;
	}
	
	public void setObj(String obj) {
		this.obj = obj;
	}
	
	public Method getMethod() {
		return method;
	}
	
	public void setMethod(Method method) {
		this.method = method;
		this.parseMethodParams(this.method);
	}
	
	public Object invoke(Object obj, Map<String, Object> argsMap) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object [] args = new Object[this.params.length];
		
		for(int i = 0;i<params.length;i++) {
			args[i] = argsMap.get(this.params[i]);
			if(args[i] == null)
				throw new RuntimeException(String.format("Param %s is not present", params[i]));
		}//
		
		return this.method.invoke(obj, args);
	}
	
}
