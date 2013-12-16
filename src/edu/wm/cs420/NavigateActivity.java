package edu.wm.cs420;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import edu.wm.cs420.utils.GMapV2Direction;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.IntentSender;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class NavigateActivity extends Activity implements LocationListener
{
	private GoogleMap mMap;
	private String name;
	private double lat;
	private double lng;
	private LatLng start;
	private LatLng end;
	GMapV2Direction md;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigate);
		name = getIntent().getStringExtra("destination");
		lat = Double.parseDouble(getIntent().getStringExtra("lat"));
		lng = Double.parseDouble(getIntent().getStringExtra("lng"));
		log("LL: " + lat +"," + lng);
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
	            
	            double latitude = location.getLatitude();
		        double longitude = location.getLongitude();
		 
		        start = new LatLng(latitude, longitude);
				log(lat+","+lng);
				end = new LatLng(lat,lng);
			    md = new GMapV2Direction();
			   new FindRouteTask().execute("");
			    //findDirections(latitude,longitude,lat, lng, GMapV2Direction.MODE_WALKING );
			            
		        
	        }
		
	}
	
	class FindRouteTask extends AsyncTask<String, Void, Document> {


	    protected Document doInBackground(String... urls) {
	    	Document doc = md.getDocument(start, end,
                    GMapV2Direction.MODE_WALKING);
            
            log("" + (doc != null));
            return doc;
	    }

	    protected void onPostExecute(Document doc) {
	    	 try {
		            ArrayList<LatLng> directionPoint = md.getDirection(doc);
		            PolylineOptions rectLine = new PolylineOptions().width(3).color(
		                    Color.RED);

		            for (int i = 0; i < directionPoint.size(); i++) {
		                rectLine.add(directionPoint.get(i));
		            }
		            mMap.addPolyline(rectLine);
		            mMap.addMarker(new MarkerOptions()
		            	.position(end)
		            	.title(name)
		                .draggable(false));
		            log("Success");
		        } catch (Exception e){ 
		            log(e.toString());
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
	 
	    @Override
	    public void onProviderDisabled(String provider) {
	    }
	 
	    @Override
	    public void onProviderEnabled(String provider) {
	    }
	 
	    @Override
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	       
	    }
    
	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigate, menu);
		return true;
	}
  
	public void log(String str){
		Log.d("Navigate", str);
	}
    

}
