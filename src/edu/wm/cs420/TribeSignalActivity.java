package edu.wm.cs420;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.esri.android.appframework.map.MapViewHelper;
import com.esri.android.map.Callout;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.Layer;
import com.esri.android.map.LocationService;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.ags.ArcGISTiledMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer.MODE;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnStatusChangedListener.STATUS;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;

import edu.wm.cs420.services.LocationUpdateService;
import edu.wm.cs420.web.HTTPBasicAuth;
import edu.wm.cs420.web.HTTPRequestResult;
import edu.wm.cs420.web.HTTPRequestTaskExecutor;
import edu.wm.cs420.web.HTTPRequestTask.NetworkListener;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TribeSignalActivity extends Activity implements NetworkListener{

	MapView map = null;
	MapViewHelper mvHelper;
	GraphicsLayer graphicsLayer;
	ArcGISFeatureLayer buildings;
	TextView status;
	Button toggleLocationButton;
	Button findFriends;
	private Graphic m_identifiedGraphic;

	RelativeLayout showLocation;
	private LocationUpdateService alarm;

	
	LocationService ls;
	Menu menu;
	boolean sharingLocation;
	
	private String username;
	private String password;

	private String featureServiceURL;
	@SuppressWarnings("serial")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tribe_signal);
		
	    if(getIntent() != null){
	    	username = getIntent().getStringExtra("username");
	    	password = getIntent().getStringExtra("password");	    	
	    }
	    
	    log(username);
		// Retrieve the map and initial extent from XML layout
				showLocation = (RelativeLayout) this.findViewById(R.id.showLocation);
				status = (TextView)findViewById(R.id.status);
				toggleLocationButton = (Button)findViewById(R.id.button);
				findFriends = (Button)findViewById(R.id.nearby);
				
				sharingLocation = false;
				alarm = new LocationUpdateService();
				alarm.SetAlarm(this, username, password);
				status.setText("Sharing Location On");
				toggleLocationButton.setText("Turn Sharing Off");
				
				
				map = (MapView)findViewById(R.id.map);
				mvHelper = new MapViewHelper(map); 
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
		            	identifyLocation( x, y);
		            	
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
			//log(m_identifiedGraphic.getAttributeValue("Lat").toString());
			//log(m_identifiedGraphic.getAttributeValue("Lng").toString());
			
			
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
						if(m_identifiedGraphic.getAttributeValue("NameFamil") != null){
							log(m_identifiedGraphic.getAttributeValue("NameFamil").toString());
							buildings.clear();
							String url = "http://murmuring-cliffs-5802.herokuapp.com/location/nearby";
							ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
							pos.add(new BasicNameValuePair("lat", m_identifiedGraphic.getAttributeValue("Long").toString()));
							pos.add(new BasicNameValuePair("lng", m_identifiedGraphic.getAttributeValue("Lat").toString()));
							log("Lat:"  + m_identifiedGraphic.getAttributeValue("Long").toString());
							HTTPBasicAuth.getInstance().setUsername(username);
							HTTPBasicAuth.getInstance().setPassword(password);
							HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
							rte.doGetWithParams(url, pos, HTTPBasicAuth.getInstance(), this);
						
						}
						else
							log("null");
						buildings.setSelectedGraphics(n, true);
						return;
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
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu=menu;
		getMenuInflater().inflate(R.menu.tribe_signal, menu);
		
		MenuItem item = menu.findItem(R.id.action_expand);
		item.setVisible(false);
		MenuItem item2 = menu.findItem(R.id.action_collapse);
		item2.setVisible(true);
		return true;
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
	        case R.id.logout:
	            logout();
	            return true;   
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	

	private void expandSearch() {
		MenuItem item = menu.findItem(R.id.action_expand);
		item.setVisible(false);
		
		MenuItem item2 = menu.findItem(R.id.action_collapse);
		item2.setVisible(true);
		
		showLocation.setVisibility(View.VISIBLE);
		
	}
	
	private void collapseSearch() {
		MenuItem item = menu.findItem(R.id.action_expand);
		item.setVisible(true);
		
		MenuItem item2 = menu.findItem(R.id.action_collapse);
		item2.setVisible(false);
		showLocation.setVisibility(View.GONE);
		
	}
	
	public void toggleLocation(View v){
		if(sharingLocation){
			sharingLocation = false;
			findFriends.setVisibility(View.GONE);
			status.setText("Sharing Location Off");
			log("Sharing off");
			alarm.CancelAlarm(this);
			toggleLocationButton.setText("Turn Sharing On");
			
			String url = "http://murmuring-cliffs-5802.herokuapp.com/user/me/stopSharingLocation";
			ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
			
			
			HTTPBasicAuth.getInstance().setUsername(username);
			HTTPBasicAuth.getInstance().setPassword(password);
			HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
			rte.doPost(url, pos, HTTPBasicAuth.getInstance(), this);
			
		}
		else{
			sharingLocation = true;
			findFriends.setVisibility(View.VISIBLE);
			log("Sharing on");
			status.setText("Sharing Location On");
			alarm.SetAlarm(this, username, password);
			toggleLocationButton.setText("Turn Sharing Off");
		}
	}

	public void logout(){
		username = null;
		password = null;
    	Intent intent = new Intent(this, LoginActivity.class);
    	intent.putExtra("logout", "true");        
        Toast.makeText(this, "Log out", Toast.LENGTH_SHORT).show();
        startActivity(intent);
		
	}
	public void sharedLocation(View v){
		toast("Shared location");
		buildings.clearSelection();
		LocationManager locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		Location loc = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		
		String url = "http://murmuring-cliffs-5802.herokuapp.com/user/me/nearby";
		ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
		pos.add(new BasicNameValuePair("lat", ""+loc.getLatitude()));
		pos.add(new BasicNameValuePair("lng", "" +loc.getLongitude()));
		
		
		HTTPBasicAuth.getInstance().setUsername(username);
		HTTPBasicAuth.getInstance().setPassword(password);
		HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
		rte.doPost(url, pos, HTTPBasicAuth.getInstance(), this);
	}
	public void networkRequestCompleted(HTTPRequestResult r) {
		
		try {
			Intent i = new Intent(this, FriendsActivity.class);
			i.putExtra("result", r.getResult());
			startActivity(i);
						
			
		} catch (Exception e){
			
			log(e.toString());
		}
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
		Log.d("TRIBE_SIGNAL", str);
	}
	public void toast(String string){
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

}
