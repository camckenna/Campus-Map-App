package edu.wm.cs420.services;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import edu.wm.cs420.R;
import edu.wm.cs420.TribeSignalActivity;
import edu.wm.cs420.web.HTTPBasicAuth;
import edu.wm.cs420.web.HTTPRequestResult;
import edu.wm.cs420.web.HTTPRequestTaskExecutor;
import edu.wm.cs420.web.HTTPRequestTask.NetworkListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

public class LocationUpdateService extends BroadcastReceiver implements NetworkListener{

	private String mUsername;
	private String mPassword;
	private double lat;
	private double lng;
	
	public LocationUpdateService(){
		super();
	}
	public LocationUpdateService(String mUsername, String mPassword) {
		super();
		this.mUsername = mUsername;
		this.mPassword = mPassword;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("LocationUpdate", "In Alarm");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        
        mUsername = intent.getStringExtra("username");
	    	mPassword = intent.getStringExtra("password");
        
        Log.d("LocationUpdate", ""+mUsername);
	if (mUsername != null) {
		LocationManager locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		Location loc = locManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Log.d("LocationUpdate", " location" + loc.getLatitude());
		lat = loc.getLatitude();
		lng = loc.getLongitude();
		
		String url = "http://murmuring-cliffs-5802.herokuapp.com/user/me";
		ArrayList<NameValuePair> pos = new ArrayList<NameValuePair>();
		pos.add(new BasicNameValuePair("lat", ""+lat));
		pos.add(new BasicNameValuePair("lng", "" + lng));
		
		
		HTTPBasicAuth.getInstance().setUsername(mUsername);
		HTTPBasicAuth.getInstance().setPassword(mPassword);
		HTTPRequestTaskExecutor rte = new HTTPRequestTaskExecutor();
		rte.doPost(url, pos, HTTPBasicAuth.getInstance(), this);
	}
        wl.release();
	}
    public void SetAlarm(Context context, String username, String password)
    {
    	Log.d("LocationUpdate", "Alarm starting");
        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LocationUpdateService.class);
        Bundle extras = new Bundle();
    	extras.putString("username", username);
    	extras.putString("password", password);
    	i.putExtras(extras);
    	Log.d("LocationUpdate", "Username: "+username);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 2, pi); // Millisec * Second * Minute
        /*
         * PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);       
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
        		60*1000, pintent);
         */
    }

    public void CancelAlarm(Context context)
    {
    	Log.d("LocationUpdate", "Stopping Alarm");
        Intent intent = new Intent(context, LocationUpdateService.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
	public void networkRequestCompleted(HTTPRequestResult r) {
		
		try {
			Log.i("LocationUpdate",r.getResult());
			
		} catch (Exception e){
			
			Log.d("LocationUpdate", e.toString());
		}
	}
	
	

}
