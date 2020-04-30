package com.github.jrpc.server.application;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.jrpc.core.props.Props;
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
	
	private static RPCServerStarter checkIsRPCStarterClass(Class<?> clazz) {
		
		//获取RPCServerStarter注解
		RPCServerStarter starter = clazz.getAnnotation(RPCServerStarter.class);
		
    	if(starter == null)
    		throw new RuntimeException(String.format("%s is not a RPC Starter Class", clazz.getName()));
    	
    	if(! "".equals(starter.name()))
    		serverName = starter.name();
    	else 
    		serverName = clazz.getName();
    	return starter;
	}
	
	private static void loadAllService(String packageName) {
		//加载所有类
		List<Class<?>> classList = ClassUtil.getClasses(packageName);
		
		//遍历所有类并根据注解类型分类
		for(Class<?> clazz: classList)
		    dispatcher.addMapping(clazz, clazz.getAnnotation(RPCService.class));
		//
	}
	
	private static void loadProperties(Class<?> clazz, RPCServerStarter starter) {
		Props.put("rpc.server.port", starter.port());
		Props.put("rpc.server.encoding",starter.encoding());
		Props.put("rpc.server.buffersize", "1024");
		
		String classpath = clazz.getResource("/").getPath().toString();
		String propertiesPath = new File(classpath, "rpc-server.properties").toString();
		try {
			Props.loadProperties(propertiesPath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    public static void run(Class<?> clazz, String []args) {
    	//检查该类是否为启动类
    	RPCServerStarter starter = checkIsRPCStarterClass(clazz);
    	
    	//加载properties属性
    	loadProperties(clazz, starter);
    	
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

