/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
 * Time range entity wrapper.
 * @author doan
 */
public class TimeRange {

	/**
	 * Scalar mapping of [begin timestamp; end timestamp] range. The idea behind the mapping is 
	 * to convert date timestamps into alphanumeric values using a plain Caesar cipher and combining
	 * the two values. The scalar value representing the range has a natural lexicographical order 
	 * and can be used as range search criteria. Which brings us to the reason for this mapping:
	 *  > Datastore API: Inequality filters are limited to at most one property
	 * The range cannot be queried as two timestamp but a single scalar value can be.  
	 * @author doan
	 */
	private static class ScalarRange {
		/** Separator between begin date and end date */
		private final static String SEPARATOR = "-";
		/** Length of the encoded timestamp */
		private final static int ENCODED_TIMESTAMP_LENGTH = 30;
		
		/**
		 * Converts the range vector into a scalar date value.
		 * @param aBegin the begin timestamp
		 * @param anEnd the end timestamp
		 * @return the scalar range value
		 */
		public String toScalar( long aBegin, long anEnd ) {
			String begin = toAlpha(toDigits(aBegin));
			String end = toAlpha(toDigits(anEnd));
			return begin+SEPARATOR+end;
		}
		
		/**
		 * Converts the scalar range value into a range vector.
		 * @param aScalar the scalar range value
		 * @return the vector with begin and end timestamp 
		 */
		public long[] toRange( String aScalar ) {
			String[] range = aScalar.split(SEPARATOR);
			long begin = toLong(toDigits(range[0]));
			long end = toLong(toDigits(range[1]));
			return new long[]{begin,end};
		}
		
		/**
		 * Converts the array of digits into a alphanumeric encoded string.
		 * @param aDigits the digits to be converted into a string
		 * @return the alphanumeric encoded string
		 */
		private String toAlpha( Integer[] aDigits ) {
			StringBuilder sb = new StringBuilder();
			int pad = Math.max(0, ENCODED_TIMESTAMP_LENGTH - aDigits.length); // covers -100000 b.c. to 100000 a.d. at least 
			for(int i=0; i<pad; i++) {
				sb.append('a');
			}
			for(Integer digit : aDigits) {
				sb.append((char)('a' + digit.intValue()));
			}
			return sb.toString();
		}
		
		/**
		 * Converts the array of digits into a long value.
		 * @param aDigits the digits to be converted to a long value 
		 * @return the long value
		 */
		private long toLong( Integer[] aDigits ) {
			long val = 0L;
			for( Integer digit : aDigits ) {
				val = val*10 + digit;
			}
			return val;
		}
		
		/**
		 * Converts the alphanumeric string value into an array of digits.
		 * @param aVal the alphanumeric encoded value
		 * @return the array of digits
		 */
		private Integer[] toDigits( String aVal ) {
			List<Integer> digits = new ArrayList<Integer>();
			for( int i=0; i<aVal.length(); i++ ) {
				char s = aVal.charAt(i);
				digits.add(s - 'a');
			}
			return digits.toArray(new Integer[digits.size()]);
		}
		
		/**
		 * Converts the long value into an array of digits.
		 * @param aVal the long value
		 * @return the array of digits
		 */
		private Integer[] toDigits( long aVal ) {
			List<Integer> digits = new ArrayList<Integer>();
			long number = aVal;
			while( number > 0 ) {
				digits.add((int)(number % 10));
				number = number/10;
			}
			Collections.reverse(digits);
			return digits.toArray(new Integer[digits.size()]);
		}
	}
	
	/** Range to scalar bi-directional converter*/
	private static final ScalarRange CONVERTER = new ScalarRange();
	
	/** Time range entity */
	private static final String TIME_RANGE = "TimeRange";
	/** Property start time of a range */
	private static final String PROPERTY_RANGE = "range";
	/** Property state of a state */
	private static final String PROPERTY_STATE = "state";

	/** Initial task state */
	private static final String STATE_INITIAL = "initial";
	/** Finished task state */
	private static final String STATE_FINISHED = "finished";
	/** Open task state */
	private static final String STATE_OPEN = "open";

	/** The project */
	private Project project;
	/** The datastore service */
	private DatastoreService service;
	
	/**
	 * Creates an instance of the time range wrapper.
	 */
	public TimeRange() {
		service = DatastoreServiceFactory.getDatastoreService();
		project = new Project();
	}
	
	/**
	 * Retrieves the time ranges on a given day.
	 * @param aUser the user
	 * @param aProject the project
	 * @param aDay the day
	 * @return the list of ranges
	 */
	public List<Range<Long>> getRanges( String aUser, String aProject, Date aDay ) {
		List<Entity> entityList = getRangeEntities(aUser, aProject, aDay);
		List<Range<Long>> list = getRangeListFromEntities(entityList);
		return list;
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
	 * @param aTimestamp the begin timestamp
	 */
	public void openRange( String aUser, String aProject, Date aTimestamp ) {
		Key key = project.getKey(aUser, aProject);
		if (key!=null) {
			if( !hasOpenRange(key) ) {
				Entity timeRange = createNewTimeRange(key);
				Calendar cal = getCal();
				cal.setTime(aTimestamp);
				long begin = cal.getTimeInMillis();
				String range = CONVERTER.toScalar(begin, 0);
				timeRange.setProperty(PROPERTY_RANGE, range);
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
	 * @param aTimestamp the end timestamp
	 */
	public void closeRange( String aUser, String aProject, Date aTimestamp ) {
		Key key = project.getKey(aUser, aProject);
		if (key!=null) {
			if( hasOpenRange(key) ) {
				Entity timeRange = getOpenRange(key);
				Calendar cal = getCal();
				cal.setTime(aTimestamp);
				String range = (String)timeRange.getProperty(PROPERTY_RANGE);
				long[] rangeVector = CONVERTER.toRange(range);
				range = CONVERTER.toScalar(rangeVector[0], cal.getTimeInMillis());
				timeRange.setProperty(PROPERTY_RANGE, range);
				timeRange.setProperty(PROPERTY_STATE, STATE_FINISHED);
				service.put(timeRange);
			}
		}
	}

	/**
	 * Retrieves all time ranges of the given project which are contained within the given day. 
	 * @param aUser the user
	 * @param aProject the project
	 * @param aDay the day
	 * @return the list of time range entities for the project of the user on that day
	 */
	private List<Entity> getRangeEntities( String aUser, String aProject, Date aDay ) {
		Entity pro = project.getProject(aUser, aProject);
		if( pro!=null ) {
			Key proKey = pro.getKey();
			Calendar cal = getCal();
			cal.setTime(aDay);
			setToDayBegin(cal);
			long begin = cal.getTimeInMillis();
			setToDayEnd(cal);
			long end = cal.getTimeInMillis();
			return getTimeRangesInRange(proKey, begin, end);
		}
		return Collections.emptyList();
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
		Entity pro = project.getProject(aUser, aProject);
		if( pro!=null ) {
			Key proKey = pro.getKey();
			Calendar cal = getCal();
			cal.setTime(aBegin);
			long begin = cal.getTimeInMillis();
			cal.setTime(anEnd);
			long end = cal.getTimeInMillis();
			if( begin < end ) {
				return getTimeRangesInRange(proKey, begin, end);				
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
			String range = (String) entityRange.getProperty(PROPERTY_RANGE);
			long[] rangeVector = CONVERTER.toRange(range);
			list.add(new Range<Long>(rangeVector[0], rangeVector[1]));
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
		project.setProperty(PROPERTY_RANGE, CONVERTER.toScalar(0, 0));
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
		String rangeBegin = CONVERTER.toScalar(aBegin, aBegin); 
		String rangeEnd = CONVERTER.toScalar(aBegin, anEnd);
		Filter beginFilter = new FilterPredicate(PROPERTY_RANGE, FilterOperator.GREATER_THAN, rangeBegin);
		Filter endFilter = new FilterPredicate(PROPERTY_RANGE, FilterOperator.LESS_THAN, rangeEnd);
		Filter compositeFilter = CompositeFilterOperator.and(beginFilter, endFilter);
		Query q = new Query(TIME_RANGE,aProKey).setAncestor(aProKey).setFilter(compositeFilter);
		return service.prepare(q).asList(FetchOptions.Builder.withDefaults());
	}

	/**
	 * Returns a UTC calendar instance.
	 * @return the UTC calendar instance
	 */
	private Calendar getCal() {
		return Calendar.getInstance( TimeZone.getTimeZone("UTC") );
	}
	
	/**
	 * Sets the calendar to a day's end.
	 * @param cal the calendar
	 */
	private void setToDayEnd(Calendar cal) {
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
	}

	/**
	 * Sets the calendar to a day's start.
	 * @param cal the calendar
	 */
	private void setToDayBegin(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
	}
}
