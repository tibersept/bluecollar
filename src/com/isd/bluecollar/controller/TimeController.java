/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller;

import java.util.Calendar;
import java.util.TimeZone;

import com.isd.bluecollar.data.internal.Project;
import com.isd.bluecollar.data.store.ProjectDP;
import com.isd.bluecollar.data.store.TimeRangeDP;
import com.isd.bluecollar.data.store.UserDP;

/**
 * Time card controller.
 * @author doan
 */
public class TimeController {

	/**
	 * Checks in a user, starting a task for the project.
	 * @param aUser the user
	 * @param aProject the project
	 * @return the timestamp of the task begin
	 */
	public String checkin( String aUser, String aProject ) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		long checkinTime = cal.getTimeInMillis();
		
		TimeRangeDP tr = new TimeRangeDP(new ProjectDP());
		tr.openRange(aUser, aProject, checkinTime);
		
		UserDP usr = new UserDP();
		usr.setCurrentProject(aUser, aProject);
		usr.setTaskBegin(aUser, checkinTime);
		
		return String.valueOf(checkinTime);
	}
	
	/**
	 * Checks out a user, stopping a task for the project.
	 * @param aUser the user
	 * @param aProject the project
	 * @return the timestamp of the task end
	 */
	public String checkout( String aUser, String aProject ) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		long checkoutTime = cal.getTimeInMillis();
		
		TimeRangeDP tr = new TimeRangeDP(new ProjectDP());
		tr.closeRange(aUser, aProject, checkoutTime);
		
		UserDP usr = new UserDP();
		usr.setCurrentProject(aUser, null);
		usr.setTaskBegin(aUser, 0);

		return String.valueOf(checkoutTime);
	}
	
	/**
	 * Checks for an active project of the user. If the user has an active project
	 * the return active project object will contain valid values. Otherwise it will
	 * deliver empty values.
	 * @param aUser the user
	 * @return the active project
	 */
	public Project checkActive( String aUser ) {
		UserDP usr = new UserDP();
		String project = usr.getCurrentProject(aUser);
		long begin = usr.getTaskBegin(aUser);
		return new Project(project, begin);
	}
	
}
