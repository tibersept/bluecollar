/**
 * 
 */
package com.isd.bluecollar.datatype;

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
	 * Sets the range end.
	 * @param anEnd the range end
	 */
	public void setEnd(String anEnd) {
		this.end = anEnd;
	}
	
}
