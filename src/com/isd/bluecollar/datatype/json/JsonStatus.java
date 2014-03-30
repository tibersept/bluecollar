/**
 * 04.08.2013
 */
package com.isd.bluecollar.datatype.json;

/**
 * JSON status string.
 * @author doan
 */
public class JsonStatus {
	
	/** ok string */
	public static final String OK_STRING = "ok";
	/** error string */
	public static final String ERROR_STRING = "err";
	/** ok status */
	public static final JsonStatus OK_STATUS;
	/** error status */
	public static final JsonStatus ERROR_STATUS;
	
	static {
		OK_STATUS = new JsonStatus();
		OK_STATUS.setStatus(OK_STRING);
		ERROR_STATUS = new JsonStatus();
		ERROR_STATUS.setStatus(ERROR_STRING);
	}
	
	/** Status string */
	private String status;
	/** The active project */ 
	private String project;
	/** Date timestamp for begin of project (in UTC) */
	private String projectBegin;
	
	/**
	 * Creates a status instance.
	 */
	public JsonStatus() {
		status = OK_STRING;
		project = "";
		projectBegin = "";
	}
	
	/**
	 * Returns the status.
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 * @param aStatus the status
	 */
	public void setStatus( String aStatus ) {
		status = aStatus;
	}
	
	/**
	 * Returns the project begin timestamp if such has been defined.
	 * Otherwise this method returns an empty string.
	 * @return the project begin timestamp
	 */
	public String getProjectBegin() {
		return projectBegin;
	}
	
	/**
	 * Sets the project begin.
	 * @param aBegin the project begin
	 */
	public void setProjectBegin( String aBegin ) {
		projectBegin = aBegin;
	}
	
	/**
	 * Returns the project name, if such is defined. Otherwise the
	 * method returns an empty string.
	 * @return the project name
	 */
	public String getProject() {
		return project;
	}
	
	/**
	 * Sets the project name.
	 * @param aProject the project name
	 */
	public void setProject( String aProject ) {
		project = aProject;
	}
	
	
}
