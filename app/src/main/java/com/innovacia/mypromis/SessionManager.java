package com.innovacia.mypromis;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;
	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "PromisPref";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	
	// User name (make variable public to access from outside)
	public static final String KEY_ID = "mem_id";
	
	// Email address (make variable public to access from outside)
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PRO_ID = "proID";
	public static final String KEY_PRO_NAME = "proName";

	public static final String KEY_ALERT_STATUS = "alertStatus";


	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String id, String username){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		// Storing name in pref
		editor.putString(KEY_ID, id);
		// Storing email in pref
		editor.putString(KEY_USERNAME, username);
		// commit changes
		editor.commit();
	}

	public void saveProject(String proID, String proName){
		editor.putString(KEY_PRO_ID, proID);
		editor.putString(KEY_PRO_NAME, proName);
		// commit changes
		editor.commit();
	}


	public void saveAlert(String alertStatus){
		editor.putString(KEY_ALERT_STATUS, alertStatus);
		editor.commit();
	}


	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		user.put(KEY_ID, pref.getString(KEY_ID, null));
		user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
		user.put(KEY_PRO_NAME, pref.getString(KEY_PRO_NAME, null));
		user.put(KEY_PRO_ID, pref.getString(KEY_PRO_ID, null));
		user.put(KEY_ALERT_STATUS, pref.getString(KEY_ALERT_STATUS, "Not Activated"));

		// return user
		return user;
	}
	
	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, LoginActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
