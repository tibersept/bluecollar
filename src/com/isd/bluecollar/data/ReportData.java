/**
 * 
 */
package com.isd.bluecollar.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Structured report data.
 * @author doan
 */
public class ReportData {

	/** Day titles */
	private List<String> dayTitles;
	/** Project titles */
	private List<String> projectTitles;
	/** Day project hours */
	private Map<String, Float> dayProjectHours;
	/** Invalid days */
	private Set<String> invalidDays;
	
	/**
	 * Creates a new report data instance.
	 */
	public ReportData() {
		dayTitles = new ArrayList<String>();
		projectTitles = new ArrayList<String>();
		dayProjectHours = new HashMap<String, Float>();
		invalidDays = new HashSet<String>();
	}
	
	/**
	 * Adds a day title to the list of day titles.
	 * @param aTitle the day title
	 */
	public void addDayTitle( String aTitle ) {
		dayTitles.add( aTitle );
	}
	
	/**
	 * Returns the list of day titles.
	 * @return the list of day titles
	 */
	public List<String> getDayTitles() {
		return Collections.unmodifiableList(dayTitles);
	}
	
	/**
	 * Adds a project title to the list of project titles.
	 * @param aTitle the project title
	 */
	public void addProjectTitle( String aTitle ) {
		projectTitles.add( aTitle );
	}
	
	/**
	 * Returns the list of project titles.
	 * @return the list of project titles
	 */
	public List<String> getProjectTitles() {
		return Collections.unmodifiableList(projectTitles);
	}
	
	/**
	 * Sets the hours as the project hours on the given day.
	 * @param aDay the day title
	 * @param aProject the project title
	 * @param anHours the hours
	 */
	public void setHours( String aDay, String aProject, Float anHours ) {
		dayProjectHours.put(getDayProjectKey(aDay, aProject), anHours);
	}
	
	/**
	 * Returns the project hours on the given day.
	 * @param aDay the day title
	 * @param aProject the project title
	 * @return the hours
	 */
	public float getHours( String aDay, String aProject ) {
		Float val = dayProjectHours.get(getDayProjectKey(aDay,aProject));
		if( val==null ) {
			return 0.0f;
		}
		return val.floatValue();
	}
	
	/**
	 * Adds an invalid day to the set of invalid days.
	 * @param aDay the day
	 */
	public void addInvalidDay( String aDay ) {
		invalidDays.add(aDay);
	}
	
	/**
	 * Checks whether the given day is invalid.
	 * @param aDay the day
	 * @return <code>true</code> if day is invalid
	 */
	public boolean isInvalidDay( String aDay ) {
		return invalidDays.contains(aDay);
	}
	
	/**
	 * Returns the number of days in this report data.
	 * @return the number of days
	 */
	public int getDayCount() {
		return dayTitles.size();
	}
	
	/**
	 * Returns the number of projects in this report data.
	 * @return the number of projects
	 */
	public int getProjectCount() {
		return projectTitles.size();
	}
	
	/**
	 * Returns a combined day project key.
	 * @param aDay the day title
	 * @param aProject the project title
	 * @return the day project key
	 */
	private String getDayProjectKey( String aDay, String aProject ) {
		return aDay + "-" + aProject;
	}
}
