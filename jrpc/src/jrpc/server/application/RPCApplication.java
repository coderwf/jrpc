package jrpc.server.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jrpc.bean.BeanFactory;
import jrpc.bean.Handler;
import jrpc.server.annotation.RPCServerStarter;
import jrpc.server.annotation.RPCService;
import jrpc.server.dispatcher.Dispatcher;
import jrpc.utils.ClassUtil;

/*
 * RPC服务端的启动入口
 * 
 * */

public class RPCApplication {
	private static String serverName = null;
	
	private static Dispatcher dispatcher = Dispatcher.getSingletonDispatcher();
	
	private static void checkIsRPCStarterClass(Class<?> clazz) {
		
		//获取RPCServerStarter注解
		RPCServerStarter starter = clazz.getAnnotation(RPCServerStarter.class);
		
    	if(starter == null)
    		throw new RuntimeException(String.format("%s is not a RPC Starter Class", clazz.getName()));
    	
    	if(! "".equals(starter.name()))
    		serverName = starter.name();
    	else 
    		serverName = clazz.getName();
    	
	}
	
	private static void loadAllService(String packageName) {
		//加载所有类
		List<Class<?>> classList = ClassUtil.getClasses(packageName);
		
		//遍历所有类并根据注解类型分类
		for(Class<?> clazz: classList)
		    dispatcher.addMapping(clazz, clazz.getAnnotation(RPCService.class));
		//
	}
	
    public static void run(Class<?> clazz, String []args) {
    	//检查该类是否为启动类
    	checkIsRPCStarterClass(clazz);
    	
    	//加载所有service类
    	loadAllService("jrpc");
    	
    	//监听服务
    	
    }
}
