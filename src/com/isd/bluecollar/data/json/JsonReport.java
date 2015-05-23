/**
 * 23.05.2015
 */
package com.isd.bluecollar.data.json;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON report wrapper class.
 * @author doan
 */
public class JsonReport {

	/** the flag indicates whether this report has been finished */
	private boolean finished;
	
	/** the user */
	private String user;
	/** the locale */
	private String language;
	/** The range with timezone */
	private JsonRange range;
	/** The list of projects */
	private List<JsonProject> projectList;
	
	/**
	 * Creates a new instance of the JSON Report
	 */
	public JsonReport() {
		finished = false;
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
		return range.getTimezone();
	}
	
	/**
	 * Sets the timezone.
	 * @param aTimezone the timezone
	 */
	public void setTimezone(String aTimezone) {
		range.setTimezone(aTimezone);
	}
	
	/**
	 * Returns the report language.
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Sets the report language.
	 * @param aLanguage the language
	 */
	public void setLanguage(String aLanguage) {
		this.language = aLanguage;
	}
	
	/**
	 * Returns the report begin time point.
	 * @return the report begin
	 */
	public String getBegin() {
		return range.getBegin();
	}
	
	/**
	 * Sets the report begin time point.
	 * @param aBegin the begin time point
	 */
	public void setBegin(String aBegin) {
		range.setBegin(aBegin);
	}
	
	/**
	 * Returns the end time point for the report.
	 * @return the end time point
	 */
	public String getEnd() {
		return range.getEnd();
	}
	
	/**
	 * Set the end time point for the report.
	 * @param anEnd the end time point
	 */
	public void setEnd(String anEnd) {
		range.setEnd(anEnd);
	}
	
	/**
	 * Checks if the report is finished, i.e. it is fully generated.
	 * @return the finished state of the report
	 */
	public boolean isFinished() {
		return finished;
	}
	
	/**
	 * Sets the finished state of the report.
	 * @param finished the finished state of the report
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
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
	
	/**
	 * Adds a project to the report.
	 * @param aProject a project
	 */
	public void addProject(JsonProject aProject) {
		this.projectList.add(aProject);
	}
	
}