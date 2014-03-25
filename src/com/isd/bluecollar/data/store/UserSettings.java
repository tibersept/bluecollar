/**
 * 
 */
package com.isd.bluecollar.data.store;

import java.util.Collections;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

/**
 * User settings entity wrapper.
 * @author doan
 */
public class UserSettings {

	/** User settings entity */
	private static final String USER_SETTINGS = "UserSettings";
	
	/** The user entity wrapper */
	private User user;
	/** The datastore service */
	private DatastoreService service;
	
	/**
	 * Creates a new instance of the user settings entity. 
	 */
	public UserSettings() {
		service = DatastoreServiceFactory.getDatastoreService();
		user = new User();
	}
	
	/**
	 * Retrieves the user settings or creates a new user settings entity.
	 * @param aUser the user key
	 * @return the user settings entity
	 */
	public Entity getUserSettings( Key aUser ) {
		Query q = new Query(USER_SETTINGS,aUser).setAncestor(aUser);
		Entity settings = service.prepare(q).asSingleEntity();
		if( settings==null ) {
			settings = createNewUserSettings(aUser);
		}
		return settings;
	}
	
	/**
	 * Returns the user setting.
	 * @param aUser the user
	 * @param aSetting the setting name
	 * @return the setting value
	 */
	public String getUserSetting( String aUser, String aSetting ) {
		Key key = user.getKey(aUser);
		if( key!=null ) {
			Entity settings = getUserSettings(key);
			if( settings!=null ) {
				Object val = settings.getProperty(aSetting);
				if( val!=null ) {
					return val.toString();
				}
			}
		}
		return "";
	}
	
	/**
	 * Sets a user setting to the passed value.
	 * @param aUser the user
	 * @param aSetting the setting
	 * @param aValue the value
	 */
	public void setUserSetting( String aUser, String aSetting, String aValue ) {
		Key key = user.getKey(aUser);
		if( key!=null ) {
			Entity settings = getUserSettings(key);
			if( settings!=null ) {
				settings.setProperty(aSetting, aValue);
				service.put(settings);
			}
		}
	}
	
	/**
	 * Retrieves all user settings.
	 * @param aUser the user key
	 * @return the user settings
	 */
	public Map<String,Object> getUserSettingsAsMap( String aUser ) {
		Key key = user.getKey(aUser);
		if( key!=null ) {
			Entity settings = getUserSettings(key);
			if( settings!=null ) {
				return settings.getProperties();
			}
		}
		return Collections.emptyMap();
	}
	
	/**
	 * Creates a new user settings entity.
	 * @param aUser the user key
	 * @return the entity
	 */
	private Entity createNewUserSettings( Key aUser ) {
		Entity userSettings = new Entity(USER_SETTINGS, aUser);
		service.put(userSettings);
		return userSettings;
	}
	
}