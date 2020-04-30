package com.github.jrpc.core.props;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class Props {
    private static final Properties properties = new Properties();
    
    public static String get(String key) {
    	return properties.getProperty(key);
    }
    
    
    public static void put(String key, String value) {
    	properties.put(key, value);
    }
        
    public static void loadProperties(String file) throws IOException {
    	InputStream inputStream=null;
        try {
        	inputStream = new BufferedInputStream(new FileInputStream(new File(file)));
            properties.load(inputStream);
        } finally{
            if(null != inputStream)
            inputStream.close();     
        }//finally
    }//   
}
