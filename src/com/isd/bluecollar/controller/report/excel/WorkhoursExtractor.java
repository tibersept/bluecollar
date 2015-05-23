/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller.report.excel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.isd.bluecollar.data.report.ProjectTimeRange;
import com.isd.bluecollar.data.report.ReportData;
import com.isd.bluecollar.data.report.WorkdayData;

/**
 * This class provides the algorithm to extract workhours for the projects
 * from the provided data.
 * @author doan
 */
public class WorkhoursExtractor {

	/** Report data */
	private ReportData data;
	/** Calendar instance */
	private Calendar cal;
	/** Date format */
	private SimpleDateFormat format;
	
	/** A set of overflow projects */
	private Set<String> overflowProjects;
	/** A set of days without any data */
	private Set<String> skippedDays;
	
	/**
	 * Creates a new work hours extractor instance.
	 * @param aData the report data
	 * @param aCal the calendar instance
	 * @param aFormat the date format
	 */
	public WorkhoursExtractor( ReportData aData, Calendar aCal, SimpleDateFormat aFormat ) {
		data = aData;
		cal = aCal;
		format = aFormat;
		overflowProjects = new HashSet<String>();
		skippedDays = new HashSet<String>();
	}
	
	/**
	 * Computes the report end data, ensuring that the last date of the calendar
	 * matches the last day specified as the report end day.
	 * @param aUser the user/owner of projects
	 * @param anEnd the end date
	 */
	public void computeReportEndData( String aUser, Date anEnd ) {
		Calendar cal = getCal();
		int lastDay = cal.get(Calendar.DAY_OF_MONTH);
		long lastTime = cal.getTimeInMillis();
		cal.setTime(anEnd);
		int endDay = cal.get(Calendar.DAY_OF_MONTH);
		long endTime = cal.getTimeInMillis();
		if( (lastDay!=endDay) && (lastTime<endTime) ) {
			processDay(aUser);
		}
	}
	
	/**
	 * Process a single day for the report.
	 * @param aUser the user/owner of projects
	 */
	public void processDay( String aUser ) {
		Calendar cal = getCal();
		Date day = cal.getTime();
		String dayString = getFormat().format(day);
		// Day title
		getData().addDayTitle(dayString);
		
		WorkdayData workday = new WorkdayData();
		// set day string
		workday.setDay(dayString);
		// load the workday data
		workday.loadData(aUser, day);
		// process the data
		doProcessDay( dayString, workday );
		// weekend data
		if( isWeekend(getCal()) ) {
			getData().addInvalidDay(dayString);
		}
		// clear
		workday.clear();
	}
	
	/**
	 * Clears the internal data of the work hours extractor.
	 */
	public void clear() {
		clearOverflows();
		clearSkipped();
	}
	
	/**
	 * Does the actual computation of workday hours
	 * @param anRData the report data being generated
	 * @param aCal the calendar instance
	 * @param aDayString the day string
	 * @param aWorkday the workday data retrieved from datastore
	 * @param anOverflowProjects a set of projects that haven't been completed 
	 * 							on previous days
	 */
	private void doProcessDay( String aDayString, WorkdayData aWorkday ) {
		// Compute day range
		long dayBegin = getCalTime(getCal(), 0, 0, 0);
		long dayEnd = getCalTime(getCal(), 23, 59, 59);
		List<String> projects = aWorkday.getProjects();
		checkCoveredByOverflowingProject(projects);
		if( projects.isEmpty() ) {
			addSkipped(aWorkday.getDay());
		} else {			
			for( String project : projects ) {
				List<ProjectTimeRange> ranges = aWorkday.getRanges(project);
				processProjectRanges(aDayString, dayBegin, dayEnd, project, ranges);
			}
		}
	}
	
	/**
	 * Processes the project ranges that fall onto the same workday.
	 * @param aDayString the day string
	 * @param aDayBegin the begin timestamp of the day
	 * @param aDayEnd the end timestamp of the day
	 * @param aProject the project name
	 * @param aRanges the list of ranges
	 */
	private void processProjectRanges(String aDayString, long aDayBegin,
			long aDayEnd, String aProject, List<ProjectTimeRange> aRanges) {
		float totalTime = 0.0f;
		for( ProjectTimeRange range : aRanges ) {
			long begin = getProjectBegin(range, aDayBegin, aProject);
			long end = getProjectEnd(range, aDayEnd, aProject);
			float tim = getTimeDifference(begin, end);
			totalTime += tim;
		}
		getData().setHours(aDayString, aProject, totalTime);
	}

	/**
	 * Determines the end timestamp of the project on that workday.
	 * @param aRange one of the project ranges for that project on the workday
	 * @param aDayEnd the end timestamp of the day
	 * @param aProject the project name
	 * @return the end timestamp of the project
	 */
	private long getProjectEnd( ProjectTimeRange aRange, long aDayEnd, String aProject ) {
		long end = aRange.getEnd();
		if( end == 0 ) {
			// Overflow project
			addOverflow(aProject);
			end = aDayEnd;
		}
		return end;
	}

	/**
	 * Determines the begin time of the project on that workday.
	 * @param aRange one of the time ranges for the project on the workday
	 * @param aDayBegin the begin timestamp of the day
	 * @param aProject the project
	 * @return the begin timestamp of the project range
	 */
	private long getProjectBegin( ProjectTimeRange aRange, long aDayBegin, String aProject ) {
		long begin = aRange.getBegin();
		if( begin == 0 ) {
			// Completed overflow project
			if( removeOverflow(aProject) ) {
				// ... which was not in range -> initial days skipped 
				for( String day : getSkipped() ) {
					getData().setHours(day, aProject, 24.0f);
				}
				clearSkipped();
			}
			begin = aDayBegin;
		}
		return begin;
	}

	/**
	 * Checks whether an overflow project does not cover the whole day, in which case
	 * the overflow project is added as the only entry of the project list for the day.
	 * @param aProjects the list of projects
	 */
	private void checkCoveredByOverflowingProject( List<String> aProjects ) {
		if( aProjects.isEmpty() ) {
			// Just add the first project from the overflow projects
			for( String project : getOverflows() ) {
				aProjects.add(project);
				break;
			}
		}
	}
	
	/**
	 * Sets the calendar time to a particular hour and returns that time as
	 * milliseconds distance from the epoch.
	 * @param aCal the calendar instance.
	 * @param aHrs the hours
	 * @param aMin the minutes
	 * @param aSec the seconds
	 * @return the distance from the epoch in milliseconds
	 */
	private long getCalTime( Calendar aCal, int aHrs, int aMin, int aSec ) {
		aCal.set(Calendar.HOUR_OF_DAY,aHrs);
		aCal.set(Calendar.MINUTE, aMin);
		aCal.set(Calendar.SECOND, aSec);
		return aCal.getTimeInMillis();
	}

	/**
	 * Computes and returns the time difference between begin and end dates.
	 * @param begin the begin timestamp
	 * @param end the end timestamp
	 * @return the difference in hours represented in decimal scales
	 */
	private float getTimeDifference(long begin, long end) {
		long dif = end - begin;
		if( dif>0 ) {
			int hrs = (int)Math.floor(dif / 3600000);
			dif = dif - (hrs * 3600000);
			int min = (int)Math.round(dif / 60000);
			min = Math.round((min*100)/60);
			return (hrs + (min/100));
		}
		return 0.0f;
	}
	
	/**
	 * Checks if the calendar is set to a weekend day.
	 * @return <code>true</code> if calendar is set on a weekend
	 */
	private boolean isWeekend( Calendar aCal ) {
		int dow = aCal.get(Calendar.DAY_OF_WEEK);
		return (dow==Calendar.SUNDAY || dow==Calendar.SATURDAY);
	}
	
	/**
	 * Returns the report data.
	 * @return the report data
	 */
	private ReportData getData() {
		return data;
	}
	
	/**
	 * Returns the current work hours calendar instance.
	 * @return the calendar
	 */
	private Calendar getCal() {
		return cal;
	}
	
	/**
	 * Returns the date formatter.
	 * @return the date formatter
	 */
	private SimpleDateFormat getFormat() {
		return format;
	}
	
	/**
	 * Returns the overflow projects.
	 * @return the overflow projects
	 */
	private Set<String> getOverflows() {
		return overflowProjects;
	}
	
	/**
	 * Adds the project to overflow projects.
	 * @param aProject the project
	 * @return <code>true</code> if overflow was added
	 */
	private boolean addOverflow( String aProject ) {
		return overflowProjects.add(aProject);
	}
	
	/**
	 * Removes the project from the overflow projects.
	 * @param aProject the project
	 * @return <code>true</code> if overflow was removed
	 */
	private boolean removeOverflow( String aProject ) {
		return overflowProjects.remove(aProject);
	}
	
	/**
	 * Clears the overflowing projects.
	 */
	private void clearOverflows() {
		overflowProjects.clear();
	}
	
	/**
	 * Returns the set of skipped days.
	 * @return the skipped days
	 */
	private Set<String> getSkipped() {
		return skippedDays;
	}
	
	/**
	 * Adds the given day as a skipped day.
	 * @param aDay the skipped day
	 * @return <code>true</code> if workday was added to skipped days
	 */
	private boolean addSkipped( String aDay ) {
		return skippedDays.add(aDay);
	}
	
	/**
	 * Removes the day from the skipped days.
	 * @param aDay the skipped day
	 * @return <code>true</code> if workday was removed from skipped days
	 */
	@SuppressWarnings("unused")
	private boolean removeSkipped( String aDay ) {
		return skippedDays.remove(aDay);
	}
	
	/**
	 * Clears the skipped days set.
	 */
	private void clearSkipped() {
		skippedDays.clear();
	}
	
	
}
