/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.store;

import com.google.appengine.api.datastore.DatastoreFailureException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * User entity datastore proxy.
 * @author doan
 */
public class UserDP {

	/** User entity */
	private static final String USER = "User";
	/** Property name */
	private static final String PROPERTY_NAME = "name";
	/** Property current project */
	private static final String PROPERTY_CURRENT_PROJECT = "currentProject";
	/** Property task begin */
	private static final String PROPERTY_TASK_BEGIN = "taskBegin";
	
	/** The datastore service */
	private DatastoreService service;
	
	/**
	 * Creates a new instance of the user entity wrapper.
	 */
	public UserDP() {
		service = DatastoreServiceFactory.getDatastoreService();
	}
	
	/**
	 * Returns the user entity matching the user name.
	 * @param aUser the user name
	 * @return the user entity
	 */
	public Entity getUser( String aUser ) {
		Key key = KeyFactory.createKey(USER, aUser);
		try {
			Entity user = service.get(key);
			return user;
		} catch (EntityNotFoundException e) {
			return createNewUser(aUser);
		} catch (DatastoreFailureException e) {
			return null;
		}
	}
	
	/**
	 * Returns the user key. The user is created if he doesn't exist.
	 * @param aUser the user nickname
	 * @return the user root
	 */
	public Key getKey( String aUser ) {
		Entity user = getUser(aUser);
		if(user!=null) {
			return user.getKey();
		}
		return null;
	}
	
	/**
	 * Returns the current project of the user. This method will return an empty
	 * string if there is no current project.
	 * @param aUser the user
	 * @return the current project of this user
	 */
	public String getCurrentProject( String aUser ) {
		Entity user = getUser(aUser);
		if( user!=null ) {
			return String.valueOf(user.getProperty(PROPERTY_CURRENT_PROJECT));
		}
		return "";
	}
	
	/**
	 * Sets the current project for this user.
	 * @param aUser the user 
	 * @param aProject the project
	 */
	public void setCurrentProject( String aUser, String aProject ) {
		Entity user = getUser(aUser);
		if( user!=null ) {
			user.setProperty(PROPERTY_CURRENT_PROJECT, aProject);
			service.put(user);
		}
	}
	
	/**
	 * Returns the current task begin timestamp for this user. This method will
	 * return 0 if there is no current task.
	 * @param aUser the user
	 * @return the current task begin timestamp
	 */
	public long getTaskBegin( String aUser ) {
		Entity user = getUser(aUser);
		if( user!=null ) {
			Object val = user.getProperty(PROPERTY_TASK_BEGIN);
			if( val!=null ) {
				return ((Long)val).longValue();
			}
		}
		return 0;
	}
	
	/**
	 * Sets the begin of the current user task associated with the current project.
	 * @param aUser the user
	 * @param aBegin the begin timestamp
	 */
	public void setTaskBegin( String aUser, long aBegin ) {
		Entity user = getUser(aUser);
		if( user!=null ) {
			user.setProperty(PROPERTY_TASK_BEGIN,aBegin);
			service.put(user);
		}
	}
	
	/**
	 * Creates a new user.
	 * @param aUser the user
	 * @return the entity of the newly created user
	 */
	private Entity createNewUser(String aUser) {
		Entity user = new Entity(USER, aUser);
		user.setProperty(PROPERTY_NAME, aUser);
		user.setProperty(PROPERTY_CURRENT_PROJECT, null);
		user.setProperty(PROPERTY_TASK_BEGIN, null);
		service.put(user);
		return user;
	}
}
