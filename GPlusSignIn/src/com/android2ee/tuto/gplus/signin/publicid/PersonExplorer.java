/**<ul>
 * <li>GPlusSignIn</li>
 * <li>com.android2ee.tuto.gplus.signin.publicid</li>
 * <li>10 juin 2013</li>
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

import java.util.List;

import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnMomentsLoadedListener;
import com.google.android.gms.plus.PlusClient.OnPeopleLoadedListener;
import com.google.android.gms.plus.model.moments.Moment;
import com.google.android.gms.plus.model.moments.MomentBuffer;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.Person.AgeRange;
import com.google.android.gms.plus.model.people.Person.Cover;
import com.google.android.gms.plus.model.people.Person.Cover.CoverPhoto;
import com.google.android.gms.plus.model.people.Person.Urls;
import com.google.android.gms.plus.model.people.PersonBuffer;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals
 *        This class aims to:
 *        <ul>
 *        <li></li>
 *        </ul>
 */
public class PersonExplorer {

	/**
	 * The owner which is logged in the application
	 */
	private Person gPlusGuy;
	/**
	 * The PlusClient
	 */
	private PlusClient mPlusClient;
	/**
	 * A String representation of the displayed Person
	 */
	private StringBuilder gPlusGuyRepresentation;
	/**
	 * The next Token to use to load the next bunch of people
	 */
	String nextPersonPageToken = "100";
	/**
	 * The next Token to use to load the next bunch of moment
	 */
	String nextMomentPageToken = null;
	/******************************************************************************************/
	/** Constructors **************************************************************************/
	/******************************************************************************************/

	/**
	 * @param gPlusGuy
	 */
	public PersonExplorer(PlusClient plusClient) {
		this.gPlusGuy = plusClient.getCurrentPerson();
		this.mPlusClient=plusClient;
		gPlusGuyRepresentation=new StringBuilder();
	}

	/******************************************************************************************/
	/** Public methods **************************************************************************/
	/******************************************************************************************/
	/**
	 * Explore a Person information:
	 * Load all the fields with the person information
	 */
	public String explore() {
		displayPersonInformation();
		displayPersonMoment();
		displayPersonSocialGraph();
		return gPlusGuyRepresentation.toString();
	}
	/******************************************************************************************/
	/** Read Moments **************************************************************************/
	/******************************************************************************************/
	/**
	 * Load and display the Person's moments
	 */
	private void displayPersonMoment() {
		// You can use this method to load People and set the order by
		// but most important you can ask for bunch of data using maxResult and pageToken
		// mPlusClient.loadPeople(listener, collection, orderBy, maxResults, pageToken)
		// The other simple way is that one
		// mPlusClient.loadMoments(new OnMomentsLoadedListener() {
		//
		// @Override
		// public void onMomentsLoaded(ConnectionResult status, MomentBuffer momentBuffer, String nextPageToken,
		// String updated) {
		// displayLoadedMoments(status, momentBuffer, nextPageToken, updated);
		//
		// }
		// }, maxResult, pageToken , targetUrl , type , mPlusClient.getCurrentPerson().getId());

		mPlusClient.loadMoments(new OnMomentsLoadedListener() {
			public void onMomentsLoaded(ConnectionResult status, MomentBuffer momentBuffer, String nextPageToken,
					String updated) {
				displayLoadedMoments(status, momentBuffer, nextPageToken, updated);

			}
		});

	}

	
	/**
	 * To load more moments, call this method
	 */
	public void loadMoreMoments() {
		mPlusClient.loadMoments(new OnMomentsLoadedListener() {

			@Override
			public void onMomentsLoaded(ConnectionResult status, MomentBuffer momentBuffer, String nextPageToken,
					String updated) {
				displayLoadedMoments(status, momentBuffer, nextPageToken, updated);

			}
		}, 100, nextMomentPageToken, null, null, mPlusClient.getCurrentPerson().getId());

	}

	/**
	 * Display loaded moment
	 * 
	 * @param status
	 *            The resulting connection status of the loadMoments(OnMomentsLoadedListener) request.
	 * @param momentBuffer
	 *            The requested moments. The listener must close this object when finished.
	 * @param nextPageToken
	 *            The continuation token, which is used to page through large result sets. Provide this
	 * @param value
	 *            in a subsequent request to return the next page of results.
	 * @param updated
	 *            The time at which this collection of moments was last updated. Formatted as an RFC 3339
	 *            timestamp
	 */
	private void displayLoadedMoments(ConnectionResult status, MomentBuffer momentBuffer, String nextPageToken,
			String updated) {		
		if (momentBuffer.getCount() > 0) {
			nextMomentPageToken = nextPageToken;
			if (status.isSuccess()) {
				try {
					for (Moment moment : momentBuffer) {
						setTextViewText("New moment found : "+ moment.getId()+", "+moment.getResult()+", "+
								moment.getStartDate()+", "+ moment.getTarget()+", "+ moment.getType());
						
					}
				} finally {
					momentBuffer.close();
				}
			}
		}
	}

	/******************************************************************************************/
	/** Read Social Graph of the Owner !Not of the current displayed person **************************************************************************/
	/******************************************************************************************/
	/**
	 * Load and display the ownerSocial Graph
	 * You can not display the SocialGraph of a friend of you
	 */
	private void displayPersonSocialGraph() {
		// You can use this method to load People and set the order by
		// but most important you can ask for bunch of data using maxResult and pageToken
		// mPlusClient.loadPeople(listener, collection, orderBy, maxResults, pageToken)
		// The other simple way is that one
		mPlusClient.loadVisiblePeople(new OnPeopleLoadedListener() {
			@Override
			public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
				displayLoadedPeople(status, personBuffer, nextPageToken);
			}
		}, null);
	}

	
	/**
	 * To load more people, call this method
	 */
	public void loadMorePeople() {
		mPlusClient.loadVisiblePeople(new OnPeopleLoadedListener() {
			@Override
			public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
				displayLoadedPeople(status, personBuffer, nextPageToken);
			}
		}, PlusClient.OrderBy.BEST,  nextPersonPageToken);
//		mPlusClient.loadVisiblePeople(new OnPeopleLoadedListener() {
//			@Override
//			public void onPeopleLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
//				displayLoadedPeople(status, personBuffer, nextPageToken);
//			}
//		}, PlusClient.OrderBy.BEST, 100, nextPersonPageToken);
	}

	/**
	 * Display loaded people
	 * 
	 * @param status
	 *            The resulting connection status of the loadPeople(OnPeopleLoadedListener, int) request.
	 * @param personBuffer
	 *            The requested collection of people. The listener must close this object when finished.
	 * @param nextPageToken
	 *            The continuation token, which is used to page through large result sets. Provide this value in a
	 *            subsequent request to return the next page of results.
	 */
	private void displayLoadedPeople(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
			//
			this.nextPersonPageToken = nextPageToken;
			if (status.isSuccess()) {
				try {
					for (Person people : personBuffer) {						
						if (people.getImage() != null) {
							setTextViewText(people.getImage().getUrl()+","+ people.getId()+","+ 
									people.getDisplayName());
						} else {
							setTextViewText(","+ people.getId()+","+ 
									people.getDisplayName());
						}
					}
				} finally {
					personBuffer.close();
				}
			
		}

	}

	/******************************************************************************************/
	/** Read the information of the people displayed *************************************************************************/
	/******************************************************************************************/
	/**
	 * 
	 */
	private void displayPersonInformation() {
		// Fill the top of the screen
		buildUserMajorInformation();
		// Fill the bragging
		buildBragging();
		// Fill personal information
		buildPersonalInformation();
		// Fill the bio
		buildBiography();
	}

	/**
	 * Retrieve and display the Personal information
	 * 
	 * @param gPlusGuy
	 */
	private void buildPersonalInformation() {
		// name
		Person.Name name = gPlusGuy.getName();
		if (name != null) {
			if (name.hasFamilyName()) {
				setTextViewText(name.getFamilyName());
			}
			if (name.hasHonorificPrefix()) {
				setTextViewText( name.getHonorificPrefix());
			}
			if (name.hasGivenName()) {
				setTextViewText( name.getGivenName());
			}
			if (name.hasGivenName()) {
				setTextViewText( name.getGivenName());
			}
			if (name.hasHonorificSuffix()) {
				setTextViewText( name.getHonorificSuffix());
			}
			if (name.hasMiddleName()) {
				setTextViewText( name.getMiddleName());
			}
		}
		// NickName line
		setTextViewText( gPlusGuy.getNickname());

		// G+ Id
		setTextViewText( null != gPlusGuy.getId() ? "G+ ID : " + gPlusGuy.getId() : null);
		// tagline
		setTextViewText( null != gPlusGuy.getTagline() ? "Tagline : " + gPlusGuy.getTagline() : null);

		// age range
		AgeRange mAgeRange = gPlusGuy.getAgeRange();
		if (mAgeRange != null) {
			if (mAgeRange.hasMax()) {
				setTextViewText( "MaxAge : " + Integer.toString(mAgeRange.getMax()));
			} else {
				setTextViewText( null);
			}
			if (mAgeRange.hasMin()) {
				setTextViewText( "MinAge : " + Integer.toString(mAgeRange.getMin()));
			} else {
				setTextViewText( null);
			}
		} else {
			setTextViewText( null);
			setTextViewText( null);
		}
		// BirthDay
		setTextViewText( gPlusGuy.getBirthday());

		// Circle count
		// int circleByCount = gPlusGuy.getCircledByCount();
		// strB.append("circleByCount :" + circleByCount + "\r\n");
		// if(gPlusGuy.hasCircledByCount()) {
		// setTextViewText(fragment.txvCMaxAge, Integer.toString(gPlusGuy.getCircledByCount()));
		// }else {
		// setTextViewText(fragment.txvMaxAge, null);
		// }

		// Builder
		StringBuilder strB = new StringBuilder();
		// Emails
//		List<Person.Emails> emails = gPlusGuy.getEmails();
//		if (null != emails && emails.size() != 0) {
//			strB.append("Emails\r\n");
//			for (Person.Emails email : emails) {
//				strB.append("email :" + email.getType() + "(" + email.getValue() + ")\r\n");
//			}
//			setTextViewText( strB.length() != 0 ? strB.toString() : null);
//			strB.setLength(0);
//		} else {
			setEmptyTextViewTitle( "No Public Emails Adresses: Removed from the GPS API (fuckers)");
//		}

		// Organizations

		// A lot of data associated to the organization
		List<Person.Organizations> organizations = gPlusGuy.getOrganizations();
		if (null != organizations && organizations.size() != 0) {
			strB.append("Organizations\r\n");
			for (Person.Organizations org : organizations) {
				strB.append(org.getName() + "- " + org.getStartDate() + " - " + org.getStartDate() + "\r\n");
			}
			setTextViewText( strB.length() != 0 ? strB.toString() : null);
			strB.setLength(0);
		} else {
			setEmptyTextViewTitle( "No Public Organizations");
		}

		// Places lived

		List<Person.PlacesLived> placesLived = gPlusGuy.getPlacesLived();
		if (null != placesLived && placesLived.size() != 0) {
			strB.append("Places Lived\r\n");
			for (Person.PlacesLived place : placesLived) {
				strB.append(place.getValue() + "\r\n");
			}
			setTextViewText(strB.length() != 0 ? strB.toString() : null);
			strB.setLength(0);
		} else {
			setEmptyTextViewTitle( "No Public Known Places Lived");
		}

		// URL of the person profile
		strB.append("Links\r\n");
		String url = gPlusGuy.getUrl();
		if (url != null) {
			strB.append("" + url + "\r\n");
		}
		List<Urls> urls = gPlusGuy.getUrls();
		if (urls != null) {
			for (Urls currentUrl : urls) {
				strB.append("" + currentUrl.getType() + " " + currentUrl.getValue()
						+ "\r\n");
			}
		}
		setTextViewText(strB.length() != 0 ? strB.toString() : null);
		strB.setLength(0);

	}

	/**
	 * Set the text to the text view
	 * If the text is null, the text view is backgournded red else green
	 * 
	 * @param txv
	 * @param str
	 */
	private void setTextViewText(String str) {
		gPlusGuyRepresentation.append(str);
		gPlusGuyRepresentation.append("\r\n");
	}

	/**
	 * Set the text to the text view
	 * the text view is backgournded red
	 * 
	 * @param txv
	 * @param str
	 */
	private void setEmptyTextViewTitle(String str) {
		gPlusGuyRepresentation.append("*o* ");
		gPlusGuyRepresentation.append(str);
		gPlusGuyRepresentation.append(" *o*");
		gPlusGuyRepresentation.append("\r\n");
	}

	/**
	 * Build the biography of the this.person
	 */
	private void buildBiography() {
		String biography = gPlusGuy.getAboutMe();
		if (biography != null) {
			setTextViewText("bio : " + biography + "...<br/><br/>");
		} else {
			setTextViewText("bio : absente...<br/><br/>");
		}
	}

	/**
	 * Build the bragging of the this.person
	 */
	private void buildBragging() {
		// BraggingRights
		String braggingRights = gPlusGuy.getBraggingRights();
		if (braggingRights != null) {
			setTextViewText(braggingRights);
		} else {
			setTextViewText("No information available");
		}
	}

	/**
	 * Build the bragging of the this.person
	 */
	private void buildUserMajorInformation() {
		// Cover
		Cover mPhotoCover = gPlusGuy.getCover();
		CoverPhoto coverPhoto = mPhotoCover.getCoverPhoto();
		setTextViewText(coverPhoto.getUrl());
		// Name
		setTextViewText(gPlusGuy.getDisplayName());
		// RelationShip and Gender
		setTextViewText(""+gPlusGuy.getRelationshipStatus());
		setTextViewText(""+gPlusGuy.getGender());
		// Person's picture
		Person.Image mImage = gPlusGuy.getImage();
		setTextViewText(mImage.getUrl());
		
		// Location
		setTextViewText(gPlusGuy.getCurrentLocation());
		// Language
		setTextViewText(gPlusGuy.getLanguage());
	}
}
