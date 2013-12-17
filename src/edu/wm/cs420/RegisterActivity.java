package edu.wm.cs420;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import edu.wm.cs420.web.HTTPBasicAuth;
import edu.wm.cs420.web.HTTPRequestResult;
import edu.wm.cs420.web.HTTPRequestTaskExecutor;
import edu.wm.cs420.web.HTTPRequestTask.NetworkListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements NetworkListener{
	private final String TAG = "Register_Activity";
	
	private EditText emailText;
	private EditText firstNameText;
	private EditText lastNameText;
	private EditText passwordText;
	private EditText verifyPasswordText;
	private Button registerButton;
	private TextView textView;
	
	private Intent intent;
	private String firstname;
	private String lastname;
	private String email;
	private String password;
	private String verifyPassword;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    intent = new Intent(this, TribeSignalActivity.class);
	    emailText = (EditText) findViewById(R.id.emailhandle);
		firstNameText = (EditText) findViewById(R.id.firstname);
		lastNameText = (EditText) findViewById(R.id.lastname);
		passwordText = (EditText) findViewById(R.id.password);
		verifyPasswordText = (EditText) findViewById(R.id.verifyPassword);
		registerButton = (Button) findViewById(R.id.btnRegister);
		textView = (TextView) findViewById(R.id.link_to_login);
		
		if(savedInstanceState == null){
			email = "";
			password = "";
		}
		else{
			email = savedInstanceState.getString("username");
			password = savedInstanceState.getString("password");
		}
		
		
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
		outState.putString("username", email);
		outState.putString("password", password);
	}


	public void goToLogin(View view){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	public void register(View view){
		// Reset errors.
		emailText.setError(null);
		firstNameText.setError(null);
		lastNameText.setError(null);
		passwordText.setError(null);
		verifyPasswordText.setError(null);

		// Store values at the time of the login attempt.
		email = emailText.getText().toString();
		firstname = firstNameText.getText().toString();
		lastname = lastNameText.getText().toString();
		password = passwordText.getText().toString();
		verifyPassword = verifyPasswordText.getText().toString();


		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			passwordText.setError(getString(R.string.error_field_required));
			focusView = passwordText;
			cancel = true;
		} 
		// Check for a valid password.
		if (TextUtils.isEmpty(verifyPassword)) {
					verifyPasswordText.setError(getString(R.string.error_field_required));
					focusView = verifyPasswordText;
					cancel = true;
		} 
		if(!password.equals(verifyPassword)){
			verifyPasswordText.setError(getString(R.string.error_passwords_not_match));
			focusView = verifyPasswordText;
			cancel = true;
		}
		// Check for a valid email address.
		if (TextUtils.isEmpty(firstname)) {
			firstNameText.setError(getString(R.string.error_field_required));
			focusView = firstNameText;
			cancel = true;
		}
		if (TextUtils.isEmpty(lastname)) {
			lastNameText.setError(getString(R.string.error_field_required));
			focusView = lastNameText;
			cancel = true;
		}
		if (TextUtils.isEmpty(email)) {
			emailText.setError(getString(R.string.error_field_required));
			focusView = emailText;
			cancel = true;
		}
		Log.d(TAG, "Almost good!");
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			
			String url = "http://murmuring-cliffs-5802.herokuapp.com/register";
			ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
			pos.add(new BasicNameValuePair("emailHandle", email));
			pos.add(new BasicNameValuePair("firstName", "" + firstname));
			pos.add(new BasicNameValuePair("lastName", "" + lastname));
			pos.add(new BasicNameValuePair("password", "" + password));
			pos.add(new BasicNameValuePair("lat", "" + null));
			pos.add(new BasicNameValuePair("lng", "" + null));
			
			HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
			rte.doPost(url, pos, HTTPBasicAuth.getInstance(), this);
		}
		
		
		
		//
		//Intent intent = new Intent(this, MainActivity.class);
		//startActivity(intent);
	}
	@Override
	public void networkRequestCompleted(HTTPRequestResult r) {
		
		try {
			JSONObject item = new JSONObject(r.getResult());
			Log.i("LoginActivity",r.getResult());
			if(item.getInt("status") == 0){
				Log.i("LoginActivity",HTTPBasicAuth.getInstance().getUsername());
				
				Intent i = new Intent(this,TribeSignalActivity.class);
				Bundle extras = new Bundle();
		    	extras.putString("username", email);
		    	extras.putString("password", password);
		    	Log.i("LoginActivity","registered");
		    	Log.i("LoginActivity","" + email);
		    	Log.i("LoginActivity","" + password);
		    	i.putExtras(extras);
				startActivity(i);
			}
		} catch (Exception e){
		}
	}

}
