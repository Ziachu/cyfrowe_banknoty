package gui;

import java.util.EventObject;

public class LoginEvent extends EventObject {

	private String role;
	private String host;
	private String port;
	
	public LoginEvent(Object source) {
		super(source);
	}

	public LoginEvent(Object source, String role, String host, String port) {
		super(source);
		
		this.role = role;
		this.host = host;
		this.port = port;
	}
	
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	
}
