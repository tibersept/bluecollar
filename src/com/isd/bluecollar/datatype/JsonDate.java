package com.isd.bluecollar.datatype;

/**
 * JSON date wrapper. Internally the date is passed as a string.
 * @author doan
 */
public class JsonDate {
	
	/** The date */
	private String date;
	
	/**
	 * Creates a new date wrapper.
	 */
	public JsonDate( String aDate) {
		date = aDate;
	}
	
	/**
	 * Returns the date.
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	
	/**
	 * Sets the date.
	 * @param aDate the date
	 */
	public void setDate( String aDate ) {
		date = aDate;
	}
	
}
