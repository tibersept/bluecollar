/**
 * 
 */
package com.isd.bluecollar.datatype;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * JSON date range wrapper. Internally the dates are represented as strings.
 * @author doan
 */
public class JsonRange {

	/** The range begin */
	private String begin;
	/** The range end */
	private String end;
	/** The timezone string */
	private String timezone;
	
	/**
	 * Creates a new JSON range with empty begin and end strings.
	 */
	public JsonRange() {
		begin = "";
		end = "";
		timezone = "UTC";
	}
	
	/**
	 * Creates a new JSON range with the given begin and end strings.
	 * @param aBegin the range begin
	 * @param anEnd the range end
	 */
	public JsonRange( String aBegin, String anEnd ) {
		begin = aBegin;
		end = anEnd;
		timezone = "UTC";
	}

	/**
	 * Returns the range begin.
	 * @return the range begin
	 */
	public String getBegin() {
		return begin;
	}
	
	/**
	 * Returns the begin date as a date object.
	 * @return the date object
	 */
	public Date getBeginDate() {
		return getDateFromTimestamp(getBegin());
	}

	/**
	 * Sets the range begin
	 * @param aBegin the range begin
	 */
	public void setBegin(String aBegin) {
		this.begin = aBegin;
	}

	/**
	 * Returns the range end.
	 * @return the range end
	 */
	public String getEnd() {
		return end;
	}

	/**
	 * Returns the end date as a date object.
	 * @return the date object
	 */
	public Date getEndDate() {
		return getDateFromTimestamp(getEnd());
	}
	
	/**
	 * Sets the range end.
	 * @param anEnd the range end
	 */
	public void setEnd(String anEnd) {
		this.end = anEnd;
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
	 * @param timezone the timezone
	 */
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	
	/**
	 * Parses the timestamp string and converts into a Java data object.
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
