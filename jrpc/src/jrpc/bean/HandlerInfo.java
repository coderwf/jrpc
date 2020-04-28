package jrpc.bean;

import java.lang.reflect.Method;

public class HandlerInfo {
    private String obj;
    private Method method;
    
    public HandlerInfo() {};
    
    public HandlerInfo(String obj, Method method) {
    	this.obj = obj;
    	this.method = method;
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
	}
	
	
}
