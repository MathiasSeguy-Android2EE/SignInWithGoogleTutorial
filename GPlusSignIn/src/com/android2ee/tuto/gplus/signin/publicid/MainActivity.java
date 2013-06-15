package com.android2ee.tuto.gplus.signin.publicid;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;
import com.google.android.gms.plus.PlusShare;
import com.google.android.gms.plus.model.moments.ItemScope;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.people.Person;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {
	/******************************************************************************************/
	/**
	 * The Tag for log
	 */
	private static final String TAG = "MainActivity";
	/******************************************************************************************/
	/** Managing the Google+ SignIn **************************************************************************/
	/******************************************************************************************/
	/**
	 * The PlusClient
	 */
	private PlusClient mPlusClient;
	/**
	 * The connectionResult returned by the last connection try
	 */
	private ConnectionResult mConnectionResult;
	/**
	 * The SignIn native button
	 */
	private SignInButton signInButton;
	/**
	 * The request code for the Intent connection launch by startActivityForResult
	 */
	private final static int requestCodeResolverError = 12354;
	/**
	 * A boolean to know is the user asked for the connection or if it has be done by code (ex: in onStart)
	 */
	private boolean connectionAskedByUser = false;
	/******************************************************************************************/
	/** Managing Button **************************************************************************/
	/******************************************************************************************/
	/**
	 * The button disconect
	 */
	private Button btnDisconnect;
	/**
	 * The button revoke
	 */
	private Button btnRevoke;
	/**
	 * The button clear
	 */
	private Button btnClear;
	/**
	 * The button clear
	 */
	private Button btnWriteInteractivePost;
	/**
	 * The button clear
	 */
	private Button btnWriteMoment;
	/**
	 * The string representation of the g+ profile of the owner
	 */
	private TextView txvPersonPresentation;

	/******************************************************************************************/
	/** Managing life cycle **************************************************************************/
	/******************************************************************************************/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Build your GPlus Person:
		// Register for AddActivity:This type of app activity is the generic fallback type. Use it when no other app
		// activity type is appropriate.
		// for DiscoverActivity:Use this type when your user discovers something in your app, such as discovering a
		// new album.
		// And for CommentActivity: Use this type when your user comments on an article, blog entry, or other
		// creative work.
		// And define your scope because writeMoment requires the PLUS_LOGIN OAuth 2.0 scope specified in the
		// PlusClient constructor.
		// http://stuff.mit.edu:8001/afs/sipb/project/android/OldFiles/sdk/android-sdk-linux/extras/google/google_play_services/docs/reference/com/google/android/gms/plus/PlusClient.html
		mPlusClient = new PlusClient.Builder(this, this, this)
				.setVisibleActivities("http://schemas.google.com/AddActivity",
						"http://schemas.google.com/CommentActivity", "http://schemas.google.com/DiscoverActivity")
				.setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE).build();

		txvPersonPresentation = (TextView) findViewById(R.id.txvPersonPresentation);
		// .setScopes(Scopes.PLUS_LOGIN, Scopes.PLUS_PROFILE).build();
		// Add a listener to the SignInButton and link it to the connection
		signInButton = (SignInButton) findViewById(R.id.btnSignIn);
		signInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				gPlusSignInClicked();

			}
		});

		// Add a listener to the SignOutButton and link it to the disconnection
		btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
		btnDisconnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				disconnect();
			}
		});

		// Add a listener to the SignOutButton and link it to the disconnection
		btnClear = (Button) findViewById(R.id.btnClearDefaultAccount);
		btnClear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clear();
			}
		});

		// Add a listener to the revoke and link it to the disconnection
		btnRevoke = (Button) findViewById(R.id.btnRevoke);
		btnRevoke.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				revoke();
			}
		});

		// Add a listener to the revoke and link it to the disconnection
		btnWriteInteractivePost = (Button) findViewById(R.id.btnWriteIPost);
		btnWriteInteractivePost.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendInteractivePost("My message (should be dynamic:)");
			}
		});

		// Add a listener to the revoke and link it to the disconnection
		btnRevoke = (Button) findViewById(R.id.btnWriteMoment);
		btnRevoke.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				writeMomentInStream();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG, "onStart");
		// Ensure calling that method in onStart not in onResume (because onResume is called to often and to late in the
		// activity life cycle.
		// Begin the service connection: retrieve if the user is already connected (throug the web or something else)
		// and/or just connect the user
		// It has to be done at the beginning of your activity/application to ensure an user is in
		if (!mPlusClient.isConnected() && !mPlusClient.isConnecting()) {
			// Be sure you'are not attempting to connect when you are connected or when you are connecting
			// This method will try to connect, it succeeds only if the user has already grants the applications' right
			// using the G+SignIn flow.
			// Else a ErrorIntent will be received
			mPlusClient.connect();
			// When the result is coming back, it will be handle by onConnected or onConnectionFailed
		}
		// If is connecting or connected, also disable the signin button
		if (mPlusClient.isConnected() || mPlusClient.isConnecting()) {
			signInButton.setEnabled(false);
		}

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		super.onStop();
		Log.e(TAG, "onStop");
		// sure to not leak, close the connection. Each time you call connect you should call disconnect
		mPlusClient.disconnect();
	}

	/******************************************************************************************/
	/** Managing connection **************************************************************************/
	/******************************************************************************************/
	/**
	 * Called when the plus button is hit
	 */
	private void gPlusSignInClicked() {
		Log.e(TAG, "gPlusSignInClicked");
		Log.e(TAG, "mPlusClient.isConnected(): " + mPlusClient.isConnected() + " mConnectionResult==null "
				+ (mConnectionResult == null) + " => " + (!mPlusClient.isConnected() && mConnectionResult != null));
		connectionAskedByUser = true;
		if (!mPlusClient.isConnected() && !mPlusClient.isConnecting() && mConnectionResult != null) {
			// In this case you have already called mPlusClient.connect (because mConnRes!=null) but it failed
			// So you try to launch the resolution of that failure by calling startResolutionFR which you should display
			// the G+SignIn flow
			try {
				mConnectionResult.startResolutionForResult(this, requestCodeResolverError);
			} catch (SendIntentException e) {
				Log.e(TAG, "SendIntentException");
				// It also failed, you' re a lucky guy today,
				// So you restart the connection process again.
				// (trick:at this step you should know if there is a web-connection to stop trying to connect)
				mConnectionResult = null;
				mPlusClient.connect();
				signInButton.setEnabled(false);
			}
		} else if (!mPlusClient.isConnected() && !mPlusClient.isConnecting() && mConnectionResult == null) {
			// This case is called when you haven't already try to connect (meaning you don't have the
			// mPlusClient.connect in your onResume or onStart method)
			// In our case, we will never go through this code
			// just try to connect
			mPlusClient.connect();
			signInButton.setEnabled(false);
		} else {
			// Something forgotten or what ?
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// This method is called when the activity of connection started by
		// mConnectionResult.startResolutionForResult(this, requestCodeResolverError); has finished
		// It has displayed the connection activity made by Google Fellow.
		Log.e(TAG, "onActivityResult");
		if (requestCode == requestCodeResolverError && resultCode == RESULT_OK) {
			Log.e(TAG, " onActivityResult RESULT_OK");
			// hey now you have the rights granted the call to connect will work
			mConnectionResult = null;
			mPlusClient.connect();
			signInButton.setEnabled(false);
		} else {
			// ensure the connection is reset, else you won't be able to log your user in
			// because of the condition if (!mPlusClient.isConnected() && mConnectionResult != null) {
			// in the ClickListener of the SignInButton
			mConnectionResult = null;
			Log.e(TAG, "onActivityResult SendIntentException");
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/******************************************************************************************/
	/** Managing disconnection **************************************************************************/
	/******************************************************************************************/

	/**
	 * Called when the button disconnect is clicked
	 */
	private void clear() {
		if (mPlusClient.isConnected()) {
			// First case: Give your user the ability to choose an another account at the next connection
			// -------------------------------------------------------------------------------------------
			// First reset the G+ account bound to the application
			// Then when the user connect again it can choose an already existing account
			mPlusClient.clearDefaultAccount();
			// You should call disconnect, else nothing will happens (an exception in fact)
			// disconnect the user
			mPlusClient.disconnect();
			// Kill the connectionResult
			mConnectionResult = null;
		}
	}

	/**
	 * This method is called when the user click on the revoke button
	 */
	private void revoke() {
		Log.e(TAG, "gPlusSignOutClicked");
		if (mPlusClient.isConnected()) {

			// Second case: Give your user the ability to reset its grants for your application
			// -------------------------------------------------------------------------------------------
			// If you want to delete the authorizations the user gave to you
			// you should call that method. At the next connection, the user will give you the new grants
			// (The activity G+Connection will be launched again and the user will grant your application to access to
			// circles, data as if he was connecting for the first time to your app)
			// You should always give your user the ability to do such a reset on the way your application acts in its
			// G+ account
			// It also call disconnect on mPlusClient. If you disconnect the client before the call of onAccessRevoked,
			// the listener below will never work.
			mPlusClient.revokeAccessAndDisconnect(new OnAccessRevokedListener() {
				@Override
				public void onAccessRevoked(ConnectionResult status) {
					userAccessRevoked(status);
				}
			});

		}
	}

	/**
	 * This method is called when the user has is access revoked
	 * 
	 * @param status
	 *            of the revocation
	 */
	private void userAccessRevoked(ConnectionResult status) {
		Log.e(TAG, "userAccessRevoked returns " + status);

	}

	/**
	 * Called when the button disconnect is clicked
	 */
	private void disconnect() {
		if (mPlusClient.isConnected()) {
			// Last case: Just disconnect the user, next call on connect, will connect him again smoothly
			// -------------------------------------------------------------------------------------------
			// He will have the same account and the same grants as before
			// disconnect the user
			mPlusClient.disconnect();
			// Kill the connectionResult
			mConnectionResult = null;

			// Tips: If you disconnect before clearing account, you'll have the "java.lang.IllegalStateException: Not
			// connected. Call connect() and wait for onConnected() to be called." thrown in your face.
		}
	}

	/**
	 * Build and send the moment
	 * 
	 * @param message
	 *            the message to post
	 * @param userId
	 *            the userToDisplay
	 */
	private void sendInteractivePost(String message) {
		String userId=mPlusClient.getCurrentPerson().getId();
		PlusShare.Builder psBuilder = new PlusShare.Builder(this, mPlusClient);
		// ------------------------------------------------------------------------------------------------------------
		// Build the post itself
		// ------------------------------------------------------------------------------------------------------------
		// The message of the post
		psBuilder.setText(message);
		// The content url of the post
		// R.string.content_url=https://plus.google.com/%1$s/about
		String contentUrl = getString(R.string.content_url, getString(R.string.a2ee_gplus_profile_id));
		// The ContentUrl (it's a fake one here) content_url with project page
		psBuilder.setContentUrl(Uri.parse(contentUrl));
		// ------------------------------------------------------------------------------------------------------------
		// Add the CallToAction
		// ------------------------------------------------------------------------------------------------------------
		// Adds a call-to-action button for an interactive post. To use this method, you must have passed a signed-in
		// PlusClient to the Builder.Builder(Activity, PlusClient) constructor or an IllegalStateException will be
		// thrown.
		//
		// Parameters
		// label : The call-to-action label. Choose a value from the list of list uri
		// URI :The URL to link to when the user clicks the call-to-action. This parameter is required.
		// DeepLinkId : The link used in Android device for your activity
		// add_call_label=CONNECT
		String addCallLabel = getString(R.string.add_call_label);
		// .add_call_uri=https://plus.google.com/%1$s/about
		String addCallUri = getString(R.string.add_call_uri, userId);
		Uri addCallURI = Uri.parse(addCallUri);
		// add_call_deeplink_id=user/%1$s?action=see
		String addCallDeepLinkId = getString(R.string.add_call_deeplink_id, userId);
		psBuilder.addCallToAction(addCallLabel, addCallURI, addCallDeepLinkId);
		// ------------------------------------------------------------------------------------------------------------
		// Build the post itself : The second way to do
		// If you don't set contentUrl you can do the following code. But if you had set it, the following code will do
		// nothing
		// ------------------------------------------------------------------------------------------------------------
		// The deep link id: It will be used when coming back on the device
		// Parameters
		// deepLinkId The deep-link ID to a resource to share on Google+. This parameter is required.
		// title The title of the resource. Used if there is no content URL to display. This parameter is optional.
		// description The description of a resource. Used if there is no content URL to display. This parameter is
		// optional.
		// thumbnailUri The thumbnailUri for a resource. Used if there is no content URL to display. This parameter is
		// optional.
		// content_deepl_id_url=user/%1$s?action=see
		String contentDeepLinkIdUrl = getString(R.string.content_deepl_id_url, userId);
		// content_deepl_title_url=Google Public Profile
		String contentDeepLinkTitleUrl = getString(R.string.content_deepl_title_url);
		// content_deepl_descr_url=You can see your public google profile using the Android application Google Public
		// Profile
		String contentDeepLinkDescUrl = getString(R.string.content_deepl_descr_url, userId);
		// content_deepl_tumbnail_url=https://plus.google.com/%1$s/about
		String contentDeepLinkThumbUrl = getString(R.string.content_deepl_tumbnail_url, userId);
		Uri contentDeepLinkThumbURI = Uri.parse(contentDeepLinkThumbUrl);
		psBuilder.setContentDeepLinkId(contentDeepLinkIdUrl, // content_deepl_id_url
				contentDeepLinkTitleUrl,// content_deepl_title_url
				contentDeepLinkDescUrl,// content_deepl_descr_url
				contentDeepLinkThumbURI);// my picture but why ?o?

		// And then drop the moment in your user stream
		startActivityForResult(psBuilder.getIntent(), 0);
	}

	/**
	 * Write a moment in the stream of the user
	 * It's also called an "Application Activity"
	 * This method should work, but not in fact
	 */
	void writeMomentInStream() {
		String userId=mPlusClient.getCurrentPerson().getId();
		Person owner = mPlusClient.getCurrentPerson();
		// content_deepl_id_url=user/%1$s?action=see
		String contentDeepLinkIdUrl = getString(R.string.content_deepl_id_url, userId);
		ItemScope target = new ItemScope.Builder()
				// Also, use ItemScope.setId() to create a unique application specific ID for the item you are writing.
				//It will be used as the deepLinkId 		
				.setId(contentDeepLinkIdUrl)
				// It should NOT be set as the Google+ user ID.
				.setText("Google Public Identity is used by " + owner.getDisplayName() + " which said something")
				.setDescription(
						owner.getDisplayName()
								+ " had a look at its google public profile and at his friends profile too using Google Public Profile an application of Android2ee (Taining Expertise in Android)")
				.setThumbnailUrl(owner.getImage().getUrl()).setType("http://schema.org/Comment").build();
		
		ItemScope rating = new ItemScope.Builder()
	    .setType("http://schema.org/Rating")
	    .setRatingValue("5")
	    .setBestRating("5")
	    .setWorstRating("0")
	    .build();
		ItemScope result = new ItemScope.Builder()
	    .setType("http://schema.org/Comment")
	    .setName("Excellent application ")
//	    .setUrl("https://play.google.com/store/apps/details?id=com.android2ee.project.gplus.signin.publicid&write_review=true")
	    .setDescription("It is amazingly effective")
//	    .setReviewRating(rating)
	    .build();
		
		
		// Ensure in your new PlusClient.Builder(this, this, this).setVisibleActivities(
		// "http://schemas.google.com/AddActivity", "http://schemas.google.com/ListenActivity").build()
		// You have define the activity you want to add as your moment type
		Moment moment = new Moment.Builder().setType("http://schemas.google.com/CommentActivity").setTarget(target).setResult(result)
				.build();

		if (mPlusClient.isConnected()) {
			mPlusClient.writeMoment(moment);
			Log.e(TAG, "writeMomentInStream called mPlusClient.isConnected()");
		} else {
			Log.e(TAG, "writeMomentInStream called !!!!mPlusClient.isConnected()");
		}
		Toast.makeText(this, "Moment has been sent", Toast.LENGTH_SHORT).show();
	}

	/******************************************************************************************/
	/** Implementing GooglePlayServicesClient.ConnectionCallbacks **************************************************************************/
	/******************************************************************************************/

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onConnected(android.os.Bundle)
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		Person accountOwner = mPlusClient.getCurrentPerson();
		// The connection with the user is ok, you can explore him it
		// retrieve the user
		txvPersonPresentation.setText(new PersonExplorer(mPlusClient).explore());
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks#onDisconnected()
	 */
	@Override
	public void onDisconnected() {
		// When using AccessRevoked, this method is never called
		Log.e(TAG, "onDisconnected ");
		// Last case: Just disconnect the user, next call on connect, will connect him again smoothly
		// -------------------------------------------------------------------------------------------
		// He will have the same account and the same grants as before
		// disconnect the user
		mPlusClient.disconnect();
		// Kill the connectionResult
		mConnectionResult = null;
	}

	/******************************************************************************************/
	/** Implementing GooglePlayServicesClient.OnConnectionFailedListener **************************************************************************/
	/******************************************************************************************/
	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener#onConnectionFailed(com.google
	 * .android.gms.common.ConnectionResult)
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG,
				"onConnectionFailed " + result + " hasResolution: " + result.hashCode() + " errorCode: "
						+ result.getErrorCode());
		signInButton.setEnabled(true);
		// You can just set the mConnectionResult for your user to be able to connect using the signInButton
		// @see gPlusSignInClicked()
		if (!connectionAskedByUser) {
			// This case is when you want your user to click the g+connection button to connect
			// and not hide it by an automatic connection
			mConnectionResult = result;
		} else {
			// Or you want to automagicly display him the solution to its problem either by showing the native SignIn
			// Activity or the createAccount one. It's handle by the google team throught the GoogleService Api

			try {
				// if auto-connection failed then ensure to display the SignIn Activity to the user for him to be able
				// to sign in if it's a Sign_In_Required else just try to find a solution
				if (result.hasResolution()) {
					result.startResolutionForResult(this, requestCodeResolverError);
				}
			} catch (SendIntentException e) {
				Log.e(TAG, "onConnectionFailed " + result, e);
			} finally {
				connectionAskedByUser = false;
			}
		}
	}
}
