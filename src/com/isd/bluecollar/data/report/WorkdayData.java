/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.isd.bluecollar.data.internal.Project;
import com.isd.bluecollar.data.internal.Range;
import com.isd.bluecollar.data.store.ProjectDP;
import com.isd.bluecollar.data.store.TimeRangeDP;

/**
 * Workday data.
 * @author doan
 */
public class WorkdayData {

	/** The workday */
	private String day;
	/** List of projects */
	private List<Project> projects;
	/** Project already added to the data */
	private Set<Long> addedProjects;
	/** Begin times */
	private Map<String, List<ProjectTimeRange>> projectTimes;
	
	/**
	 * Creates a new workday data instance.
	 */
	public WorkdayData() {
		day = "";
		projects = new ArrayList<Project>();
		addedProjects = new HashSet<Long>();
		projectTimes = new HashMap<String, List<ProjectTimeRange>>();
	}
	
	/**
	 * Loads the workday data for the given day.
	 * @param aUser the user
	 * @param aCal the calendar set to current day
	 */
	public void loadData( String aUser, Calendar aCal ) {
		ProjectDP projectEntity = new ProjectDP();
		TimeRangeDP rangeEntity = new TimeRangeDP(projectEntity);
		List<Project> projects = projectEntity.getProjects(aUser, false);
		
		// copy calendar
		Calendar cal = Calendar.getInstance(aCal.getTimeZone());
		cal.setTime(aCal.getTime());
		// get begin, end dates
		setToDayBegin(cal);
		Date begin = cal.getTime();
		setToDayEnd(cal);
		Date end = cal.getTime();
		
		for( Project project : projects ) {
			List<Range<Long>> ranges = rangeEntity.getRanges(aUser, project.getName(), begin, end);
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
	public void addProjectTime( Project aProject, Range<Long> aRange ) {
		if( aProject!=null ) {
			addProject(aProject);
			List<ProjectTimeRange> list = projectTimes.get(aProject.getName());
			if( list==null ) {
				list = new ArrayList<ProjectTimeRange>();
				projectTimes.put(aProject.getName(), list);
			}
			ProjectTimeRange range = new ProjectTimeRange(aProject.getName());
			range.setRange(aRange);
			list.add(range);
		}
	}

	/**
	 * Returns the projects of the workday.
	 * @return the projects
	 */
	public List<Project> getProjects() {
		return projects;
	}
 
	/**
	 * Returns the list of ranges for that project on the given day.
	 * @param aProject the project
	 * @return the list of ranges or an empty list if no ranges are present
	 */
	public List<ProjectTimeRange> getRanges( Project aProject ) {
		List<ProjectTimeRange> ranges = projectTimes.get(aProject.getName());
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
		addedProjects.clear();
		for( Map.Entry<String, List<ProjectTimeRange>> entry : projectTimes.entrySet() ) {
			List<ProjectTimeRange> ranges = entry.getValue();
			if( ranges!=null ) {
				ranges.clear();
			}
		}
		projectTimes.clear();
	}
	
	/**
	 * Adds the project to the list of projects.
	 * @param aProject the project
	 */
	private void addProject(Project aProject) {
		if( !addedProjects.contains(aProject.getId()) ) {
			projects.add(aProject);
			addedProjects.add(aProject.getId());
		}
	}

	/**
	 * Sets the calendar to a day's end.
	 * @param cal the calendar
	 */
	private void setToDayEnd(Calendar cal) {
		cal.set(Calendar.HOUR, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
	}

	/**
	 * Sets the calendar to a day's start.
	 * @param cal the calendar
	 */
	private void setToDayBegin(Calendar cal) {
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
	}
}