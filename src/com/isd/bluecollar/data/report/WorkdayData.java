/**
 * 01.08.2013
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

import com.isd.bluecollar.data.store.Project;
import com.isd.bluecollar.data.store.TimeRange;
import com.isd.bluecollar.datatype.Range;

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
	private Map<String, List<ProjectTaskTimeRange>> projectTimes;
	
	/**
	 * Creates a new workday data instance.
	 */
	public WorkdayData() {
		day = "";
		projects = new HashSet<String>();
		projectTimes = new HashMap<String, List<ProjectTaskTimeRange>>();
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
				addProjectTime(project, range.getBegin(), range.getEnd());
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
	 * @param aBegin the begin date
	 * @param anEnd the end date
	 */
	public void addProjectTime( String aProject, Long aBegin, Long anEnd ) {
		if( aProject!=null ) {
			projects.add(aProject);
			List<ProjectTaskTimeRange> list = projectTimes.get(aProject);
			if( list==null ) {
				list = new ArrayList<ProjectTaskTimeRange>();
				projectTimes.put(aProject, list);
			}
			ProjectTaskTimeRange range = new ProjectTaskTimeRange(aProject);
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
	public List<ProjectTaskTimeRange> getRanges( String aProject ) {
		List<ProjectTaskTimeRange> ranges = projectTimes.get(aProject);
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
		for( Map.Entry<String, List<ProjectTaskTimeRange>> entry : projectTimes.entrySet() ) {
			List<ProjectTaskTimeRange> ranges = entry.getValue();
			if( ranges!=null ) {
				ranges.clear();
			}
		}
		projectTimes.clear();
	}
}