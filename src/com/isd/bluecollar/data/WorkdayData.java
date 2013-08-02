/**
 * 01.08.2013
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
 * Workday data.
 * @author doan
 */
public class WorkdayData {

	/** The workday */
	private String day;
	/** Project titles */
	private Set<String> projects;
	/** Begin times */
	private Map<String, List<ProjectRange>> projectTimes;
	
	/**
	 * Creates a new workday data instance.
	 */
	public WorkdayData() {
		day = "";
		projects = new HashSet<String>();
		projectTimes = new HashMap<String, List<ProjectRange>>();
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
			List<ProjectRange> list = projectTimes.get(aProject);
			if( list==null ) {
				list = new ArrayList<ProjectRange>();
				projectTimes.put(aProject, list);
			}
			ProjectRange range = new ProjectRange(aProject);
			range.setBegin(aBegin);
			range.setEnd(anEnd);
			list.add(range);
		}
	}
	
	/**
	 * Returns the projects of the workday.
	 * @return the projects
	 */
	public List<String> getProjects() {
		return new ArrayList<String>(projects);
	}
 
	/**
	 * Returns the list of ranges for that project on the given day.
	 * @param aProject the project name
	 * @return the list of ranges or an empty list if no ranges are present
	 */
	public List<ProjectRange> getRanges( String aProject ) {
		List<ProjectRange> ranges = projectTimes.get(aProject);
		if( ranges==null ) {
			return Collections.emptyList();
		}
		return ranges;
	}
	
	/**
	 * Clears the intrnal strucutres of the workday data.
	 */
	public void clear() {
		projects.clear();
		for( Map.Entry<String, List<ProjectRange>> entry : projectTimes.entrySet() ) {
			List<ProjectRange> ranges = entry.getValue();
			if( ranges!=null ) {
				ranges.clear();
			}
		}
		projectTimes.clear();
	}
}