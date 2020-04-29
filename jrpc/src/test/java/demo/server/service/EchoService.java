package demo.server.service;

import com.github.jrpc.server.annotation.RPCMapping;
import com.github.jrpc.server.annotation.RPCService;

class Result{
    private int status;
    
    private String message;
    
    private Object data;
    
    public Result(int status, String message, Object data) {
    	this.status = status;
    	this.message = message;
    	this.data = data;
    }

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
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


class Student{
    private int age;
    private String name;
    
    public Student(int age, String name) {
		this.age = age;
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}

@RPCService(name = "Echo")
public class EchoService {
    @RPCMapping(mapping = "echo")
    public Result echoAnything(int age, String name) {
    	return new Result(200, "OK", new Student(age, name));
    }
    
}
