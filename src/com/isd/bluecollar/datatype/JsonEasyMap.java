/**
 * 04.08.2013
 */
package com.isd.bluecollar.datatype;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of the easy map is just to keep track of two subsequent arrays and
 * to map keys in one array and the values in the other. The map is reduced to managing
 * just strings. This map does not filter out duplicates.
 * @author doan
 */
public class JsonEasyMap {
	
	/** The key list */
	private List<String> keyList;
	/** The value list */
	private List<String> valueList;
	
	/**
	 * Creates an easy map instance.
	 */
	public JsonEasyMap() {
		keyList = new ArrayList<String>();
		valueList = new ArrayList<String>();
	}
	
	/**
	 * Adds a new key value pair to the map.
	 * @param aKey the key
	 * @param aValue the value
	 */
	public void addEntry( String aKey, String aValue ) {
		keyList.add(aKey);
		valueList.add(aValue);
	}
	
	/**
	 * Returns the value matching the first occurrence of key, or an empty 
	 * string if key is not found.
	 * @param aKey the key
	 * @return the value
	 */
	public String getValue( String aKey ) {
		int idx = keyList.indexOf(aKey);
		if( idx!=-1 ) {
			return valueList.get(idx);
		}
		return "";
	}
	
	/**
	 * Removes the key and returns the first value matching that key. Returns an empty
	 * string if the key is not found.
	 * @param aKey the key
	 * @return the value matching the key
	 */
	public String removeKey( String aKey ) {
		int idx = keyList.indexOf(aKey);
		if( idx!=-1 ) {
			keyList.remove(idx);
			return valueList.remove(idx);
		}
		return "";
	}
	
	/**
	 * Returns the key list.
	 * @return the key list
	 */
	public List<String> getKeyList() {
		return keyList;
	}
	
	/**
	 * Returns the value list.
	 * @return the value list
	 */
	public List<String> getValueList() {
		return valueList;
	}
	
}
