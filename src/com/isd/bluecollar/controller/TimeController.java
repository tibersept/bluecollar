/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.isd.bluecollar.data.internal.ActiveProject;
import com.isd.bluecollar.data.store.TimeRange;
import com.isd.bluecollar.data.store.User;

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
		Date rightNow = cal.getTime();
		
		TimeRange tr = new TimeRange();
		tr.openTimeRange(aUser, aProject, rightNow);
		
		User usr = new User();
		usr.setCurrentProject(aUser, aProject);
		usr.setTaskBegin(aUser, rightNow.getTime());
		
		return String.valueOf(rightNow.getTime());
	}
	
	/**
	 * Checks out a user, stopping a task for the project.
	 * @param aUser the user
	 * @param aProject the project
	 * @return the timestamp of the task end
	 */
	public String checkout( String aUser, String aProject ) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		
		TimeRange tr = new TimeRange();
		tr.closeTimeRange(aUser, aProject, rightNow);
		
		User usr = new User();
		usr.setCurrentProject(aUser, null);
		usr.setTaskBegin(aUser, 0);

		return String.valueOf(rightNow.getTime());
	}
	
	/**
	 * Checks for an active project of the user. If the user has an active project
	 * the return active project object will contain valid values. Otherwise it will
	 * deliver empty values.
	 * @param aUser the user
	 * @return the active project
	 */
	public ActiveProject checkActive( String aUser ) {
		User usr = new User();
		String project = usr.getCurrentProject(aUser);
		long begin = usr.getTaskBegin(aUser);
		return new ActiveProject(project, begin);
	}
	
}
