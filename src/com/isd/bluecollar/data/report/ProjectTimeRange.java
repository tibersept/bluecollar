/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.report;

import com.isd.bluecollar.data.internal.Range;

/**
 * Project work time range. This class is just used to encapsulate the begin and
 * end timestamps of the work done for a single task of the project at a given time.  
 * @author doan
 */
public class ProjectTimeRange {

	/** The project name */
	private String project;
	/** Project time range */
	private Range<Long> range;
	
	/**
	 * Creates a new project range.
	 * @param aProject the project name
	 */
	public ProjectTimeRange( String aProject ) {
		project = aProject;
		range = new Range<Long>(0L,0L);
	}
	
	/**
	 * Returns the project name.
	 * @return the project name
	 */
	public String getProject() {
		return project;
	}
	
	/**
	 * Returns the begin timestamp of the work on the project.
	 * @return the begin timestamp
	 */
	public long getBegin() {
		return range.getBegin();
	}
	
	/**
	 * Sets the begin timestamp of the work on the project.
	 * @param aBegin the timestamp
	 */
	public void setBegin( Long aBegin ) {
		range.setBegin(aBegin);
	}
	
	/**
	 * Returns the end timestamp of the work on the project.
	 * @return the end timestamp
	 */
	public long getEnd() {
		return range.getEnd();
	}
	
	/**
	 * Sets the end timestamp of the work on the project.
	 * @param anEnd the end timestamp
	 */
	public void setEnd( Long anEnd ) {
		range.setEnd(anEnd);
	}
	
	/**
	 * Sets the project time range.
	 * @param aRange the range
	 */
	public void setRange( Range<Long> aRange ) {
		range = aRange;
	}
	
 }