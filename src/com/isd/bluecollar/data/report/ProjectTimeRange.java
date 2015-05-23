/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.report;

/**
 * Project work time range. This class is just used to encapsulate the begin and
 * end timestamps of the work done for a single task of the project at a given time.  
 * @author doan
 */
public class ProjectTimeRange {

	/** The project name */
	private String project;
	/** Project begin timestamp */
	private Long begin;
	/** Project end timestamp */
	private Long end;
	
	/**
	 * Creates a new project range.
	 * @param aProject the project name
	 */
	public ProjectTimeRange( String aProject ) {
		begin = null;
		end = null;
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
		if( begin!=null ) {
			return begin.longValue();
		}
		return 0;
	}
	
	/**
	 * Sets the begin timestamp of the work on the project.
	 * @param aBegin the timestamp
	 */
	public void setBegin( Long aBegin ) {
		begin = aBegin;
	}
	
	/**
	 * Returns the end timestamp of the work on the project.
	 * @return the end timestamp
	 */
	public long getEnd() {
		if( end!=null ) {
			return end.longValue();
		}
		return 0;
	}
	
	/**
	 * Sets the end timestamp of the work on the project.
	 * @param anEnd the end timestamp
	 */
	public void setEnd( Long anEnd ) {
		end = anEnd;
	}
 }
