package edu.wm.cs420.web;

public class HTTPBasicAuth {

	private String username;
	private String password;
	
	private static HTTPBasicAuth instance;
	
	private HTTPBasicAuth() {}
	
	public static HTTPBasicAuth getInstance() {
		if (instance == null) {
			instance = new HTTPBasicAuth();
		}
		return instance;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getBasicAuthString() {
		return username+":"+password;
	}
	
}