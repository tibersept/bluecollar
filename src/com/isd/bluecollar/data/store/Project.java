/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * Project entity wrapper.
 * @author doan
 */
public class Project {

	/** Project entity */
	private static final String PROJECT = "Project";
	/** Property project name */
	private static final String PROPERTY_NAME = "name";
	/** Property project description */
	private static final String PROPERTY_DESCRIPTION = "description";
	
	
	/** The user entity wrapper */
	private User user;
	/** The datastore service */
	private DatastoreService service;
	
	/**
	 * Creates a new project entity wrapper instance.
	 */
	public Project() {
		service = DatastoreServiceFactory.getDatastoreService();
		user = new User();
	}
	
	/**
	 * Returns a project entity matching the project name.
	 * @param aUserKey the user key
	 * @param aProject the project name
	 * @return the project matching the project name
	 */
	public Entity getProject( String aUser, String aProject ) {
		Key key = user.getKey(aUser);
		if( key!=null ) {
			return doGetProject(key, aProject);
		}
		return null;
	}
	
	/**
	 * Returns the project key matching the username and the project name.
	 * @param aUser the user
	 * @param aProject the project
	 * @return
	 */
	public Key getKey( String aUser, String aProject ) {
		Entity project = getProject(aUser,aProject);
		if(project!=null) {
			return project.getKey();
		}
		return null;
	}
	
	/**
	 * Adds a new project to the list of projects of this user.
	 * @param aUser the username
	 * @param aName the project name
	 * @param aDescription the project description
	 */
	public void addProject( String aUser, String aName, String aDescription ) {
		Key key = user.getKey(aUser);
		if( key!=null ) {
			Entity project = doGetProject(key,aName);
			if( project!=null ) {
				project.setProperty(PROPERTY_DESCRIPTION, aDescription);
				service.put(project);
			} else {
				createNewProject(aName, aDescription, key);
			}
		}
	}
	
	/**
	 * Returns a list of all projects assigned to user.
	 * @param aUser the user
	 * @param anAlphaSorted flag indicates whether list should be sorted
	 * @return the list of all projects
	 */
	public List<String> getProjects( String aUser, boolean anAlphaSorted ) {
		Key key = user.getKey(aUser);
		if( key!=null ) {
			List<String> list = new ArrayList<String>();
			List<Entity> projects = getAllProjects(key);
			for( Entity project : projects ) {
				String projectName = (String) project.getProperty(PROPERTY_NAME);
				list.add(projectName);
			}
			if( anAlphaSorted ) {
				Collections.sort(list);
			}
			return list;
		}
		return Collections.emptyList();
	}
	
	/**
	 * Returns a project entity matching the project name.
	 * @param aUserKey the user key
	 * @param aName the project name
	 * @return the project matching the project name
	 */
	private Entity doGetProject( Key aUserKey, String aName ) {
		Filter filter = new FilterPredicate(PROPERTY_NAME, FilterOperator.EQUAL, aName);
		Query q = new Query(PROJECT,aUserKey).setAncestor(aUserKey).setFilter(filter);
		return service.prepare(q).asSingleEntity();
	}
	
	/**
	 * Returns all projects which belong to the given user.
	 * @param aUserKey the user key
	 * @return all projects of the user
	 */
	private List<Entity> getAllProjects( Key aUserKey ) {
		Query q = new Query(PROJECT,aUserKey).setAncestor(aUserKey);
		return service.prepare(q).asList(FetchOptions.Builder.withDefaults());
	}
	
	/**
	 * Creates a new project attached to the user.
	 * @param aName the project name
	 * @param aDescription the project description
	 * @param aUserKey the user key
	 * @return the newly created project entity
	 */
	private Entity createNewProject(String aName, String aDescription, Key aUserKey) {
		Entity project = new Entity(PROJECT, aName, aUserKey);
		project.setProperty(PROPERTY_NAME, aName);
		project.setProperty(PROPERTY_DESCRIPTION, aDescription);
		service.put(project);
		return project;
	}
	
}