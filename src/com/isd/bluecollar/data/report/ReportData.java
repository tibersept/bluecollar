/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.isd.bluecollar.data.internal.Project;

/**
 * Structured report data.
 * @author doan
 */
public class ReportData {

	/** Day index titles */
	private List<String> dayIndexTitles;
	/** Day name titles */
	private List<String> dayNameTitles;
 	/** Project titles */
	private List<Project> projects;
	/** Total hours per day */
	private Map<String, Float> dayHours;
	/** Total hours spent on project */
	private Map<String, Float> projectHours;
	/** Day project hours */
	private Map<String, Float> dayProjectHours;
	/** Free days (holidays, weekends) */
	private Set<String> freeDays;
	/** Total workhours for the period */
	private float totalWorkhours;
	/** Required workhours */
	private float requiredWorkhours;
	
	/**
	 * Creates a new report data instance.
	 */
	public ReportData() {
		projects = new ArrayList<Project>();
		dayIndexTitles = new ArrayList<String>();
		dayNameTitles = new ArrayList<String>();
		dayHours = new HashMap<String,Float>();
		projectHours = new HashMap<String,Float>();
		dayProjectHours = new HashMap<String,Float>();
		freeDays = new HashSet<String>();
		totalWorkhours = 0.0f;
		requiredWorkhours = 0.0f;
	}
	
	/**
	 * Adds a day index title to the list of day index titles.
	 * @param aTitle the day index title
	 */
	public void addDayIndexTitle( String aTitle ) {
		dayIndexTitles.add(aTitle);
	}
	
	/**
	 * Returns the list of day index titles.
	 * @return the list of day index titles
	 */
	public List<String> getDayIndexTitles() {
		return Collections.unmodifiableList(dayIndexTitles);
	}
	
	/**
	 * Adds a day name title to the list of day name titles.
	 * @param aTitle the day name title
	 */
	public void addDayNameTitle( String aTitle ) {
		dayNameTitles.add(aTitle);
	}
	
	/**
	 * Returns the list of day name titles.
	 * @return the list of day name titles
	 */
	public List<String> getDayNameTitles() {
		return Collections.unmodifiableList(dayNameTitles);
	}
	
	/**
	 * Returns the list of project titles.
	 * @return the list of project titles
	 */
	public List<Project> getProjects() {
		return Collections.unmodifiableList(projects);
	}
	
	/**
	 * Adds a free day to the set of invalid days.
	 * @param aDay the day
	 */
	public void addFreeDay( String aDay ) {
		freeDays.add(aDay);
	}
	
	/**
	 * Checks whether the given day is free (a holiday or a weekend).
	 * @param aDay the day
	 * @return <code>true</code> if day is a holiday or a weekend
	 */
	public boolean isFreeDay( String aDay ) {
		return freeDays.contains(aDay);
	}
	
	/**
	 * Sets the hours as the project hours on the given day.
	 * @param aDay the day title
	 * @param aProject the project title
	 * @param anHours the hours
	 */
	public void setHours( String aDay, Project aProject, Float anHours ) {
		addProject(aProject);
		addDayHours(aDay, anHours);
		addProjectHours(aProject, anHours);
		addDayProjectHours(aDay, aProject, anHours);
		totalWorkhours += anHours.floatValue();
	}

	/**
	 * Returns the work hours on a given day.
	 * @param aDay the day
	 * @return the hours
	 */
	public float getTotalDayHours( String aDay ) {
		return getHours(dayHours, aDay);
	}

	/**
	 * Returns the total project hours spent on a project within the specified period of time. 
	 * @param aProject the project
	 * @return the total project hours
	 */
	public float getTotalProjectHours( String aProject ) {
		return getHours(projectHours, aProject);
	}

	/**
	 * Returns the project hours on the given day.
	 * @param aDay the day title
	 * @param aProject the project title
	 * @return the hours
	 */
	public float getProjectHoursOnDay( String aDay, String aProjectTitle ) {
		return getHours(dayProjectHours, getDayProjectKey(aDay,aProjectTitle));
	}
	
	/**
	 * Returns the total work hours for the report period.
	 * @return the total work hours
	 */
	public float getTotalWorkhours() {
		return totalWorkhours;
	}
	
	/**
	 * Returns the required work hours for the report period.
	 * @return the required work hours
	 */
	public float getRequiredWorkhours() {
		return requiredWorkhours;
	}
	
	/**
	 * Sets the required work hours for the report period.
	 * @param anHours the required work hours 
	 */
	public void setRequiredWorkhours( float anHours ) {
		requiredWorkhours = anHours;
	}

	/**
	 * Returns the number of days in this report data.
	 * @return the number of days
	 */
	public int getDayCount() {
		return dayIndexTitles.size();
	}
	
	/**
	 * Returns the number of projects in this report data.
	 * @return the number of projects
	 */
	public int getProjectCount() {
		return projects.size();
	}
	
	/**
	 * Clears computed data. Should be executed after report generation.
	 */
	public void clear() {
		projects.clear();
		dayIndexTitles.clear();
		dayNameTitles.clear();
		dayHours.clear();
		dayProjectHours.clear();
		freeDays.clear();
	}
	
	/**
	 * Adds a project.
	 * @param aProject the project
	 */
	private void addProject( Project aProject ) {
		if( !projects.contains(aProject) ) {
			projects.add(aProject);
		}
	}

	/**
	 * Adds the given amount of hours to the total hours of work on the given day.
	 * @param aDay the day
	 * @param anHours the hours
	 */
	private void addDayHours( String aDay, Float anHours ) {
		addHours(dayHours, aDay, anHours);
	}
	
	/**
	 * Adds the given amount of hours to the total hours of work spent on the project.
	 * @param aProject the project
	 * @param anHours the hours
	 */
	private void addProjectHours( Project aProject, Float anHours ) {
		addHours(projectHours, aProject.getName(), anHours);
	}

	/**
	 * Adds the given amount of hours to the project hours on that given day.
	 * @param aDay the day
	 * @param aProject the project
	 * @param anHours the hours
	 */
	private void addDayProjectHours(String aDay, Project aProject, Float anHours) {
		String key = getDayProjectKey(aDay, aProject.getName());
		addHours(dayProjectHours, key, anHours);
	}
	
	/**
	 * Adds the given amount of hours to the hours map, to the item matching the key.
	 * @param anHoursMap
	 * @param aKey
	 * @param aVal
	 */
	private void addHours( Map<String, Float> anHoursMap, String aKey, Float aVal ) {
		Float f = anHoursMap.get(aKey);
		if( f==null ) {
			f = new Float(0);
		}
		float total = f.floatValue()+aVal.floatValue();
		dayHours.put(aKey, total);
	}

	/**
	 * Returns stored hours for the given key.
	 * @param anHoursMap the map
	 * @param aKey the key
	 * @return the value in hours or 0 hours if key not found
	 */
	private float getHours( Map<String, Float> anHoursMap, String aKey ) {
		Float f = anHoursMap.get(aKey);
		if( f==null ) {
			return 0.0f;
		}
		return f.floatValue();
	}

	/**
	 * Returns a combined day project key.
	 * @param aDay the day title
	 * @param aProject the project title
	 * @return the day project key
	 */
	private String getDayProjectKey( String aDay, String aProjectTitle ) {
		return aDay + "-" + aProjectTitle;
	}
}
