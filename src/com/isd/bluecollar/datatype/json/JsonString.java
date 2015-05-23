/**
 * 23.05.2015
 */
package com.isd.bluecollar.datatype.json;

/**
 * JSON string wrapper.
 * @author doan
 */
public class JsonString {
	
	/** The string value */
	private String string;
	
	/**
	 * Creates a new string wrapper with empty string.
	 */
	public JsonString() {
		string = "";
	}
	
	/**
	 * Creates a new string wrapper.
	 */
	public JsonString( String aString) {
		string = aString;
	}
	
	/**
	 * Returns the string.
	 * @return the string
	 */
	public String getString() {
		return string;
	}
	
	/**
	 * Sets the string.
	 * @param aString the string
	 */
	public void setString( String aString ) {
		string = aString;
	}
	
}
