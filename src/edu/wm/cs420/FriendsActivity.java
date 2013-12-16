package edu.wm.cs420;

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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.wm.cs420.NavigateActivity.FindRouteTask;
import edu.wm.cs420.utils.GMapV2Direction;

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

public class FriendsActivity extends Activity implements LocationListener {

	GoogleMap mMap;
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_friends);
	        
	        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
			 
	        // Showing status
	        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
	 
	            int requestCode = 10;
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	            dialog.show();
	 
	        }else { // Google Play Services are available
	        	
	        	String result = getIntent().getStringExtra("result");
	 
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
	}


