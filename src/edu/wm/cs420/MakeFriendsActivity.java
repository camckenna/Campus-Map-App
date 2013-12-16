package edu.wm.cs420;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.wm.cs420.web.HTTPBasicAuth;
import edu.wm.cs420.web.HTTPRequestResult;
import edu.wm.cs420.web.HTTPRequestTaskExecutor;
import edu.wm.cs420.web.HTTPRequestTask.NetworkListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MakeFriendsActivity extends Activity  implements NetworkListener {

	private AutoCompleteTextView usernameTextView;
	private List<String> publicUsernames;
	private Button sendRequestButton;
	private TextView errors;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_make_friends);
		errors = (TextView)findViewById(R.id.makeFriendsError);
		usernameTextView = (AutoCompleteTextView)findViewById(R.id.connectUsername);
        usernameTextView.setThreshold(1);
        final NetworkListener listener = this;
        sendRequestButton = (Button)findViewById(R.id.sendRequest);
        sendRequestButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		String url = "http://murmuring-cliffs-5802.herokuapp.com/user/me/makeRequest";
        		ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
        		pos.add(new BasicNameValuePair("emailHandle", usernameTextView.getText().toString()));
        		HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
        		rte.doPost(url, pos, HTTPBasicAuth.getInstance(), listener);
        	}
        });
        String url = "http://murmuring-cliffs-5802.herokuapp.com/user/regex";
		ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
		pos.add(new BasicNameValuePair("regex", ".*"));
		HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
		rte.doGetWithParams(url, pos, HTTPBasicAuth.getInstance(), this);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.make_friends, menu);
		return true;
	}

	@Override
	public void networkRequestCompleted(HTTPRequestResult r) {
		JSONObject f;
		if (r.getUrl().endsWith("makeRequest")) {
			try {
				f = new JSONObject(r.getResult());
				if (!f.get("status").equals("0")) {
					errors.setText((String)f.get("object"));
				} else {
					errors.setText("");
					toast("Request has been sent!");
				}
				
			} catch(Exception e) {
				
			}
		} else {
			try {
				f = new JSONObject(r.getResult());
			
			JSONArray json = f.getJSONArray("object");
			List<String> usernames = new ArrayList<String>();
			for (int i = 0; i < f.length(); i++) {
				JSONObject obj = json.getJSONObject(i);
				usernames.add(obj.getString("emailHandle"));
			}
			publicUsernames = usernames;
			 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		                android.R.layout.simple_dropdown_item_1line, publicUsernames);
		        usernameTextView.setAdapter(adapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
	public void toast(String string){
		Toast.makeText(this, string, Toast.LENGTH_LONG).show();
	}
}
