package com.isd.bluecollar.datatype;

/**
 * JSON byte array wrapper which contains a BASE64 encoded version of a byte array.
 * @author isakov
 */
public class JsonByteArray {

	/** The BASE64 encoded byte array */
	private String byteArray;
	
	/**
	 * Creates a new JSON byte array with no data.
	 */
	public JsonByteArray() {
		byteArray = "";
	}
	
	/**
	 * Creates a new byte array with the passed byte array data.
	 * @param aByteArray the byte array
	 */
	public JsonByteArray( String aByteArray ) {
		byteArray = aByteArray;
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
