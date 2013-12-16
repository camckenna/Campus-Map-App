package edu.wm.cs420.web;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class HTTPRequestTask extends AsyncTask<String, Void, String>{

	public interface NetworkListener {
		void networkRequestCompleted(HTTPRequestResult r);
	}
	 
	private ArrayList<NameValuePair> data;
	private HTTPBasicAuth auth;
	private HTTPRequestType type;
	private String url;
 
	private NetworkListener listener;
	
	
	public HTTPRequestTask(ArrayList<NameValuePair> data, HTTPBasicAuth auth,
			HTTPRequestType type)  {
		this.data = data;
		this.auth = auth;
		this.type = type;
	}
	
	@Override
    protected String doInBackground(String... params) {
		
		this.url = params[0];
		 String result = null;

        
        try {
            String authEncoded = Base64.encodeToString(auth.getBasicAuthString().getBytes(), Base64.DEFAULT);

            HttpURLConnection connection = null;
           
            URL url = null;
            
	        //Initialize as get
	        if (type == HTTPRequestType.GET) {
	    		//Set up request parameters
	    		try {
	    			url = new URL (params[0]);
	    		} catch (MalformedURLException e) {
	    			Log.e("HTTPRequestTask",e.getMessage());
	    		}
	        	connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestProperty  ("Authorization", "Basic " + authEncoded);
	        	Log.i("HTTPRequestTask","Doing get to "+url);
	        	connection.setRequestMethod("GET");
	        } 
	        //Initialize as post
	        else if (type == HTTPRequestType.POST) {
	    		//Set up request parameters
	    		try {
	    			url = new URL (params[0]);
	    		} catch (MalformedURLException e) {
	    			Log.e("HTTPRequestTask",e.getMessage());
	    		}
	        	connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestProperty  ("Authorization", "Basic " + authEncoded);
	        	Log.i("HTTPRequestTask","Doing post to "+url);
	            connection.setRequestMethod("POST");
	            connection.setDoOutput(true);
	            //Set up post data
	            BufferedWriter out = new BufferedWriter(
	                    new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
	            //Send the data
	            out.write(makeQuery(data));
	            out.flush();
	            out.close();
	        }
	        else if (type == HTTPRequestType.GETWITHPARAMS) {
	        	String query = makeQuery(data);
	    		//Set up request parameters
	    		try {
	    			url = new URL (params[0]+"?"+query);
	    		} catch (MalformedURLException e) {
	    			Log.e("HTTPRequestTask",e.getMessage());
	    		}
	        	connection = (HttpURLConnection) url.openConnection();
	            connection.setRequestProperty  ("Authorization", "Basic " + authEncoded);
	            Log.i("HTTPRequestTask","Doing get with params to "+url);
	            connection.setRequestMethod("GET");
	        }
	        
	        
	        //Get the response
        	int HttpResult = connection.getResponseCode();  
            if (HttpResult == HttpURLConnection.HTTP_OK){  
            	BufferedReader br = new BufferedReader(new InputStreamReader(  
                connection.getInputStream(),"utf-8"));
            	StringBuilder builder = new StringBuilder();
            	String line = "";  
            	while ((line = br.readLine()) != null) {
            		builder.append(line);
            	}  
            	result = builder.toString();
            	br.close();  

            	Log.i("HTTPRequestTask",result); 

            } else {  
            	Log.e("HTTPRequestTask",connection.getResponseMessage());  
            }  
        }
        catch (Exception e) {
        	StringWriter sw = new StringWriter();
        	PrintWriter pw = new PrintWriter(sw);
        	e.printStackTrace(pw);
        	Log.e("HTTPRequestTask",sw.toString());
        }
        return result;
    }
	
	private String makeQuery(ArrayList<NameValuePair> data) throws UnsupportedEncodingException
	{
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : data)
	    {
	        if (first)
	            first = false;
	        else
	            result.append("&");

	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        HTTPRequestResult r = new HTTPRequestResult(result,url);
        listener.networkRequestCompleted(r);
    }

    public NetworkListener getListener() {
    	return listener;
    }
    public void setListener(NetworkListener listener) {
		this.listener = listener;
	}
}