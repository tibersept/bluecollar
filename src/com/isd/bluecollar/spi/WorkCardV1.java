package com.isd.bluecollar.spi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

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
	public JsonDate checkin(User aUser) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayStart("bluecollar-default", rightNow);
				
		// Logger.getLogger(getClass().getName()).info("Workday entity:" + workdayEntity);
		
		return new JsonDate(sdf.format(rightNow));
	}
	
	/**
	 * Checks out at the specified time.
	 * @return the time of checkout specified as distance in milliseconds from the EPOCH
	 */
	@ApiMethod(name = "wcard.checkout", httpMethod = "POST")
	public JsonDate checkout(User aUser) {
		Date rightNow = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
		
		WorkTimeData wtd = new WorkTimeData();
		wtd.setDayEnd("bluecollar-default", rightNow);
		
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
}
