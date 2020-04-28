package jrpc.bean;

public class RPCRequest {
	
    private String service;
    
    private Object parameters[];
    
	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public Object[] getParameters() {
		return parameters;
	}
	
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
    
}
