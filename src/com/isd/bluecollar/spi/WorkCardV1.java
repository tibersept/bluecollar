package com.isd.bluecollar.spi;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import com.isd.bluecollar.data.WorkTimeData;
import com.isd.bluecollar.datatype.CurrentUserProject;
import com.isd.bluecollar.datatype.JsonByteArray;
import com.isd.bluecollar.datatype.JsonEasyMap;
import com.isd.bluecollar.datatype.JsonString;
import com.isd.bluecollar.datatype.JsonRange;
import com.isd.bluecollar.datatype.JsonStatus;
import com.isd.bluecollar.report.ReportGenerator;

@Api(
	name = "bluecollar",version = "v2",
	clientIds = {ClientIds.WEB_CLIENT_ID}
)
public class WorkCardV1 {
	
	/**
	 * Checks in at this current time.
	 * @return the time of checking specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkin", httpMethod = "POST")
	public JsonString checkin( @Named("project") String aProject, User aUser ) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		String username = getUserName(aUser);
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayStart(username, aProject, rightNow);

		return new JsonString(String.valueOf(rightNow.getTime()));
	}
	
	/**
	 * Checks out at the specified time.
	 * @return the time of checkout specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkout", httpMethod = "POST")
	public JsonString checkout( @Named("project") String aProject, User aUser ) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();		
		String username = getUserName(aUser);
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayEnd(username, aProject, rightNow);
		
		return new JsonString(String.valueOf(rightNow.getTime()));
	}
	
	/**
	 * Checks for an active user project and returns that one in the status in
	 * case such is encountered.
	 * @param aUser the user
	 * @return the status potentially with the current project.
	 */
	@ApiMethod(name = "wcard.checkactive", httpMethod = "POST")
	public JsonStatus checkActive( User aUser ) {
		String username = getUserName(aUser);
		WorkTimeData wtd = new WorkTimeData();
		CurrentUserProject project = wtd.getActiveProject(username);
		JsonStatus status = new JsonStatus();
		if( project.exists() ) {
			status.setProject(project.getName());
			status.setProjectBegin(String.valueOf(project.getTimestamp()));
		}
		return status;
	}
	
	/**
	 * Generates an EXCEL report and returns the generated report as a byte array. 
	 * @param aRange the range 
	 * @param aTimezone the time zone
	 * @param aUser the user 
	 * @return
	 */
	@ApiMethod(name = "wcard.generatereport", httpMethod = "POST" )
	public JsonByteArray generateReport( JsonRange aRange, User aUser ) {
		ReportGenerator reporter = new ReportGenerator(aRange);
		reporter.setUser(getUserName(aUser));
		if( aRange.validateRange() ) {
			return new JsonByteArray(reporter.generateReport());
		} else {
			return new JsonByteArray();
		}
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
	@ApiMethod( name = "wcard.listprojects", httpMethod = "POST" )
	public List<String> listProjects( User aUser ) {
		String username = getUserName(aUser);
		WorkTimeData wtd = new WorkTimeData();
		List<String> projects = wtd.getProjectList(username,  true);
		return projects;
	}
	
	/**
	 * Retrieves a user setting.
	 * @param aSetting the setting name
	 * @param aUser the user
	 * @return the setting value
	 */
	@ApiMethod( name = "wcard.getusersetting", httpMethod = "POST" )
	public JsonString getUserSetting( @Named("setting") String aSetting, User aUser ) {
		String username = getUserName(aUser);
		WorkTimeData wtd = new WorkTimeData();
		String val = wtd.getUserSetting(username, aSetting);
		return new JsonString(val);
	}
	
	/**
	 * Sets a user setting 
	 * @param aSetting the setting name
	 * @param aValue the setting value
	 * @param aUser the user whose settings are modified
	 */
	@ApiMethod( name = "wcard.setusersetting", httpMethod = "POST" )
	public JsonStatus setUserSetting( @Named("setting") String aSetting,@Named("value") String aValue, User aUser ) {
		String username = getUserName(aUser);
		WorkTimeData wtd = new WorkTimeData();
		wtd.setUserSetting(username, aSetting, aValue);
		return JsonStatus.OK_STATUS;
	}
	
	/**
	 * Returns all the settings of the user.
	 * @param aUser the user
	 * @return a simple JSON map of all user settings
	 */
	@ApiMethod( name = "wcard.getallsettings", httpMethod = "POST" )
	public JsonEasyMap getAllSettings( User aUser ) {
		String username = getUserName(aUser);
		WorkTimeData wtd = new WorkTimeData();
		Map<String, Object> settings = wtd.getUserSettings(username);
		JsonEasyMap map = new JsonEasyMap();
		for( Map.Entry<String,Object> setting : settings.entrySet() ) {
			map.addEntry(setting.getKey(), setting.getValue().toString());
		}
		return map;
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
