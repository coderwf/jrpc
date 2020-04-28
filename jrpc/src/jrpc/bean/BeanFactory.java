package jrpc.bean;

import java.util.HashMap;
import java.util.Map;

class BeanInfo{
	String scope;
	
	Object obj;
	
	Class<?> clazz;
	
	public BeanInfo(String scope, Class<?> clazz, Object obj) {
		this.scope = scope;
		this.clazz = clazz;
		this.obj = obj;
	}
	
	public BeanInfo(String scope, Class<?> clazz) {
		this(scope, clazz, null);
	}
	
}
public class BeanFactory {
    private static Map<String, BeanInfo> beansMap = new HashMap<String, BeanInfo>(100);
    
    public final static String Singleton = "singleton";
    public final static String Prototype = "prototype";
    
    public static void registerBean(String beanName, Class<?> beanClass, String scope, boolean lazyNew) throws InstantiationException, IllegalAccessException {
    	if(beansMap.containsKey(beanName))
    	    throw new RuntimeException(String.format("Duplicated Bean %s", beanName));
    	
    	BeanInfo beanInfo = new BeanInfo(scope, beanClass);
    	beansMap.put(beanName, beanInfo);
    	
    	if(scope.equals(Prototype))
    		return ;
    	
    	if(scope.equals(Singleton)) {
    		if(!lazyNew)
    			beanInfo.obj = beanClass.newInstance();
    		return ;
    	}
    	
    	throw new RuntimeException(String.format("Invalid Bean Scope %s for %s", scope, beanName));
    }
    
    public static void registerBean(String beanName, Class<?> beanClass, String scope) throws InstantiationException, IllegalAccessException {
    	registerBean(beanName, beanClass, scope, false);
    }

    public static Object getBean(String beanName) throws InstantiationException, IllegalAccessException {
        BeanInfo beanInfo = beansMap.get(beanName);
        if(beanInfo == null)
            return null;
        
        if(beanInfo.scope.equals(Prototype))
    		return beanInfo.clazz.newInstance();
        
        if(beanInfo.scope.equals(Singleton)) {
        	if(beanInfo.obj == null)
        		beanInfo.obj = beanInfo.clazz.newInstance();
        	return beanInfo.obj;
        }
        
        return null;
    }
    
    
    public static boolean hasBean(String beanName) {
    	return beansMap.containsKey(beanName);
    }
    
}
