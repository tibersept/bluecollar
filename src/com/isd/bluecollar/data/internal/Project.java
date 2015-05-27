/**
 * 27.05.2015
 */
package com.isd.bluecollar.data.internal;

/**
 * Project data object.
 * @author doan
 */
public class Project implements Comparable<Project> {

	/** The project id*/
	private final long id;
	/** The project name */
	private final String name;
	/** The project description */
	private final String desc;
	/** The timestamp of an active project; set only for active projects */
	private final long timestamp;
	
	/**
	 * Creates a new active project instance.
	 * @param aName the project name
	 * @param aTimestamp begin timestamp for current task in project
	 */
	public Project( final String aName, final long aTimestamp ) {
		id = -1;
		name = aName;
		desc = "";
		timestamp = aTimestamp;
	}
	
	/**
	 * Creates a new project instance.
	 * @param anId the project id
	 * @param aName the project name
	 * @param aDesc the project description
	 */
	public Project( final long anId, final String aName, final String aDesc ) {
		id = anId;
		name = aName;
		desc = aDesc;
		timestamp = -1L;
	}
	
	/**
	 * Returns the project id.
	 * @return the project id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns the project name.
	 * @return the project name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the project description.
	 * @return the project description
	 */
	public String getDesc() {
		return desc;
	}
	
	/**
	 * Returns the begin timestamp of the currently active task of the project.
	 * This value might be <code>-1</code> if the project is not representing the
	 * currently active project.  
	 * @return the begin timestamp of the current task
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	/**
	 * Checks whether the current object carries active project info. 
	 * @return <code>true</code> if object carries active project info
	 */
	public boolean isActive() {
		return id<0;
	}
	
	/**
	 * Checks whether the current object has a begin timestamp. This is 
	 * <code>true</code> only for active project objects.
	 * @return <code>true</code> if the task begin timestamp is set
	 */
	public boolean hasTimestamp() {
		return timestamp>0;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Project o) {
		if( o == null ) {
			return -1;
		}
		return (int)(getId() - o.getId());
	}
	
}
