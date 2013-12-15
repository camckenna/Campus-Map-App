package edu.wm.cs420;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import edu.wm.cs420.utils.NavigateDialog;

public class Campus_MapActivity extends Activity {
	MapView map = null;
	GraphicsLayer graphicsLayer;
	ArcGISFeatureLayer buildings;
	AutoCompleteTextView searchBar;
	
	LinearLayout searchLayout;
	Intent toNavigation;
	
	LocationService ls;
	Menu menu;

	private Callout m_callout;
	private Graphic m_identifiedGraphic;

	private int m_calloutStyle;
	private ViewGroup calloutContent;
	private String featureServiceURL;
	

	//Need smaller buildings, 
	//Jamestown Rd. Houses, Several Admin buildings
	//Separate into smaller lists
    private static final String[] BUILDINGS = new String[] { "Morton Hall",
        "Wren Building", "Ewell Hall", "Washington Hall", "Tucker Hall", "Tyler Hall", 
        "James Blair Hall", "McGlothlin-Street Hall", "Blow Hall", 
        "Blow Memorial Hall", "Monroe Hall", "President's House", "Brafferton",
        "Old Dominion", "Bryan Complex", "Bryan", "Dawson", "Stith", "Madison", "Camm",
        "Zable Stadium", "Cary Field", "Montgomery Field", "Laycock Football Center", 
        "Sadler Center", "Student Health Center", "Landrum Hall", "Chandler Hall", 
        "Barrett Hall", "Jefferson Hall", "Brown Hall", "Campus Center", "Trinkle Hall",
        "Reves Hall", "Taliaferro Hall", "Admission (Undergraduate)", "Hunt Hall",
        "Jamestown North", "Jamestown South", "Barksdale Field", "ISC2 (Integrated Science)",
        "ISC1 (Integrated Science)","Millington Hall", "Swem Library", "Small Hall",
        "Jones Hall", "Muscarelle Museum of Art", "Andrews Hall", "Phi Beta Kappa Memorial Hall",
        "Lake Matoaka Amphitheatre", "Ropes Course", "Miller Hall (Mason Sch. of Bus.)", 
        "Matoaka Art Studio", "Parking Deck", "Police and Parking Svcs.", "Adair Hall", 
        "Keck Environmental Field Lab.", "Dinwiddie", "Nicholson", "Gooch", "Fauquier",
        "Spotswood", "Preston", "Giles", "Harrison", "Page", "Cabell", "Tazewell",
        "Nicholas", "Pleasants", "Yates Hall", "Tennis Courts", "Commons Dining Hall",
        "W&M Hall", "Busch Field", "Busch Tennis Courts", "Rec. Sports Center",
        "Intramural Fields", "School of Education ", "Albert Daly Field", 
        "Plumeri Park", "Alumni House", "Western Union Building", "Ludwell","Ludwell Apts: 100>700",
        "Ludwell Apts: 200>", "Ludwell Apts: 300>", "Ludwell Apts: 400>", "Ludwell Apts: 500>",
        "Ludwell Apts: 600>", "Ludwell Apts: 700>", "Botetourt Complex", "Randolph Complex",
        "Graduate Housing", "Graduate Housing: 100>900", "Grad Res: 200", "Grad Res: 300",
        "Grad Res: 400", "Grad Res: 500", "Grad Res: 600", "Grad Res: 700", "Grad Res: 800", 
        "Grad Res: 900", "Lodge 2: Daily Grind", "Lodge 4", "Lodge 6", "Lodge 8", 
        "Lodge 10", "Lodge 12", "Lodge 14",  "Lodge 16", "Unit A", "Unit B",
        "Units A,B,C,D,E", "Unit D", "Unit E", "Unit K", "Unit F", "Unit J",
        "Unit G", "Unit M", "Units F,G,H,J", "Units K,L,M", "The Units",
        "Green and Gold Village", "Cohen Career Center", "Sorority Court",
        "College Apartments","Sor 1: Kappa Kappa Gamma", "Sor 2: Alpha Chi Omega",
        "Sor 3: Chi Omega","Sor 4: Tri Delta", "Sor 5: Pi Beta Phi", 
        "Sor 6: Kappa Delta", "Sor 7: Delta Gamma", "Sor 8: Phi Mu", "Sor 9: Kappa Alpha Theta",
        "Sor 10: Bozarth", "Sor 11: Gamma Phi Beta", "Sor 12: Delta Phi (Mullen)"
        };

	/** Called when the activity is first created. */
	@SuppressWarnings("serial")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		searchLayout = (LinearLayout) this.findViewById(R.id.searchLayout);
		searchLayout.setVisibility(View.GONE);
		toNavigation = new Intent(this, NavigateActivity.class);
 
        
        searchBar = (AutoCompleteTextView)findViewById(R.id.editText1);
        searchBar.setThreshold(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, BUILDINGS);
        searchBar.setAdapter(adapter);
        
       
		// Retrieve the map and initial extent from XML layout
		map = (MapView)findViewById(R.id.map);
		
		// Basic Tile Map [basemap]
		map.addLayer(new ArcGISTiledMapServiceLayer("" +
		"http://services.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer"));
		
		//Buildings
		buildings = new ArcGISFeatureLayer("" +
				"http://services2.arcgis.com/Jpnn3mA8A0MzRijQ/arcgis/rest/services/Campus_Map_Files/FeatureServer/33", 
				MODE.SNAPSHOT);
		map.addLayer(buildings);
		
		//Walkways
		map.addLayer(new ArcGISFeatureLayer("" +
				"http://services2.arcgis.com/Jpnn3mA8A0MzRijQ/ArcGIS/rest/services/Campus_Map_Files/FeatureServer/28", 
				MODE.SNAPSHOT));
		
		graphicsLayer = new GraphicsLayer();
	    map.addLayer(graphicsLayer);
	    
	    m_calloutStyle = R.xml.identify_calloutstyle;
		LayoutInflater inflater = getLayoutInflater();
		m_callout = map.getCallout();
		// Get the layout for the Callout from
		// layout->identify_callout_content.xml
		calloutContent = (ViewGroup) inflater.inflate(
				R.layout.identify_callout_content, null);
		m_callout.setContent(calloutContent);
		
        map.setOnStatusChangedListener(
                new OnStatusChangedListener() {

            public void onStatusChanged(Object source, STATUS status) {

                if (source == map && status == STATUS.INITIALIZED) {
                	
	                    ls = map.getLocationService();
	                    ls.setAutoPan(false);
	
	                    ls.start();
	                    map.centerAt(ls.getPoint(), true);
                	
                 }
              }
          });

        map.setOnSingleTapListener(new OnSingleTapListener() {
        	 
            private static final long serialVersionUID = 1L;
       
            @Override
            public void onSingleTap(float x, float y) {
       
            	getWindow().setSoftInputMode(
            		      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
              /*if (map.isLoaded()) {
            	  SearchForFeatureByName("Morton Hall");
              }*/
            }
          });

     
   LocationService s = map.getLocationService();
    
     map.centerAt(s.getPoint(), true);

	}

	private void identifyLocation(float x, float y) {

		m_identifiedGraphic = null;
		// Find out if the user tapped on a feature
		SearchForFeature(x, y);

		// If the user tapped on a feature, then display information regarding
		// the feature in the callout
		if (m_identifiedGraphic != null) {
			log("On a feature");
		}
	}
	/**
	 * Sets the value of m_identifiedGraphic to the Graphic present on the
	 * location of screen tap
	 * 
	 * @param x
	 *            x co-ordinate of point
	 * @param y
	 *            y co-ordinate of point
	 */
	private void SearchForFeature(float x, float y) {

		Point mapPoint = map.toMapPoint(x, y);
		log("Point:" + x + "," + y);

		if (mapPoint != null) {

			for (Layer layer : map.getLayers()) {
				if (layer == null)
					continue;

				if (layer instanceof ArcGISFeatureLayer) {
					ArcGISFeatureLayer fLayer = (ArcGISFeatureLayer) layer;
					// Get the Graphic at location x,y
					
					m_identifiedGraphic = GetFeature(fLayer, x, y);
					if(m_identifiedGraphic != null && buildings != null){
						
						buildings.clearSelection();
						int[] n = new int[1];
						n[0] = (int) m_identifiedGraphic.getId();
						if(m_identifiedGraphic.getAttributeValue("NameFamil") != null)
							log(m_identifiedGraphic.getAttributeValue("NameFamil").toString());
						else
							log("null");
						buildings.setSelectedGraphics(n, true);
					}
				} else
					continue;
			}
		}
	}

	/**
	 * Returns the Graphic present the location of screen tap
	 * 
	 * @param fLayer
	 * @param x
	 *            x co-ordinate of point
	 * @param y
	 *            y co-ordinate of point
	 * @return Graphic at location x,y
	 */
	private Graphic GetFeature(ArcGISFeatureLayer fLayer, float x, float y) {

		// Get the graphics near the Point.
		int[] ids = fLayer.getGraphicIDs(x, y, 10, 1);
		if (ids == null || ids.length == 0) {
			return null;
		}
		log("Getting feature");
		Graphic g = fLayer.getGraphic(ids[0]);
		return g;
	}
	
	private boolean SearchForFeatureByName(String name) {

		if (name != null) {

			for (Layer layer : map.getLayers()) {
				if (layer == null)
					continue;

				if (layer instanceof ArcGISFeatureLayer) {
					ArcGISFeatureLayer fLayer = (ArcGISFeatureLayer) layer;
					// Get the Graphic at location x,y
					
					m_identifiedGraphic = GetFeatureByName(fLayer, name);
					if(m_identifiedGraphic != null && buildings != null){
						
						buildings.clearSelection();
						int[] n = new int[1];
						n[0] = (int) m_identifiedGraphic.getId();
						if(m_identifiedGraphic.getAttributeValue("NameFamil") != null)
							log(m_identifiedGraphic.getAttributeValue("NameFamil").toString());
						else
							log("null");
						buildings.setSelectedGraphics(n, true);
						double lat = Double.parseDouble(m_identifiedGraphic.getAttributeValue("Lat").toString());
						double lng = Double.parseDouble(m_identifiedGraphic.getAttributeValue("Long").toString());
						log(lat + ", " + lng);
						return true;
					}
				} else
					continue;
			}
		}
		return false;
	}
	private boolean SearchForFeatureByGroupName(List<String> names) {

		if (names != null && names.size() > 0) {
			log(""+names.size());
			for (Layer layer : map.getLayers()) {
				if (layer == null)
					continue;

				if (layer instanceof ArcGISFeatureLayer) {
					ArcGISFeatureLayer fLayer = (ArcGISFeatureLayer) layer;
					// Get the Graphic at location x,y
					
					m_identifiedGraphic = GetFeatureByName(fLayer, names.get(0));
					if(m_identifiedGraphic != null && buildings != null){
						buildings.clearSelection();
						
						int[] n = new int[names.size()];
						for(int x = 0; x < names.size(); x++){
							m_identifiedGraphic = GetFeatureByName(fLayer, names.get(x));
							if(m_identifiedGraphic != null){
								n[x] = (int) m_identifiedGraphic.getId();	
								//if(m_identifiedGraphic.getAttributeValue("NameFamil") != null)
									//log(m_identifiedGraphic.getAttributeValue("NameFamil").toString());
								//else
									//log("null");
							}
							else{
								log(names.get(x));
							}
						}						
						
						buildings.setSelectedGraphics(n, true);
						
						return true;
					}
				} else
					continue;
			}
		}
		return false;
	}
	private Graphic GetFeatureByName(ArcGISFeatureLayer fLayer, String name){
		int[] ids = fLayer.getGraphicIDs();
		List<Graphic> list = new ArrayList<Graphic>();
		if (ids == null || ids.length == 0) {
			return null;
		}
		for(int x = 0; x < ids.length; x++){
			list.add(fLayer.getGraphic(ids[x]));
		}
		
		for(Graphic g : list){
			Object feature = g.getAttributeValue("NameFamil");
			if(feature != null && feature.toString().equals(name)){
				log(g.getAttributes().toString());
				return g;				
			}
		}
		return null;
		
	}

public void searchBuilding(View v){
	/*
	 * Hide the keyboard
	 */
	InputMethodManager inputManager = 
	        (InputMethodManager)
	            getSystemService(Context.INPUT_METHOD_SERVICE); 
	inputManager.hideSoftInputFromWindow(
	        this.getCurrentFocus().getWindowToken(),
	        InputMethodManager.HIDE_NOT_ALWAYS); 
	
	boolean flag = false;
	
	//Hall 
	String name = searchBar.getText().toString();
	if(name.equals("Dillard")){
		List<String> names = new ArrayList<String>();
		names.add("Hughes Hall");
		names.add("Munford Hall");
		flag = SearchForFeatureByGroupName(names);
	}
	else if(name.equals("Sorority Court")){
		List<String> names = new ArrayList<String>();
		names.add("Sor 1: Kappa Kappa Gamma");
		names.add("Sor 2: Alpha Chi Omega");
		names.add("Sor 3: Chi Omega");
		names.add("Sor 4: Tri Delta");
		names.add("Sor 5: Pi Beta Phi");
		names.add("Sor 6: Kappa Delta");
		names.add("Sor 7: Delta Gamma");
		names.add("Sor 8: Phi Mu");
		names.add("Sor 9: Kappa Alpha Theta");
		names.add("Sor 10: Bozarth");
		names.add("Sor 11: Gamma Phi Beta");
		names.add("Sor 12: Delta Phi (Mullen)");
		
		flag = SearchForFeatureByGroupName(names);
		
	}
	else if(name.equals("Graduate Housing")){
		List<String> names = new ArrayList<String>();
		names.add("Grad Res: 900");
		names.add("Grad Res: 800");
		names.add("Grad Res: 700");
		names.add("Grad Res: 600");
		names.add("Grad Res: 500");
		names.add("Grad Res: 400");
		names.add("Grad Res: 300");
		names.add("Grad Res: 200");
		names.add("Graduate Housing: 100>900");
	}
	else if(name.equals("Ludwell")){
		List<String> names = new ArrayList<String>();
		names.add("Ludwell Apts: 100>700");
		names.add("Ludwell Apts: 200>");
		names.add("Ludwell Apts: 300>");
		names.add("Ludwell Apts: 400>");
		names.add("Ludwell Apts: 500>");
		names.add("Ludwell Apts: 600>");
		names.add("Ludwell Apts: 700>");
		
		flag = SearchForFeatureByGroupName(names);
		
	}
	else if(name.equals("The Units") || name.equals("Green and Gold Village")){
		List<String> names = new ArrayList<String>();
		names.add("Unit A");
		names.add("Unit B");
		names.add("Unit D");
		names.add("Unit E");
		names.add("Unit F");
		names.add("Unit G");
		names.add("Unit J");
		names.add("Unit K");
		names.add("Unit M");
		names.add("Units A,B,C,D,E");
		names.add("Units K,L,M");
		names.add("Units F,G,H,J");
		
		flag = SearchForFeatureByGroupName(names);
		
	}
	else if(name.equals("Randolph Complex")){
		
		List<String> names = new ArrayList<String>();
		names.add("Preston");
		names.add("Giles");
		names.add("Harrison");
		names.add("Page");
		names.add("Cabell");
		names.add("Tazewell");
		names.add("Nicholas");
		names.add("Pleasants");
		flag = SearchForFeatureByGroupName(names);
		
	}
	else if(name.equals("Botetourt Complex")){
		List<String> names = new ArrayList<String>();
		names.add("Dinwiddie");
		names.add("Nicholson");
		names.add("Gooch");
		names.add("Fauquier");
		names.add("Spotswood");
		flag = SearchForFeatureByGroupName(names);
	}
	else if(name.equals("The Lodges")){
		List<String> names = new ArrayList<String>();
		names.add("Lodge 2: Daily Grind");
		names.add("Lodge 4");
		names.add("Lodge 6");
		names.add("Lodge 8");
		names.add("Lodge 10");
		names.add("Lodge 12");
		names.add("Lodge 14");
		names.add("Lodge 16");
		flag = SearchForFeatureByGroupName(names);
	}
	else if(name.equals("School of Education")){
		name = "School of Education (future site)";
		flag = SearchForFeatureByName(name);
	}
	else if(name.equals("Cohen Career Center")){
		name = "Sadler Center";
		flag = SearchForFeatureByName(name);
	}
	else{
		flag = SearchForFeatureByName(name);
	}
	
	
	if(flag){
		//Only navigate if Google Play Services is updated
		if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) 
				== ConnectionResult.SUCCESS){
			
				toNavigation.putExtra("destination", name);
				toNavigation.putExtra("lng", m_identifiedGraphic.getAttributeValue("Lat").toString());
				toNavigation.putExtra("lat", m_identifiedGraphic.getAttributeValue("Long").toString());
				log("LatLng: " + m_identifiedGraphic.getAttributeValue("Lat").toString() + "," + m_identifiedGraphic.getAttributeValue("Long").toString());
				AlertDialog.Builder alert = new AlertDialog.Builder(this);
			    alert.setTitle("Navigation");
			    alert.setCancelable(false);
			    alert.setMessage("Navigate to " + name + "?");
			    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			        	
			    	    startActivity(toNavigation);
			        }
			    });
			    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			        @Override
			        public void onClick(DialogInterface dialog, int which) {
			       }
			    });
			    alert.show();
		}
		else{
			GooglePlayServicesUtil.getErrorDialog(
					GooglePlayServicesUtil.isGooglePlayServicesAvailable(this), 
					this, 0);
		}
	}
	else{
		buildings.clear();
		toast("Not a building name");
	}
	
} 


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
		this.menu = menu;
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.actions, menu);	
	    
	    MenuItem item = menu.findItem(R.id.action_expand);
		item.setVisible(true);
		MenuItem item2 = menu.findItem(R.id.action_collapse);
		item2.setVisible(false);
	    return super.onCreateOptionsMenu(menu);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_collapse:
	            collapseSearch();
	            return true;
	        case R.id.action_expand:
	            expandSearch();
	            return true;
	            
	        case R.id.pdfcampus:
	            showCampusPDF();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showCampusPDF() {
		//Current
		Intent intent = new Intent(this,WebViewActivity.class);
		intent.putExtra("URL", "http://gisfiles.wm.edu/campusmap/wmmap.pdf");
	    startActivity(intent);
	}

	private void expandSearch() {
		MenuItem item = menu.findItem(R.id.action_expand);
		item.setVisible(false);
		
		MenuItem item2 = menu.findItem(R.id.action_collapse);
		item2.setVisible(true);
		
		searchLayout.setVisibility(View.VISIBLE);
		
	}
	
	private void collapseSearch() {
		MenuItem item = menu.findItem(R.id.action_expand);
		item.setVisible(true);
		
		MenuItem item2 = menu.findItem(R.id.action_collapse);
		item2.setVisible(false);
		searchLayout.setVisibility(View.GONE);
		
	}

	protected void onPause() {
		super.onPause();
		map.pause();
	}

	protected void onResume() {
		super.onResume();
		map.unpause();
	}
	
	public void log(String str){
		Log.d("MAP_APP", str);
	}
	public void toast(String string){
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}
}
