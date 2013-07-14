package com.isd.bluecollar.spi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.appengine.api.users.User;
import com.isd.bluecollar.data.WorkTimeData;
import com.isd.bluecollar.datatype.JsonDate;
import com.isd.bluecollar.datatype.JsonRange;
import com.isd.bluecollar.datatype.Range;

@Api(
	name = "bluecollar",version = "v1",
	clientIds = {ClientIds.WEB_CLIENT_ID}
)
public class WorkCardV1 {

	/**
	 * Checks in at this current time.
	 * @return the time of checking specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkin", httpMethod = "POST")
	public JsonDate checkin(@Named("project") String aProject, User aUser) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		String username = getUserName(aUser);
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayEnd(username, aProject, rightNow);
		
		return new JsonDate(sdf.format(rightNow));
	}
	
	/**
	 * Lists all workcard data that falls into a given range.
	 * @return the workcard range data
	 */
	@ApiMethod(name = "wcard.list", httpMethod = "POST" )
	public JsonRange list(JsonDate aDate, User aUser) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		
		Logger.getLogger(getClass().getName()).info("Received date:" + aDate.getDate());
		
		WorkTimeData wtd = new WorkTimeData();
		Range<Date> range = wtd.getRangeForDay("bluecollar-default", rightNow);
		
		return new JsonRange(sdf.format(range.getBegin()), sdf.format(range.getEnd()));
	}
	
	/**
	 * Adds a project to the project list of the user.
	 * @param aName the project name
	 * @param aDescription the project description
	 * @param aUser the user
	 * @return <code>true</code> on success
	 */
	@ApiMethod(name = "wcard.addproject", httpMethod = "POST" )
	public List<String> addProjects( @Named("name") String aName, @Named("description") String aDescription, User aUser ) {
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
