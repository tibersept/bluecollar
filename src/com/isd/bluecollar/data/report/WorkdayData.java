/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.isd.bluecollar.data.internal.Range;
import com.isd.bluecollar.data.store.Project;
import com.isd.bluecollar.data.store.TimeRange;

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
	private Map<String, List<ProjectTimeRange>> projectTimes;
	
	/**
	 * Creates a new workday data instance.
	 */
	public WorkdayData() {
		day = "";
		projects = new HashSet<String>();
		projectTimes = new HashMap<String, List<ProjectTimeRange>>();
	}
	
	/**
	 * Loads the workday data for the given day.
	 * @param aUser the user
	 * @param aDay the day
	 */
	public void loadData( String aUser, Date aDay ) {
		Project projectEntity = new Project();
		TimeRange rangeEntity = new TimeRange();
		List<String> projects = projectEntity.getProjects(aUser, false);
		
		for( String project : projects ) {
			List<Range<Long>> ranges = rangeEntity.getTimeRanges(aUser, project, aDay);
			for (Range<Long> range : ranges ) {
				addProjectTime(project, range);
			}
		}
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
	 * @param aRange the project time range
	 */
	public void addProjectTime( String aProject, Range<Long> aRange ) {
		if( aProject!=null ) {
			projects.add(aProject);
			List<ProjectTimeRange> list = projectTimes.get(aProject);
			if( list==null ) {
				list = new ArrayList<ProjectTimeRange>();
				projectTimes.put(aProject, list);
			}
			ProjectTimeRange range = new ProjectTimeRange(aProject);
			range.setRange(aRange);
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
	public List<ProjectTimeRange> getRanges( String aProject ) {
		List<ProjectTimeRange> ranges = projectTimes.get(aProject);
		if( ranges==null ) {
			return Collections.emptyList();
		}
		return ranges;
	}
	
	/**
	 * Clears the internal structures of the workday data.
	 */
	public void clear() {
		projects.clear();
		for( Map.Entry<String, List<ProjectTimeRange>> entry : projectTimes.entrySet() ) {
			List<ProjectTimeRange> ranges = entry.getValue();
			if( ranges!=null ) {
				ranges.clear();
			}
		}
		projectTimes.clear();
	}
}