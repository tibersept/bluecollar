/**
 * 
 */
package com.isd.bluecollar.data;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
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
	 * Sets the start of a workday.
	 * @param aUserName the username
	 * @param aTimestamp the timestamp
	 */
	public void setDayStart( String aUser, Date aTimestamp ) {
		updateDay(aUser,aTimestamp,true);
	}
	
	/**
	 * Sets the end of a workday.
	 * @param aUserName the username
	 * @param aTimestamp the timestamp
	 */
	public void setDayEnd( String aUser, Date aTimestamp ) {
		updateDay(aUser,aTimestamp,false);
	}

	/**
	 * Updates one of the timestamp on a workday. 
	 * @param aUser the username
	 * @param aDate the timestamp
	 * @param aStart flag indicating whether this is check-in or check-out
	 */
	public void updateDay( String aUser, Date aDate, boolean aStart ) {
		Key key = getUserKey(aUser);
		if (key!=null) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			cal.setTime(aDate);
			Entity workday = getDayByCalendar(key,cal);
			if (workday!=null) {
				if( aStart ) {
					workday.setProperty("start", cal.getTimeInMillis());
				} else {
					workday.setProperty("end", cal.getTimeInMillis());
				}
				service.put(workday);
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
		workday.setProperty("start", null);
		workday.setProperty("end", null);
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
	
}
