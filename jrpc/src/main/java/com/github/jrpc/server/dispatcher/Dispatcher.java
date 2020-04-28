package com.github.jrpc.server.dispatcher;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.github.jrpc.core.bean.BeanFactory;
import com.github.jrpc.server.annotation.RPCMapping;
import com.github.jrpc.server.annotation.RPCService;
import com.github.jrpc.server.beans.HandlerInfo;



public class Dispatcher {
    private static Dispatcher dispatcher = new Dispatcher();
    
    private HashMap<String, HandlerInfo> mappings = new HashMap<String, HandlerInfo>(200);
    
    private Dispatcher() {};
    
    public static Dispatcher getSingletonDispatcher() {
    	return dispatcher;
    }
    
    public void addMapping(Class<?> clazz, RPCService service) {

    	if(service == null)
    		return ;
    	
    	String serviceName = null;
    	
    	if("".equals(service.name()))
    		serviceName = clazz.getSimpleName();
    	else
    		serviceName = service.name();

    	Method methods[] = clazz.getMethods();
    	
    	for(Method method: methods) {
    		    		
    		RPCMapping rpcMapping = method.getAnnotation(RPCMapping.class);
    		if(rpcMapping == null)
    			continue;
    		
    		String mappingName = null;
    		
    		if("".equals(rpcMapping.mapping()))
    			mappingName = method.getName();
    		else
    			mappingName = rpcMapping.mapping();
    		
    		mappings.put(String.format("%s.%s", serviceName, mappingName), new HandlerInfo(clazz.getSimpleName(), method));
    		
    	}//for
    	
    	try {
			BeanFactory.registerBean(clazz.getSimpleName(), clazz, BeanFactory.Singleton);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
    	
    }//addMapping
    
    public HandlerInfo getHandler(String serviceName) {
    	return mappings.get(serviceName);
    }
    
}