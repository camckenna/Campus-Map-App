package edu.wm.cs420.utils;

import com.esri.android.map.LocationService;

import android.content.DialogInterface;
import android.util.Log;

public class NavigateDialog implements DialogInterface.OnClickListener {

	private String dest;
	public NavigateDialog(String dest) {
		this.dest=dest;
		
	}
	
	

	 @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	        	log("Navigate to " + dest);
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	        	log(" Don't Navigate");
	            break;
	        }
	    }
	 public void log(String str){
			Log.d("MAP_APP", str);
		}

}
