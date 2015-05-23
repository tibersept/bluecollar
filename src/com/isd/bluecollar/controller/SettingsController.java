/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller;

import java.util.Map;

import com.isd.bluecollar.data.store.UserSettings;

/**
 * User settings controller.
 * @author doan
 */
public class SettingsController {

	/**
	 * Retrieves a user setting.
	 * @param aUser the user
	 * @param aProperty the property
	 * @return the property value
	 */
	public String getSetting( String aUser, String aProperty ) {
		UserSettings us = new UserSettings();
		return us.getUserSetting(aUser, aProperty);
	}
	
	/**
	 * Sets a user property to the specified value.
	 * @param aUser the user
	 * @param aProperty the property
	 * @param aValue the property value
	 */
	public void setSetting( String aUser, String aProperty, String aValue ) {
		UserSettings us = new UserSettings();
		us.setUserSetting(aUser, aProperty, aValue);
	}
	
	/**
	 * Returns a collection of all settings.
	 * @param aUser the user
	 * @return the collection of user settings
	 */
	public Map<String,Object> getAllSettings( String aUser ) {
		UserSettings wtd = new UserSettings();		
		return wtd.getUserSettingsAsMap(aUser);
	}
}
