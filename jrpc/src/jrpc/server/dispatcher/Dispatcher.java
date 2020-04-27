package jrpc.server.dispatcher;

import java.lang.reflect.Method;
import java.util.HashMap;

import jrpc.bean.BeanFactory;
import jrpc.bean.Handler;
import jrpc.server.annotation.RPCMapping;
import jrpc.server.annotation.RPCService;

public class Dispatcher {
    private static Dispatcher dispatcher = new Dispatcher();
    
    private HashMap<String, Handler> mappings = new HashMap<String, Handler>(200);
    
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
    		
    		mappings.put(String.format("%s.%s", serviceName, mappingName), new Handler(clazz.getSimpleName(), method));
    		
    	}//for
    	
    	try {
			BeanFactory.registerBean(clazz.getSimpleName(), clazz, BeanFactory.Singleton);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
    	
    }//addMapping
    
    public Handler getHandler(String serviceName) {
    	return mappings.get(serviceName);
    }
    
}
