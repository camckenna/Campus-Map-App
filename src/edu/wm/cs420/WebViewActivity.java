package edu.wm.cs420;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class WebViewActivity extends Activity {

	private String type;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		//type = getIntent().getStringExtra("type");
		//url = getIntent().getStringExtra("url");
		WebView myWebView = (WebView) findViewById(R.id.webview);		
		myWebView.loadUrl("http://www.wm.edu/about/visiting/campusmap/");
		}		

}
