/**
 * 
 */
package com.isd.bluecollar.data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.isd.bluecollar.datatype.Range;

/**
 * This is the time data manager. 
 * @author doan
 */
public class WorkTimeData {

	/** The datastore service */
	private DatastoreService service;
	
	/**
	 * Creates a new instance of the work time data.
	 */
	public WorkTimeData() {
		service = DatastoreServiceFactory.getDatastoreService();
	}
	
	
	/**
	 * Sets the start of a workday for a given project.
	 * @param aUser the username
	 * @param aProject the project name
	 * @param aTimestamp the timestamp
	 */
	public void setDayStart( String aUser, String aProject, Date aTimestamp ) {
		updateDay(aUser,aProject,aTimestamp,true);
	}
	
	/**
	 * Sets the end of a workday for a given project.
	 * @param aUser the username
	 * @param aProject the project name
	 * @param aTimestamp the timestamp
	 */
	public void setDayEnd( String aUser, String aProject, Date aTimestamp ) {
		updateDay(aUser,aProject,aTimestamp,false);
	}
	
	/**
	 * Adds a new project to the list of projects of this user.
	 * @param aUser the username
	 * @param aName the project name
	 * @param aDescription the project description
	 */
	public void addProject( String aUser, String aName, String aDescription ) {
		Key key = getUserKey(aUser);
		if( key!=null ) {
			Entity project = getProject(key,aName);
			if( project!=null ) {
				project.setProperty("projectDescription", aDescription);
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
	public List<String> getProjectList( String aUser, boolean anAlphaSorted ) {
		Key key = getUserKey(aUser);
		if( key!=null ) {
			List<String> list = new ArrayList<String>();
			List<Entity> projects = getAllProjects(key);
			for( Entity project : projects ) {
				String projectName = (String) project.getProperty("projectName");
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
	 * Updates one of the timestamp on a workday for a given project. 
	 * @param aUser the username
	 * @param aProject the project name
	 * @param aDate the timestamp
	 * @param aStart flag indicating whether this is check-in or check-out
	 */
	public void updateDay( String aUser, String aProject, Date aDate, boolean aStart ) {
		Key key = getUserKey(aUser);
		if (key!=null) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTime(aDate);
			Entity workproject = getWorkdayProject(key,aProject,cal,aStart);
			if (workproject!=null) {
				if( aStart ) {
					workproject.setProperty("start", cal.getTimeInMillis());
					workproject.setProperty("state", "open");
				} else {
					workproject.setProperty("end", cal.getTimeInMillis());
					workproject.setProperty("state", "finished");
				}
				service.put(workproject);
			}
		}
	}
	
	/**
	 * Returns the time range for the given day contained in the passed date. 
	 * @param aUser the username
	 * @param aDate the timestamp indicating the day
	 * @return the date range
	 */
	public Range<Date> getRangeForDay( String aUser, Date aDate ) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Key key = getUserKey(aUser);
		if (key!=null) {
			cal.setTime(aDate);
			Entity workday = getDayByCalendar(key, cal);
			if (workday!=null) {
				setCalendarTimeByProperty(cal, workday, "start");
				Date begin = cal.getTime();
				setCalendarTimeByProperty(cal, workday, "end");
				Date end = cal.getTime();
				return new Range<Date>(begin,end);
			}
		}
		return new Range<Date>(cal.getTime(),cal.getTime());
	}

	/**
	 * Sets the calendar time based on the property value in the current entity.
	 * @param aCal the calendar 
	 * @param aWorkday the workday entity
	 * @param aProperty the name of the property
	 */
	private void setCalendarTimeByProperty(Calendar aCal, Entity aWorkday, String aProperty) {
		Object object = aWorkday.getProperty(aProperty);
		if( object instanceof Long ) {
			aCal.setTimeInMillis(((Long)object).longValue());
		}
	}
	
	/**
	 * Returns the project matching the name attached to the workday that matches
	 * the current time set in the calendar.
	 * @param aUserKey the user key
	 * @param aProject the project
	 * @param aCal the calendar
	 * @param aStart indicates whether event is started or finished
	 * @return the workday project
	 */
	private Entity getWorkdayProject(Key aUserKey, String aProject, Calendar aCal, boolean aStart) {
		Key key = getDayKeyByCalendar(aUserKey,aCal);
		if( key!=null ) {
			return doGetWorkdayProject(key, aProject, aStart);
		}
		return null;
	}

	/**
	 * Returns the workday key using the current time set in the calendar.
	 * @param aUserKey the user key
	 * @param aCal the calendar
	 * @return the workday key
	 */
	private Key getDayKeyByCalendar(Key aUserKey, Calendar aCal) {
		Entity workday = getDayByCalendar(aUserKey, aCal);
		if( workday!=null ) {
			return workday.getKey();
		}
		return null;
	}
	
	/**
	 * Retrieves the workday by the current time set in the calendar.
	 * @param aUserKey the user key
	 * @param aCal the calendar
	 * @return the workday entity if found or <code>null</code>
	 */
	private Entity getDayByCalendar(Key aUserKey, Calendar aCal) {
		int day = aCal.get(Calendar.DAY_OF_MONTH);
		int month = aCal.get(Calendar.MONTH)+1;
		int year = aCal.get(Calendar.YEAR);
		return getDay(aUserKey,day,month,year);
	}

	/**
	 * Returns the day for the given user that represents the day on the given
	 * day of month, month year and year.
	 * @param aUserKey the user key
	 * @param aDay the day of month (ranging from 1 to 31)
	 * @param aMonth the month in year (ranging from 1 to 12)
	 * @param aYear the year 
	 * @return the day entity or <code>null</code> if the entity is not found
	 */
	private Entity getDay( Key aUserKey, int aDay, int aMonth, int aYear ) {
		Key monthKey = getMonthKey(aUserKey, aMonth, aYear);
		if( monthKey!=null ) {
			return doGetDay(monthKey, aDay, aMonth, aYear);
		}
		return null;
	}
	
	/**
	 * Returns a project entity matching the project name.
	 * @param aUserKey the user key
	 * @param aName the project name
	 * @return the project matching the project name
	 */
	private Entity getProject( Key aUserKey, String aName ) {
		Filter filter = new FilterPredicate("projectName", FilterOperator.EQUAL, aName);
		Query q = new Query("Project",aUserKey).setAncestor(aUserKey).setFilter(filter);
		Entity project = service.prepare(q).asSingleEntity();
		return project;
	}
	
	/**
	 * Returns all projects which belong to the given user.
	 * @param aUserKey the user key
	 * @return all projects of the user
	 */
	private List<Entity> getAllProjects( Key aUserKey ) {
		Query q = new Query("Project",aUserKey).setAncestor(aUserKey);
		return service.prepare(q).asList(FetchOptions.Builder.withDefaults());
	}
	
	/**
	 * Returns the user key. The user is created if he doesn't exist.
	 * @param aUser the user nickname
	 * @return the user root
	 */
	private Key getUserKey( String aUser ) {
		Key key = KeyFactory.createKey("User", aUser);
		try {
			Entity user = service.get(key);
			return user.getKey();
		} catch (EntityNotFoundException e) {
			return createNewUser(aUser);
		}
	}
	
	/**
	 * Returns the key for the currently managed user month. The month is
	 * created if it doesn't exist.
	 * @param aUserKey the user key
	 * @param aMonth the currently managed month
	 * @param aYear the current year
	 * @return the key for the currently managed user month
	 */
	private Key getMonthKey( Key aUserKey, int aMonth, int aYear ) {
		String monthYear = getMonthYear(aMonth,aYear);
		Filter filter = new FilterPredicate("monthYear", FilterOperator.EQUAL, monthYear);
		Query q = new Query("Month",aUserKey).setKeysOnly().setAncestor(aUserKey).setFilter(filter);
		Entity month = service.prepare(q).asSingleEntity();
		if(month==null) {
			return createNewMonth(monthYear, aUserKey);
		}
		return month.getKey();
	}
	
	/**
	 * Returns the current workday or creates a new workday if such doesn't exist.
	 * @param aMonthKey the month key
	 * @param aDay the current day in a month
	 * @param aMonth the current month in a year
	 * @param aYear the current year
	 * @return the workday entity
	 */
	private Entity doGetDay( Key aMonthKey, int aDay, int aMonth, int aYear ) {
		String dmy = getDayMonthYear(aDay, aMonth, aYear);
		Filter filter = new FilterPredicate("dayMonthYear", FilterOperator.EQUAL, dmy);
		Query q = new Query("Workday",aMonthKey).setAncestor(aMonthKey).setFilter(filter);
		Entity workday = service.prepare(q).asSingleEntity();
		if (workday==null) {
			return createNewWorkday(dmy, aMonthKey);
		}
		return workday;
	}
	
	/**
	 * Returns the workday project or creates one if a project with the given name does
	 * not exist for the provided name.
	 * @param aWorkdayKey the workday key
	 * @param aProject the project name
	 * @param aStart indicates whether project is being started or finished
	 * @return the workday project entity
	 */
	private Entity doGetWorkdayProject( Key aWorkdayKey, String aProject, boolean aStart ) {
		Filter nameFilter = new FilterPredicate("projectName", FilterOperator.EQUAL, aProject);
		Filter stateFilter = new FilterPredicate("state", FilterOperator.EQUAL, aStart ? "initial" : "open");
		Filter compositeFilter = CompositeFilterOperator.and(nameFilter,stateFilter);
		Query q = new Query("WorkdayProject",aWorkdayKey).setAncestor(aWorkdayKey).setFilter(compositeFilter);
		Entity project = service.prepare(q).asSingleEntity();
		if (project==null) {
			return createNewWorkdayProject(aProject, aWorkdayKey);
		}
		return project;
	}
	
	/**
	 * Returns a string combination of the month + year combination.
	 * @param aMonth the month
	 * @param aYear the year
	 * @return the month-year string representation
	 */
	private String getMonthYear( int aMonth, int aYear ) {
		return aMonth+"."+aYear;
	}
	
	/**
	 * Returns a string combination of the day + month + year combination.
	 * @param aDay the day in the month
	 * @param aMonth the month in the year
	 * @param aYear the year
	 * @return the day-month-year string representation
	 */
	private String getDayMonthYear( int aDay, int aMonth, int aYear ) {
		return aDay+"."+aMonth+"."+aYear;
	}
	
	/**
	 * Creates a new workday entity for the given month.
	 * @param aDmy the day, month, year combination
	 * @param aMonthKey the month key
	 * @return the created workday entity
	 */
	private Entity createNewWorkday( String aDmy, Key aMonthKey ) {
		Entity workday = new Entity("Workday", aDmy, aMonthKey);
		workday.setProperty("dayMonthYear", aDmy);
		service.put(workday);
		return workday;
	}
	
	/**
	 * Creates a new month entry for the given user.
	 * @param aMonthYear the month year to be created
	 * @param aUserKey the user key
	 * @return the key of the created entity
	 */
	private Key createNewMonth( String aMonthYear, Key aUserKey ) {
		Entity month = new Entity("Month", aMonthYear, aUserKey);
		month.setProperty("monthYear", aMonthYear);
		service.put(month);
		return month.getKey();
	}
	
	/**
	 * Creates a new user.
	 * @param aUser the user
	 * @return the key of the newly created user
	 */
	private Key createNewUser(String aUser) {
		Entity user = new Entity("User", aUser);
		user.setProperty("name",aUser);
		user.setProperty("currentStart", null);
		user.setProperty("currentMonth", null);
		service.put(user);
		return user.getKey();
	}
	
	/**
	 * Creates a new project attached to the user.
	 * @param aName the project name
	 * @param aDescription the project description
	 * @param aUserKey the user key
	 * @return the newly created project entity
	 */
	private Entity createNewProject(String aName, String aDescription, Key aUserKey) {
		Entity project = new Entity("Project", aName, aUserKey);
		project.setProperty("projectName", aName);
		project.setProperty("projectDescription", aDescription);
		service.put(project);
		return project;
	}
	
	/**
	 * Creates a new project attached to the workday with start and end times.
	 * @param aName the project name
	 * @param aWorkdayKey the workday key
	 * @return the newly created workday project
	 */
	private Entity createNewWorkdayProject(String aName, Key aWorkdayKey) {
		Entity project = new Entity("WorkdayProject", aName, aWorkdayKey);
		project.setProperty("projectName", aName);
		project.setProperty("state", "initial");
		project.setProperty("start", null);
		project.setProperty("end",null);
		service.put(project);
		return project;
	}
	
}
