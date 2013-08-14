/**
 * 
 */
package com.isd.bluecollar.datatype;

import java.util.List;

/**
 * Json string list wrapper.
 * @author doan
 */
public class JsonList {

	private List<String> itemList;
	
	/**
	 * Creates a new instance of the JSON string list wrapper.
	 * @param aList the string list
	 */
	public JsonList( List<String> aList ) {
		itemList = aList;
	}
	
	/**
	 * Returns the item list wrapped in the JSON string list wrapper.
	 * @return the item list
	 */
	public List<String> getItemList() {
		return itemList;
	}
	
	/**
	 * Sets the item list of the JSON string list wrapper.
	 * @param aList the string list
	 */
	public void setItemList(List<String> aList) {
		itemList = aList;
	}
}
