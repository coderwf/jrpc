package jrpc.bean;

public class RPCResponse {
    private Integer status;
    
    private String message;
    
    private Object data;
    
    public RPCResponse() {}
    
    public RPCResponse(Integer status, String message, Object data) {
    	this.status = status;
    	this.message = message;
    	this.data = data;
    }
    
    public RPCResponse(Integer status, String message) {
    	this(status, message, null);
    }
    
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
    
}
