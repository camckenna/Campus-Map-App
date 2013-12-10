package edu.wm.cs420;

import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class SearchableActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable);
		
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    Log.d("SEARCH", intent.getAction());
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      Log.d("SEARCH", "Toast?");
	      search(query);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchable, menu);
		return true;
	}
	
	public void search(String query){
		Toast.makeText(this, "The query was: " + query, Toast.LENGTH_SHORT).show();
	}

}
