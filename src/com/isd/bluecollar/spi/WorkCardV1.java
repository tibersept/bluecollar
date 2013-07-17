package com.isd.bluecollar.spi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import com.isd.bluecollar.data.WorkTimeData;
import com.isd.bluecollar.datatype.JsonByteArray;
import com.isd.bluecollar.datatype.JsonDate;
import com.isd.bluecollar.datatype.JsonRange;
import com.isd.bluecollar.report.ReportGenerator;

@Api(
	name = "bluecollar",version = "v1",
	clientIds = {ClientIds.WEB_CLIENT_ID}
)
public class WorkCardV1 {

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * Checks in at this current time.
	 * @return the time of checking specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkin", httpMethod = "POST")
	public JsonDate checkin(@Named("project") String aProject, User aUser) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String username = getUserName(aUser);
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayStart(username, aProject, rightNow);

		return new JsonDate(sdf.format(rightNow));
	}
	
	/**
	 * Checks out at the specified time.
	 * @return the time of checkout specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkout", httpMethod = "POST")
	public JsonDate checkout(@Named("project") String aProject, User aUser) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String username = getUserName(aUser);
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayEnd(username, aProject, rightNow);
		
		return new JsonDate(sdf.format(rightNow));
	}		
	
	/**
	 * Generates an EXCEL report and returns the generated report as a byte array. 
	 * @param aRange the range 
	 * @param aUser the user 
	 * @return
	 */
	@ApiMethod(name = "wcard.generatereport", httpMethod = "POST" )
	public JsonByteArray generateReport(JsonRange aRange, User aUser ) {
		ReportGenerator reporter = new ReportGenerator(aRange,DATE_FORMAT);
		reporter.setUser(getUserName(aUser));
		return new JsonByteArray(reporter.generateReport());
	}
	
	/**
	 * Adds a project to the project list of the user.
	 * @param aName the project name
	 * @param aDescription the project description
	 * @param aUser the user
	 * @return <code>true</code> on success
	 */
	@ApiMethod(name = "wcard.addproject", httpMethod = "POST" )
	public List<String> addProject( @Named("name") String aName, @Named("description") String aDescription, User aUser ) {
		String username = getUserName(aUser);
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.addProject(username, aName, aDescription);
		
		List<String> projects = wtd.getProjectList(username, true);		
		return projects;
	}
	
	/**
	 * Returns the list of projects that belong to this user. 
	 * @param aUser the user
	 * @return the list of projects
	 */
	@ApiMethod(name = "wcard.listprojects", httpMethod = "POST" )
	public List<String> listProjects( User aUser ) {
		String username = getUserName(aUser);
		WorkTimeData wtd = new WorkTimeData();
		List<String> projects = wtd.getProjectList(username,  true);
		return projects;
	}
	
	/**
	 * Attempts to retrieve the username from the user object. If it fails to do 
	 * so, then a default username is retrieved. 
	 * @param aUser the user object
	 * @return the username
	 */
	private String getUserName( User aUser ) {
		if( aUser!=null ) {
			return aUser.getNickname();
		} else {
			return "bluecollar-default";
		}
	}
}
