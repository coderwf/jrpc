package com.github.jrpc.server.application;

import java.util.List;

import com.github.jrpc.core.utils.ClassUtil;
import com.github.jrpc.server.annotation.RPCServerStarter;
import com.github.jrpc.server.annotation.RPCService;
import com.github.jrpc.server.dispatcher.Dispatcher;
import com.github.jrpc.server.provider.RPCProvider;


/*
 * RPC服务端的启动入口
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
    	
    	//加载启动类所在包以及其子包内所有service
    	loadAllService(clazz.getPackage().getName());
    	
    	//监听并对外提供服务
    	try {
			RPCProvider.provide();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//
    }
}

