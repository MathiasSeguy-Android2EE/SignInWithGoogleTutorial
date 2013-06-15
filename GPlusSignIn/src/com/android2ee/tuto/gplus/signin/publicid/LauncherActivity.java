/**<ul>
 * <li>MyGoooglePublicProfile</li>
 * <li>com.android2ee.project.gplus.signin.publicid</li>
 * <li>7 juin 2013</li>
 * 
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage except training and can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * 
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.tuto.gplus.signin.publicid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to launch the main application if GoogleService are available on the device
 *        Else it launches the google service utilities asking the user to download it from Google and if it's done with
 *        success launch the main Activity
 */
public class LauncherActivity extends Activity {
	/**
	 * The Tag for log
	 */
	private static final String TAG = "MainActivity";
	// Define the Activity that will be launched
	private Intent target = new Intent();
	/**
	 * The expected code for the method activityResult
	 */
	private int activityResult = 11021974;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int gpsuStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (gpsuStatusCode != ConnectionResult.SUCCESS) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(gpsuStatusCode, this, activityResult);
			dialog.show();
		} else {
			// Then decide which activity you want to launch
			target.setClass(getApplicationContext(), MainActivity.class);
			// Launch the target activity
			startActivity(target);
			finish();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == activityResult && resultCode == RESULT_OK) {
			// Then decide which activity you want to launch
			target.setClass(getApplicationContext(), MainActivity.class);
			// Launch the target activity
			startActivity(target);
		}
		// what ever happens, die
		finish();
	}
}
