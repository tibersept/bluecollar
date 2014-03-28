package com.isd.bluecollar.spi;

import java.util.Map;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import com.isd.bluecollar.controller.ProjectController;
import com.isd.bluecollar.controller.SettingsController;
import com.isd.bluecollar.controller.TimeController;
import com.isd.bluecollar.controller.report.ReportGenerator;
import com.isd.bluecollar.datatype.internal.ActiveProject;
import com.isd.bluecollar.datatype.json.JsonByteArray;
import com.isd.bluecollar.datatype.json.JsonEasyMap;
import com.isd.bluecollar.datatype.json.JsonInputRange;
import com.isd.bluecollar.datatype.json.JsonList;
import com.isd.bluecollar.datatype.json.JsonStatus;
import com.isd.bluecollar.datatype.json.JsonString;

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
		String username = getUserName(aUser);
		TimeController card = new TimeController();
		return new JsonString(card.checkin(username, aProject));
	}
	
	/**
	 * Checks out at the specified time.
	 * @return the time of checkout specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkout", httpMethod = "POST")
	public JsonString checkout( @Named("project") String aProject, User aUser ) {
		String username = getUserName(aUser);
		TimeController card = new TimeController();
		return new JsonString(card.checkout(username, aProject));
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
		TimeController card = new TimeController();
		ActiveProject project = card.checkActive(username);
		
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
	public JsonByteArray generateReport( JsonInputRange aRange, User aUser ) {
		ReportGenerator reporter = new ReportGenerator(getUserName(aUser),aRange);
		if( aRange.validateRange() ) {
			return new JsonByteArray(reporter.getReportName(), reporter.generateReport());
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
	public JsonList addProject( @Named("name") String aName, @Named("description") String aDescription, User aUser ) {
		String username = getUserName(aUser);
		ProjectController pc = new ProjectController();		
		return new JsonList(pc.addProject(username, aName, aDescription));
	}
	
	/**
	 * Returns the list of projects that belong to this user. 
	 * @param aUser the user
	 * @return the list of projects
	 */
	@ApiMethod( name = "wcard.listprojects", httpMethod = "POST" )
	public JsonList listProjects( User aUser ) {
		String username = getUserName(aUser);
		ProjectController pc = new ProjectController();
		return new JsonList(pc.getProjects(username));
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
		SettingsController sc = new SettingsController();
		return new JsonString(sc.getSetting(username, aSetting));
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
		SettingsController sc = new SettingsController();		
		sc.setSetting(username, aSetting, aValue);
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
		SettingsController sc = new SettingsController();
		Map<String,Object> settings = sc.getAllSettings(username);
		JsonEasyMap map = new JsonEasyMap();
		for( Map.Entry<String,Object> setting : settings.entrySet() ) {
			map.addEntry(setting.getKey(), String.valueOf(setting.getValue()));
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
