package com.github.jrpc.server.beans;

import java.util.Map;

public class RPCRequest {
	
    private String service;
    
    private Map<String, Object> data;

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}


    
}
