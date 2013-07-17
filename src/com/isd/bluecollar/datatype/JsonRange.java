/**
 * 
 */
package com.isd.bluecollar.datatype;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * JSON date range wrapper. Internally the dates are represented as strings.
 * @author doan
 */
public class JsonRange {

	/** The range begin */
	private String begin;
	/** The range end */
	private String end;
	
	/**
	 * Creates a new JSON range with empty begin and end strings.
	 */
	public JsonRange() {
		begin = "";
		end = "";
	}
	
	/**
	 * Creates a new JSON range with the given begin and end strings.
	 * @param aBegin the range begin
	 * @param anEnd the range end
	 */
	public JsonRange( String aBegin, String anEnd ) {
		begin = aBegin;
		end = anEnd;
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
	 * @param aSdf the date formatter
	 * @return the date object
	 */
	public Date getBeginDate( SimpleDateFormat aSdf ) {
		try {
			return aSdf.parse(begin);
		} catch( ParseException e ) {
			return new Date();
		}
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
	 * @param aSdf the date formatter
	 * @return the date object
	 */
	public Date getEndDate( SimpleDateFormat aSdf ) {
		try {
			return aSdf.parse(end);
		} catch( ParseException e ) {
			return new Date();
		}
	}
	
	/**
	 * Sets the range end.
	 * @param anEnd the range end
	 */
	public void setEnd(String anEnd) {
		this.end = anEnd;
	}
	
}
