package edu.wm.cs420;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.map.FeatureSet;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.tasks.ags.query.Query;
import com.esri.core.tasks.ags.query.QueryTask;

public class Campus_MapActivity extends Activity {
	MapView map = null;
    private static final String[] COUNTRIES = new String[] { "Belgium",
        "France", "France_", "Italy", "Germany", "Spain" };

	/** Called when the activity is first created. */
	@SuppressWarnings("serial")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        // actionBar.setDisplayShowTitleEnabled(false);
        // actionBar.setIcon(R.drawable.ic_action_search);    

        /*
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        AutoCompleteTextView textView = (AutoCompleteTextView)findViewById(R.id.editText1);
        textView.setAdapter(adapter);
        textView.setThreshold(0);
         */
		// Retrieve the map and initial extent from XML layout
		map = (MapView)findViewById(R.id.map);
		// Add dynamic layer to MapView
		map.addLayer(new ArcGISTiledMapServiceLayer("" +
		"http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer"));
		
        map.setOnStatusChangedListener(
                new OnStatusChangedListener() {

            public void onStatusChanged(Object source, STATUS status) {

                if (source == map && status == STATUS.INITIALIZED) {

                    LocationService ls = map.getLocationService();

                    ls.setAutoPan(false);

                    ls.start();
                 }
              }
          });

        

     
     LocationService s = map.getLocationService();
     map.centerAt(s.getPoint(), true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions, menu);
	    
	    SearchManager searchManager =
	            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	     SearchView searchView =
	             (SearchView) menu.findItem(R.id.action_search).getActionView();
	     searchView.setSearchableInfo(
	             searchManager.getSearchableInfo(getComponentName()));

	     
	    return super.onCreateOptionsMenu(menu);
	}
	
	protected void onPause() {
		super.onPause();
		map.pause();
	}

	protected void onResume() {
		super.onResume();
		map.unpause();
	}
}
