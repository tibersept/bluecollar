/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller.report;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.isd.bluecollar.data.internal.Range;
import com.isd.bluecollar.data.json.JsonRange;
import com.isd.bluecollar.data.store.UserSettingsDP;

/**
 * Report generator provides reporting functionality for mavicollar.
 * @author doan
 */
public abstract class ReportGenerator {

	/** The user */
	private String user;
	/** Generated report name */
	private String reportName;
	/** Range of the report */
	private Range<Date> range;	

	/** The report timezone */
	private TimeZone timezone;	
	/** Report language */
	private ReportLanguage lang;
	
	/**
	 * Creates a new report generator.
	 * @param aUser the user/owner of the report
	 * @param aRange the date range for the report 
	 * @param aFormat the date format parser
	 */
	public ReportGenerator( String aUser, JsonRange aRange ) {
		setUser(aUser);
		initializeLanguage();
		initializeCalendarNames();
		parseRange(aRange);
	}
	
	/**
	 * Generates the EXCEL report in BASE64 encoding.
	 * @return the generated report
	 */
	public abstract Object generateReport();
	
	/**
	 * Returns the report user.
	 * @return the report user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Sets the report user.
	 * @param aUser the report user
	 */
	public void setUser(String aUser) {
		this.user = aUser;
	}

	/**
	 * Returns the report range.
	 * @return the report range
	 */
	public Range<Date> getRange() {
		return range;
	}

	/**
	 * Sets the report range.
	 * @param aRange the range
	 */
	public void setRange(Range<Date> aRange) {
		this.range = aRange;
	}
	
	/**
	 * Returns the begin time point for the report.
	 * @return the begin time point
	 */
	public Date getBegin() {
		return getRange().getBegin();
	}
	
	/**
	 * Returns the end time point for the report.
	 * @return the end time point
	 */
	public Date getEnd() {
		return getRange().getEnd();
	}
	
	/**
	 * Returns the report timezone.
	 * @return the timezone
	 */
	public TimeZone getTimezone() {
		return timezone;
	}
	
	/**
	 * Sets the report timezone.
	 * @param aTimezone the timezone
	 */
	public void setTimezone(String aTimezone) {
		timezone = TimeZone.getTimeZone(aTimezone);
	}
	
	/**
	 * Returns the generated report name.
	 * @return the report name
	 */
	public String getReportName() {
		if( reportName==null ) {
			reportName = computeReportName();
		}
		return reportName;
	}
	
	/**
	 * Sets the report name.
	 * @param the newly generated report name
	 */
	public void setReportName( String aReportName ) {
		reportName = aReportName;
	}
	
	/**
	 * Computes and returns the report name from the current data.
	 * @return the report name
	 */
	protected abstract String computeReportName();
	
	/**
	 * Initializes the month names array.
	 */
	protected abstract void initializeCalendarNames();

	
	/**
	 * Loads the company name string selected by the user.
	 * @return the company name
	 */
	protected String loadCompanyName() {
		UserSettingsDP wtd = new UserSettingsDP();
		return wtd.getUserSetting(getUser(), "companyName");
	}
	
	/**
	 * Loads the report user name string selected by the user.
	 * @return the report user name
	 */
	protected String loadUserName() {
		UserSettingsDP wtd = new UserSettingsDP();
		return wtd.getUserSetting(getUser(), "reportUser");
	}

	/**
	 * Checks if the begin and end dates have the same value in a given field.
	 * @param aField the calendar field
	 * @return <code>true</code> if begin and end months are the same
	 */
	protected boolean isSameField( int aField ) {
		Calendar cal = getCal();
		cal.setTime(getBegin());
		int begin = cal.get(aField);
		cal.setTime(getEnd());
		int end = cal.get(aField);
		return begin==end;
	}
	
	/**
	 * Returns the report language set according to user preferences.
	 * @return the report language
	 */
	protected ReportLanguage getLang() {
		return lang;
	}
	
	/**
	 * Returns a calendar instance set in the user timezone.
	 * @return the calendar instance
	 */
	protected Calendar getCal() {
		return Calendar.getInstance(getTimezone());
	}
	
	/**
	 * Parses the report date range.
	 * @param aRange the range
	 */
	private void parseRange(JsonRange aRange) {
		setRange(new Range<Date>(aRange.getBeginDate(),aRange.getEndDate()));
		setTimezone(aRange.getTimezone());
	}

	/**
	 * Initializes the language properties of the report.
	 */
	private void initializeLanguage() {
		UserSettingsDP wtd = new UserSettingsDP();
		String lng = wtd.getUserSetting(getUser(), "language");
		lang = new ReportLanguage(lng);
	}
		
}
