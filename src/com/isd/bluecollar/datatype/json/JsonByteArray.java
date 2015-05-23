/**
 * 23.05.2015
 */
package com.isd.bluecollar.datatype.json;

/**
 * JSON byte array wrapper which contains a BASE64 encoded version of a byte array.
 * @author doan
 */
public class JsonByteArray {

	/** Name of the byte array */
	private String name;
	/** The BASE64 encoded byte array */
	private String byteArray;
	
	/**
	 * Creates a new JSON byte array with no data.
	 */
	public JsonByteArray() {
		name= "";
		byteArray = "";
	}
	
	/**
	 * Creates a new byte array with the passed byte array data.
	 * @param aName the byte array name
	 * @param aByteArray the byte array
	 */
	public JsonByteArray( String aName, String aByteArray ) {
		name = aName;
		byteArray = aByteArray;
	}
	
	/**
	 * Returns the name of the byte array, eventually the name of the file.
	 * @return the name of the byte array
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the byte array, eventually the name of the file.
	 * @param aName the name of the byte array
	 */
	public void setName(String aName) {
		name = aName;
	}
	
	/**
	 * Retrieves the byte array.
	 * @return the byte array
	 */
	public String getByteArray() {
		return byteArray;
	}
	
	/**
	 * Sets the byte array.
	 * @param aByteArray the byte array
	 */
	public void setByteArray(String aByteArray) {
		byteArray = aByteArray;
	}
	
}
