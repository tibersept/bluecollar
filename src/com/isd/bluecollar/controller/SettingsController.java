/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller;

import java.util.Map;

import com.isd.bluecollar.data.store.UserSettingsDP;

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
		UserSettingsDP us = new UserSettingsDP();
		return us.getUserSetting(aUser, aProperty);
	}
	
	/**
	 * Sets a user property to the specified value.
	 * @param aUser the user
	 * @param aProperty the property
	 * @param aValue the property value
	 */
	public void setSetting( String aUser, String aProperty, String aValue ) {
		UserSettingsDP us = new UserSettingsDP();
		us.setUserSetting(aUser, aProperty, aValue);
	}
	
	/**
	 * Returns a collection of all settings.
	 * @param aUser the user
	 * @return the collection of user settings
	 */
	public Map<String,Object> getAllSettings( String aUser ) {
		UserSettingsDP wtd = new UserSettingsDP();		
		return wtd.getUserSettingsAsMap(aUser);
	}
}
