/**
 * 01.08.2013
 */
package com.isd.bluecollar.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Workday data.
 * @author doan
 */
public class WorkdayData {

	/** The workday */
	private String day;
	/** Project titles */
	private List<String> projects;
	/** Begin times */
	private Map<String, Long> beginTimes;
	/** End times */
	private Map<String, Long> endTimes;
	
	/**
	 * Creates a new workday data instance.
	 */
	public WorkdayData() {
		day = "";
		projects = new ArrayList<String>();
		beginTimes = new HashMap<String, Long>();
		endTimes = new HashMap<String,Long>();
	}
	
	/**
	 * Returns the workday.
	 * @return the workday
	 */
	public String getDay() {
		return day;
	}
	
	/**
	 * Sets the workday.
	 * @param aDay the day
	 */
	public void setDay(String aDay) {
		day = aDay;
	}
	
	/**
	 * Adds project time to the workday data.
	 * @param aProject the project
	 * @param aBegin the begin date
	 * @param anEnd the end date
	 */
	public void addProjectTime( String aProject, Long aBegin, Long anEnd ) {
		if( aProject!=null ) {
			projects.add(aProject);
			beginTimes.put(aProject, aBegin);
			endTimes.put(aProject, anEnd);
		}
	}
	
	/**
	 * Returns the projects of the workday.
	 * @return the projects
	 */
	public List<String> getProjects() {
		return projects;
	}
 	
	/**
	 * Returns the begin time of the project on this workday as milliseconds
	 * from the epoch. Returns 0 when begin time is not available.
	 * @return the begin time
	 */
	public long getBeginTime( String aProject ) {
		Long begin = beginTimes.get(aProject);
		if( begin!=null ) {
			return begin.longValue();
		}
		return 0;
	}
	
	/**
	 * Returns the end time of the project on this workday.
	 * @param aProject
	 * @return
	 */
	public long getEndTime( String aProject ) {
		Long end = endTimes.get(aProject);
		if( end!=null ) {
			return end.longValue();
		}
		return 0;
	}
	
}