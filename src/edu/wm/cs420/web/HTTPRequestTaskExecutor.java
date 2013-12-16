package edu.wm.cs420.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.wm.cs420.web.HTTPRequestTask.NetworkListener;


import android.os.AsyncTask;

public class HTTPRequestTaskExecutor {
	
	public void doPost(String url, ArrayList<NameValuePair> data, HTTPBasicAuth auth, NetworkListener listener) {
		HTTPRequestTask task = new HTTPRequestTask(data, auth, HTTPRequestType.POST);
		task.setListener(listener);
		task.execute(url);
	}
	
	public void doGet(String url, HTTPBasicAuth auth, NetworkListener listener) {
		HTTPRequestTask task = new HTTPRequestTask(null, auth, HTTPRequestType.GET);
		task.setListener(listener);
		task.execute(url);
	}

	public void doGetWithParams(String url, ArrayList<NameValuePair> data,
			HTTPBasicAuth auth, NetworkListener listener) {
		HTTPRequestTask task = new HTTPRequestTask(data, auth, HTTPRequestType.GETWITHPARAMS);
		task.setListener(listener);
		task.execute(url);
		
	}
	
	
}