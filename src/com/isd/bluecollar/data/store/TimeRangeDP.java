/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.isd.bluecollar.data.internal.Range;

/**
 * Time range entity datastore proxy.
 * @author doan
 */
public class TimeRangeDP {
		
	/** Time range entity */
	private static final String TIME_RANGE = "TimeRange";
	/** Property begin time of a range */
	private static final String PROPERTY_BEGIN = "begin";
	/** Property end time of a range */
	private static final String PROPERTY_END = "end";
	/** Property state of a state */
	private static final String PROPERTY_STATE = "state";

	/** Initial task state */
	private static final String STATE_INITIAL = "initial";
	/** Finished task state */
	private static final String STATE_FINISHED = "finished";
	/** Open task state */
	private static final String STATE_OPEN = "open";

	/** The project */
	private ProjectDP project;
	/** The datastore service */
	private DatastoreService service;
	
	/**
	 * Creates an instance of the time range wrapper.
	 * @param aProject a required project entity wrapper
	 */
	public TimeRangeDP( ProjectDP aProject ) {
		service = DatastoreServiceFactory.getDatastoreService();
		project = aProject;
	}

	
	/**
	 * Retrieves the time ranges in a given time span.
	 * @param aUser the user
	 * @param aProject the project
	 * @param aBegin the begin of the time span
	 * @param anEnd the end of the time span
	 * @return the list of time ranges
	 */
	public List<Range<Long>> getRanges( String aUser, String aProject, Date aBegin, Date anEnd ) {
		List<Entity> entityList = getRangeEntities(aUser, aProject, aBegin, anEnd);
		List<Range<Long>> list = getRangeListFromEntities(entityList);
		return list;
	}
	
	/**
	 * Opens a time range. If there is already an open time range for the given project
	 * then this operation does nothing.
	 * @param aUser the user 
	 * @param aProject the project
	 * @param aBegin the begin date in milliseconds since the epoch
	 */
	public void openRange( String aUser, String aProject, long aBegin ) {
		Key key = project.getKey(aUser, aProject);
		if (key!=null) {
			if( !hasOpenRange(key) ) {
				Entity timeRange = createNewTimeRange(key);
				timeRange.setProperty(PROPERTY_BEGIN, aBegin);
				timeRange.setProperty(PROPERTY_STATE, STATE_OPEN);
				service.put(timeRange);
			}
		}
	}

	/**
	 * Closes a time range. If this operation does not have an open time range for the given
	 * project then this operation does nothing.
	 * @param aUser the user
	 * @param aProject  the project
	 * @param anEnd the end date in milliseconds since the epoch
	 */
	public void closeRange( String aUser, String aProject, long anEnd ) {
		Key key = project.getKey(aUser, aProject);
		if (key!=null) {
			if( hasOpenRange(key) ) {
				Entity timeRange = getOpenRange(key);
				timeRange.setProperty(PROPERTY_END, anEnd);
				timeRange.setProperty(PROPERTY_STATE, STATE_FINISHED);
				service.put(timeRange);
			}
		}
	}
	
	/**
	 * Retrieves a list of all time ranges which have started in the time range constraint specified
	 * by the begin and end times.  
	 * @param aUser the user
	 * @param aProject the project
	 * @param aBegin the begin time point
	 * @param anEnd the end time point
	 * @return all time range entities that belong to this project and user in that range
	 */
	private List<Entity> getRangeEntities( String aUser, String aProject, Date aBegin, Date anEnd ) {
		Key projectKey = project.getKey(aUser, aProject);
		if( projectKey!=null ) {
			long begin = aBegin.getTime();
			long end = anEnd.getTime();
			if( begin < end ) {
				return getTimeRangesInRange(projectKey, begin, end);				
			}
		}
		return Collections.emptyList();
	}
	
	

	/**
	 * Converts the entity list into a range list.
	 * @param entityList the entity list
	 * @return the range list
	 */
	private List<Range<Long>> getRangeListFromEntities(List<Entity> entityList) {
		List<Range<Long>> list = new ArrayList<Range<Long>>(entityList.size());
		for( Entity entityRange : entityList ) {
			long begin = (long) entityRange.getProperty(PROPERTY_BEGIN);
			long end = (long) entityRange.getProperty(PROPERTY_END);
			list.add(new Range<Long>(begin, end));
		}
		return list;
	}
	
	/**
	 * Creates a new project attached to the workday with start and end times.
	 * @param aName the project name
	 * @param aProjectKey the workday key
	 * @return the newly created workday project
	 */
	private Entity createNewTimeRange(Key aProjectKey) {
		Entity project = new Entity(TIME_RANGE, System.currentTimeMillis(), aProjectKey);
		project.setProperty(PROPERTY_STATE, STATE_INITIAL);
		project.setProperty(PROPERTY_BEGIN, 0L);
		project.setProperty(PROPERTY_END, 0L);
		service.put(project);
		return project;
	}
	
	/**
	 * Retrieves an open range from the project identified by its key.
	 * @param aProject the project
	 * @return the open time range entity
	 */
	private Entity getOpenRange(Key aProject) {
		Filter openFilter = new FilterPredicate(PROPERTY_STATE, FilterOperator.EQUAL, STATE_OPEN);
		Filter initialFilter = new FilterPredicate(PROPERTY_STATE, FilterOperator.EQUAL, STATE_INITIAL);
		Filter compositeFilter = CompositeFilterOperator.or(openFilter,initialFilter);
		Query q = new Query(TIME_RANGE,aProject).setAncestor(aProject).setFilter(compositeFilter);
		return service.prepare(q).asSingleEntity();
	}
	
	/**
	 * Checks if the project identified by the key contains any open time ranges.
	 * @param aProject the project
	 * @return <code>true</code> if project does have an open time range.
	 */
	private boolean hasOpenRange(Key aProject) {
		Filter openFilter = new FilterPredicate(PROPERTY_STATE, FilterOperator.EQUAL, STATE_OPEN);
		Filter initialFilter = new FilterPredicate(PROPERTY_STATE, FilterOperator.EQUAL, STATE_INITIAL);
		Filter compositeFilter = CompositeFilterOperator.or(openFilter,initialFilter);
		Query q = new Query(TIME_RANGE,aProject).setAncestor(aProject).setFilter(compositeFilter);
		return service.prepare(q).countEntities(FetchOptions.Builder.withDefaults())>0;
	}

	/**
	 * Returns a list of time range entities which fall into the range constrained by the
	 * begin and end timestamp.
	 * @param aProKey the process key
	 * @param aBegin the begin time
	 * @param anEnd the end time
	 * @return the list of time range entities
	 */
	private List<Entity> getTimeRangesInRange(Key aProKey, long aBegin, long anEnd) {
		Filter beginFilter = new FilterPredicate(PROPERTY_BEGIN, FilterOperator.GREATER_THAN, aBegin);
		Filter endFilter = new FilterPredicate(PROPERTY_BEGIN, FilterOperator.LESS_THAN, anEnd);
		Filter compositeFilter = CompositeFilterOperator.and(beginFilter, endFilter);
		Query q = new Query(TIME_RANGE,aProKey).setAncestor(aProKey).setFilter(compositeFilter);
		return service.prepare(q).asList(FetchOptions.Builder.withDefaults());
	}
	
}
