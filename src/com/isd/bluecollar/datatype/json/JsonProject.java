/**
 * 23.05.2015
 */
package com.isd.bluecollar.datatype.json;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON project wrapper class.
 * @author doan
 */
public class JsonProject {

	/** The project name */
	private String name;
	/** The list of time ranges for this project within the report duration */
	private List<JsonInputRange> ranges;
	
	/**
	 * Creates a new project wrapper instance.
	 * @param aName the project name
	 */
	public JsonProject( String aName ) {
		name = aName;
		ranges = new ArrayList<JsonInputRange>();
	}
	
	/**
	 * Returns the name of the project.
	 * @return the name of the project
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the project.
	 * @param aName the project name
	 */
	public void setName(String aName) {
		this.name = aName;
	}
	
	/**
	 * Returns the list of ranges for this project within the report time limits.
	 * @return the list of ranges for this projects
	 */
	public List<JsonInputRange> getRanges() {
		return ranges;
	}
	
	/**
	 * Sets the list of ranges for this project within the report time limits.
	 * @param aRanges the list of ranges
	 */
	public void setRanges(List<JsonInputRange> aRanges) {
		this.ranges = aRanges;
	}
	
	/**
	 * Adds a range to the list.
	 * @param aRange the range
	 */
	public void addRange( JsonInputRange aRange ) {
		ranges.add(aRange);
	}
}
