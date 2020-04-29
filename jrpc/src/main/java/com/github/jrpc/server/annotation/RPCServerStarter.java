package com.github.jrpc.server.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 标记该类为RPC服务的启动类 
 */


@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RPCServerStarter {
    String name() default ""; //服务名
    
    String port() default "7000"; //端口
    
    String encoding() default "utf-8";//数据传输编码方式
    
}
