/**
 * 07.08.2013
 */
package com.isd.bluecollar.datatype;

/**
 * Wrapper for the active project.
 * @author doan
 */
public class ActiveProject {

	/** The name of the project */
	private String name;
	/** The begin timestamp of the project */
	private Long timestamp;
	
	/**
	 * Creates an non-existent active project.
	 */
	public ActiveProject() {
		// do nothing
	}
	
	/**
	 * Creates an active project with the given name and timestamp.
	 * @param aName the project name
	 * @param aTimestamp the project begin timestamp
	 */
	public ActiveProject( String aName, Long aTimestamp ) {
		name = aName;
		timestamp = aTimestamp;
	}
	
	/**
	 * Returns the project name.
	 * @return the project name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the project name.
	 * @param aName the project name
	 */
	public void setName(String aName) {
		name = aName;
	}
	
	/**
	 * Returns the begin timestamp of the project.
	 * @return the begin timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Sets the begin timestamp of the project.
	 * @param aTimestamp the begin timestamp
	 */
	public void setTimestamp(Long aTimestamp) {
		timestamp = aTimestamp;
	}
	
	/**
	 * Checks whether the current user project exists.
	 * @return <code>true</code> if project exists
	 */
	public boolean exists() {
		return name!=null && timestamp!=null && name.length()>0 && timestamp.longValue()>0;
	}
	
	
}
