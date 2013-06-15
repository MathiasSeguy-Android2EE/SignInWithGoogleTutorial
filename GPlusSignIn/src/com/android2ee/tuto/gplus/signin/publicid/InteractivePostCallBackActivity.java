/**<ul>
 * <li>Goi13SignInUsingGPlus</li>
 * <li>com.android2ee.project.gplus.signin.publicid</li>
 * <li>30 mai 2013</li>
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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.plus.PlusShare;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to handle Intent coming from moment clicked in GooglePlus
 *        that Intent has sent in the device the intent catched here.
 *        The Intent has been build (in a way) by the method that wrote the moment in GPlus
 * @see com.android2ee.project.gplus.signin.publicid.ui.personfragment.PersonFragmentModel#writeMomentInStream
 */
public class InteractivePostCallBackActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Retrieve the DeepLink
		Uri deepLink = Uri.parse(PlusShare.getDeepLinkId(getIntent()));
		// Define the Activity that will be launched
		Intent target = new Intent();
		// You can explore your link to understant whioch activty you want to launch
		// As there is only one here, no matter what is found
		// so parse your uri, mine look like something like that /user/test/?action=see
		if (deepLink.isHierarchical() && deepLink.getPath().startsWith("/user/")) {
			// Get extra to better target the activity to launch
			Log.e("InteractivePostCallBackActivity", "starting with user");
			target.putExtra("profileId", deepLink.getLastPathSegment());// should be 1157887702919748841004
			Log.e("InteractivePostCallBackActivity", "profileId found : "+deepLink.getLastPathSegment());
			// Do the same for parameters
			if (deepLink.getQueryParameter("action") != null) {
				if (deepLink.getQueryParameter("action").contains("see")) {
					Log.e("InteractivePostCallBackActivity", "action see found : ");
					target.putExtra("action", "see");
				}
			}
			//Then decide which activity you want to launch
			target.setClass(getApplicationContext(), MainActivity.class);
		}else {
			Log.e("InteractivePostCallBackActivity", "Not starting with user");
		}
		//Launch the target activity
		startActivity(target);
		// and die
		finish();
	}

}
