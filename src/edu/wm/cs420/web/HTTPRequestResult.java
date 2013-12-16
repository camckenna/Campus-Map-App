package edu.wm.cs420.web;

public class HTTPRequestResult {

	private String result;
	private String url; //The url of the request
	
	public HTTPRequestResult(String result, String url) {
		super();
		this.result = result;
		this.url = url;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}	
	
}