/**
 * 
 */
package com.isd.bluecollar.datatype.json;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON report wrapper class.
 * @author doan
 */
public class JsonReport {

	/** the user */
	private String user;
	/** the timezone */
	private String timezone;
	/** the locale */
	private String locale;
	
	/** the begin time */
	private String begin;
	/** the end time */
	private String end;
	
	/** the list of projects */
	private List<JsonProject> projectList;
	
	/**
	 * Creates a new instance of the JSON Report
	 */
	public JsonReport() {
		projectList = new ArrayList<JsonProject>();
	}
	
	/**
	 * Returns the report user.
	 * @return the report user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Sets the report user.
	 * @param aUser the user
	 */
	public void setUser(String aUser) {
		this.user = aUser;
	}
	
	/**
	 * Return timezone.
	 * @return the timezone
	 */
	public String getTimezone() {
		return timezone;
	}
	
	/**
	 * Sets the timezone.
	 * @param aTimezone the timezone
	 */
	public void setTimezone(String aTimezone) {
		this.timezone = aTimezone;
	}
	
	/**
	 * Returns the report locale.
	 * @return the locale
	 */
	public String getLocale() {
		return locale;
	}
	
	/**
	 * Sets the report locale.
	 * @param aLocale the locale
	 */
	public void setLocale(String aLocale) {
		this.locale = aLocale;
	}
	
	/**
	 * Returns the report begin time point.
	 * @return the report begin
	 */
	public String getBegin() {
		return begin;
	}
	
	/**
	 * Sets the report begin time point.
	 * @param aBegin the begin time point
	 */
	public void setBegin(String aBegin) {
		this.begin = aBegin;
	}
	
	/**
	 * Returns the end time point for the report.
	 * @return the end time point
	 */
	public String getEnd() {
		return end;
	}
	
	/**
	 * Set the end time point for the report.
	 * @param anEnd the end time point
	 */
	public void setEnd(String anEnd) {
		this.end = anEnd;
	}
	
	/**
	 * Returns the list of projects.
	 * @return the list of projects
	 */
	public List<JsonProject> getProjectList() {
		return projectList;
	}
	
	/**
	 * Sets the list of projects.
	 * @param aProjectList the list of projects
	 */
	public void setProjectList(List<JsonProject> aProjectList) {
		this.projectList = aProjectList;
	}
	
}