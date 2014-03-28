/**
 * 
 */
package com.isd.bluecollar.datatype.json;

/**
 * Json time range. Internally dates are represented as strings. The timezone for the values of
 * this range is UTC.
 * @author doan
 */
public class JsonRange {

	/** begin time point */ 
	private String begin;
	/** end time point */
	private String end;

	/**
	 * Create a json time range.
	 */
	public JsonRange() {
		begin = "";
		end = "";
	}
	
	/**
	 * Creates a json time range.
	 * @param aBegin the time range begin
	 * @param anEnd the time range end
	 */
	public JsonRange( String aBegin, String anEnd ) {
		begin = aBegin;
		end = anEnd;
	}
	
	/**
	 * Returns the begin time point.
	 * @return the begin time point
	 */
	public String getBegin() {
		return begin;
	}
	
	/**
	 * Sets the begin time point.
	 * @param begin the begin time point
	 */
	public void setBegin(String begin) {
		this.begin = begin;
	}
	
	/**
	 * Returns the end time point.
	 * @return the end time point
	 */
	public String getEnd() {
		return end;
	}
	
	/**
	 * Sets the end time point.
	 * @param end the end time point
	 */
	public void setEnd(String end) {
		this.end = end;
	}
	
}
