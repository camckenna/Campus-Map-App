package edu.wm.cs420;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.wm.cs420.NavigateActivity.FindRouteTask;
import edu.wm.cs420.utils.GMapV2Direction;
import edu.wm.cs420.web.HTTPBasicAuth;
import edu.wm.cs420.web.HTTPRequestResult;
import edu.wm.cs420.web.HTTPRequestTask.NetworkListener;
import edu.wm.cs420.web.HTTPRequestTaskExecutor;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class FriendsActivity extends Activity implements LocationListener, OnMapClickListener, NetworkListener {

	GoogleMap mMap;
	String username;
	String password;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_friends);
	        
	        username = getIntent().getStringExtra("username");
	        password = getIntent().getStringExtra("password");
	        
	        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
			 
	        // Showing status
	        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
	 
	            int requestCode = 10;
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	            dialog.show();
	 
	        }else { // Google Play Services are available
	        	
	        	
	 
	        	mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
	    		
	    		setUpMapIfNeeded();
	    		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	    		Criteria criteria = new Criteria();
	    		String provider = locationManager.getBestProvider(criteria, true);
	    		Location location = locationManager.getLastKnownLocation(provider);
	 
	            if(location!=null){
	                onLocationChanged(location);
	            }
	            mMap.setMyLocationEnabled(true);
	            locationManager.requestLocationUpdates(provider, 20000, 0, this);
	            mMap.setOnMapClickListener(this);
	            /*
	            String result = getIntent().getStringExtra("result");
	            log("" + result);
	            
				JSONObject f;
				try {
					f = new JSONObject(result);
				
				JSONArray json = f.getJSONArray("object");
				for(int x = 0; x <json.length(); x++){
					JSONObject obj = json.getJSONObject(x);
					String name = obj.getString("firstName") +" "+ obj.getString("lastName");
					double lng = obj.getDouble("longitude");
					double lat = obj.getDouble("latitude");
					
					log(name);
					log(""+lat);
					
					mMap.addMarker(new MarkerOptions()
	            	.position(new LatLng(lat, lng))
	            	.title(name)
	                .draggable(false));
					
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        */
	        	
	        }
	        

	        
		        
	    }
	    
	    
	   	 public void onLocationChanged(Location location) {
	   		 
		        // Getting latitude of the current location
		        double latitude = location.getLatitude();
		 
		        // Getting longitude of the current location
		        double longitude = location.getLongitude();
		 
		        // Creating a LatLng object for the current location
		        LatLng latLng = new LatLng(latitude, longitude);
		 
		        // Showing the current location in Google Map
		       mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		 
		        // Zoom in the Google Map
		        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		 
		    }
		 
	    	private void setUpMapIfNeeded() {
	    	    // Do a null check to confirm that we have not already instantiated the map.
	    	    if (mMap == null) {
	    	        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
	    	                            .getMap();
	    	        // Check if we were successful in obtaining the map.
	    	        if (mMap != null) {
	    	        	mMap.setMyLocationEnabled(true);

	    	        }
	    	    }
	    	}
	    	
		    @Override
		    public void onProviderDisabled(String provider) {
		    }
		 
		    @Override
		    public void onProviderEnabled(String provider) {
		    }
		 
		    @Override
		    public void onStatusChanged(String provider, int status, Bundle extras) {
		       
		    }
		    
		    public void log(String str){
				Log.d("Navigate", str);
			}


			@Override
			public void onMapClick(LatLng point) {
				String url = "http://murmuring-cliffs-5802.herokuapp.com/location/nearby";
				ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
				pos.add(new BasicNameValuePair("lat", point.latitude+""));
				pos.add(new BasicNameValuePair("lng", point.longitude+""));
				log("Lat:"  + point.latitude+"");
				HTTPBasicAuth.getInstance().setUsername(username);
				HTTPBasicAuth.getInstance().setPassword(password);
				HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
				rte.doGetWithParams(url, pos, HTTPBasicAuth.getInstance(), this);
			}


			@Override
			public void networkRequestCompleted(HTTPRequestResult r) {
				String result=r.getResult();
				JSONObject f;
				try {
					f = new JSONObject(result);
				
				JSONArray json = f.getJSONArray("object");
				
				String toastStr;
				if (json.length() == 1) {
					toastStr = "You have 1 friend near that location.";
				} else {
					toastStr = "You have "+json.length()+" friends near that location.";
				}
				Toast.makeText(this, toastStr, Toast.LENGTH_LONG).show();
				
				for(int x = 0; x <json.length(); x++){
					JSONObject obj = json.getJSONObject(x);
					String name = obj.getString("firstName") +" "+ obj.getString("lastName");
					double lng = obj.getDouble("longitude");
					double lat = obj.getDouble("latitude");
					
					log(name);
					log(""+lat);
					
					mMap.addMarker(new MarkerOptions()
	            	.position(new LatLng(lat, lng))
	            	.title(name)
	                .draggable(false));
					
				}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	}


