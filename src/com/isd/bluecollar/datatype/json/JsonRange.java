/**
 * 23.05.2015
 */
package com.isd.bluecollar.datatype.json;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.isd.bluecollar.datatype.internal.Range;

/**
 * JSON input date range wrapper. Internally the dates are represented as strings. The timezone for
 * the values is specified with the range.
 * @author doan
 */
public class JsonRange {

	/** The time range */
	private Range<String> range;
	/** The timezone string */
	private String timezone;
	
	/**
	 * Creates a new JSON range with empty begin and end strings.
	 */
	public JsonRange() {
		range = new Range<String>("","");
		timezone = "UTC";
	}
	
	/**
	 * Creates a new JSON range with the given begin and end strings.
	 * @param aBegin the range begin
	 * @param anEnd the range end
	 */
	public JsonRange( String aBegin, String anEnd ) {
		range = new Range<String>(aBegin, anEnd);
		timezone = "UTC";
	}

	/**
	 * Returns the range begin.
	 * @return the range begin
	 */
	public String getBegin() {
		return range.getBegin();
	}
	
	/**
	 * Sets the range begin
	 * @param aBegin the range begin
	 */
	public void setBegin(String aBegin) {
		range.setBegin(aBegin);
	}

	/**
	 * Returns the range end.
	 * @return the range end
	 */
	public String getEnd() {
		return range.getEnd();
	}

	/**
	 * Sets the range end.
	 * @param anEnd the range end
	 */
	public void setEnd(String anEnd) {
		range.setEnd(anEnd);
	}
	
	/**
	 * Returns the begin date as a date object.
	 * @return the date object
	 */
	public Date getBeginDate() {
		return getDateFromTimestamp(getBegin());
	}

	/**
	 * Returns the end date as a date object.
	 * @return the date object
	 */
	public Date getEndDate() {
		return getDateFromTimestamp(getEnd());
	}

	/**
	 * Returns the user timezone
	 * @return the user timezone
	 */
	public String getTimezone() {
		return timezone;
	}
	
	/**
	 * Sets the user timezone.
	 * @param aTimezone the timezone
	 */
	public void setTimezone(String aTimezone) {
		this.timezone = aTimezone;
	}
	
	/**
	 * Validates the JSON range.
	 * @return <code>true</code> if range begin lies before range end
	 */
	public boolean validateRange() {
		TimeZone tz = TimeZone.getTimeZone("UTC");
		Calendar beginCal = Calendar.getInstance(tz);
		beginCal.setTime(getBeginDate());
		Calendar endCal = Calendar.getInstance(tz);
		return beginCal.before(endCal);
	}
	
	/**
	 * Parses the timestamp string and converts it into a Java data object.
	 * @param the timestamp
	 * @return the date object
	 */
	private Date getDateFromTimestamp( String aTimestamp ) {
		try {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			long timestamp = Long.parseLong(aTimestamp);
			cal.setTimeInMillis(timestamp);
			return cal.getTime();
		} catch( NumberFormatException e ) {
			return new Date();
		}
	}
	
}
