/**
 * 11.08.2013
 */
package com.isd.bluecollar.controller.report;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.isd.bluecollar.controller.report.excel.XlsReport;
import com.isd.bluecollar.data.report.ReportData;
import com.isd.bluecollar.data.store.UserSettings;
import com.isd.bluecollar.datatype.json.JsonInputRange;

/**
 * Report generator provides reporting functionality for mavicollar.
 * @author doan
 */
public class ReportGenerator {

	/** The user */
	private String user;
	/** Generate report name */
	private String reportName;
	/** Begin date of the report */
	private Date begin;
	/** End date of the report */
	private Date end;
	/** Array of string names */
	private String[] monthNames;
	/** The report timezone */
	private TimeZone timezone;	
	/** Calendar set in the user timezone */
	private Calendar cal;
	/** Report language */
	private ReportLanguage lang;
	
	/**
	 * Creates a new report generator.
	 * @param aUser the user/owner of the report
	 * @param aRange the date range for the report 
	 * @param aFormat the date format parser
	 */
	public ReportGenerator( String aUser, JsonInputRange aRange ) {
		setUser(aUser);
		initializeMonthNames();
		initializeLanguage();
		parseRange(aRange);
	}
	
	/**
	 * Generates the EXCEL report in BASE64 encoding.
	 * @return the generated report
	 */
	public String generateReport() {
		XlsReport report = new XlsReport(getLang());
		report.setUser(loadUserName());
		report.setMonthRange(computeMonthRange(false));
		report.setYearRange(computeYearRange(false));
		report.setCompanyName(loadCompanyName());
		report.setReportData(computeReportData());
		return report.generateReport();
	}
	
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
	 * Returns the report begin date.
	 * @return the report begin date
	 */
	public Date getBegin() {
		return begin;
	}

	/**
	 * Sets the report begin date.
	 * @param aBegin the begin date
	 */
	public void setBegin(Date aBegin) {
		this.begin = aBegin;
	}

	/**
	 * Returns the report end date.
	 * @return the report end date
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Sets the report end date.
	 * @param anEnd the report end date
	 */
	public void setEnd(Date anEnd) {
		this.end = anEnd;
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
	private String computeReportName() {
		String title = getLang().reportname;
		String months = computeMonthRange(true);
		String years = computeYearRange(true);
		return title+"-"+months+"-"+years+".xls";
	}
	
	/**
	 * Returns the report language set according to user preferences.
	 * @return the report language
	 */
	private ReportLanguage getLang() {
		return lang;
	}
	
	/**
	 * Loads the company name string selected by the user.
	 * @return the company name
	 */
	private String loadCompanyName() {
		UserSettings wtd = new UserSettings();
		return wtd.getUserSetting(getUser(), "companyName");
	}
	
	/**
	 * Loads the report user name string selected by the user.
	 * @return the report user name
	 */
	private String loadUserName() {
		UserSettings wtd = new UserSettings();
		return wtd.getUserSetting(getUser(), "reportUser");
	}

	/**
	 * Parses the report date range.
	 * @param aRange the range
	 */
	private void parseRange(JsonInputRange aRange) {
		setBegin(aRange.getBeginDate());
		setEnd(aRange.getEndDate());
		setTimezone(aRange.getTimezone());
	}
	
	/**
	 * Returns the day format for reports.
	 * @return the day format for reports
	 */
	private SimpleDateFormat computeDayFormat() {
		SimpleDateFormat format = null;
		if( isSameField(Calendar.YEAR) ) {
			if( isSameField(Calendar.MONTH) ) {
				format = new SimpleDateFormat("dd");
			} else {
				format = new SimpleDateFormat("dd.MM");
			}
		} else {
			format = new SimpleDateFormat("MM/dd/yyyy");
		}
		format.setTimeZone(getTimezone());
		return format;
	}
	
	/**
	 * Computes the year range.
	 * @return the year range as a string
	 */
	private String computeYearRange( boolean aFilenameSafe ) {
		if( isSameField(Calendar.YEAR) ) {
			return getYear(getBegin());
		} else {
			String bYear = getYear(getBegin());
			String eYear = getYear(getEnd());
			String sep = aFilenameSafe ? "-" : " - ";
			return bYear+sep+eYear;
		}
	}
	
	/**
	 * Computes the month range.
	 * @param aFilenameSafe <code>true</code> - the generated names are safe for filenames, i.e. numbers instead of month names
	 * @return the month range as a string
	 */
	private String computeMonthRange( boolean aFilenameSafe ) {
		if( isSameField(Calendar.YEAR) ) {
			if( isSameField(Calendar.MONTH) ) {
				return getMonthName(getBegin(), aFilenameSafe);
			} else {
				String beg = getMonthName(getBegin(), aFilenameSafe);
				String end = getMonthName(getEnd(), aFilenameSafe);
				String sep = aFilenameSafe ? "-": " - "; 
				return beg + sep + end;
			}
		} else {
			String bMonth = getMonthName(getBegin(), aFilenameSafe);
			String bYear = getYear(getBegin());
			String eMonth = getMonthName(getEnd(), aFilenameSafe);
			String eYear = getYear(getEnd());
			String sepMonth = aFilenameSafe ? "." : ". ";
			String sepYear = aFilenameSafe ? "-" : " - ";
			return bMonth+sepMonth+bYear+sepYear+eMonth+sepMonth+eYear;
		}
	}
	
	/**
	 * Computes the report data and return is as a result.
	 * @return the report data
	 */
	private ReportData computeReportData() {
		ReportData rData = new ReportData();
		Calendar cal = getCal();
		SimpleDateFormat format = computeDayFormat();
		WorkhoursExtractor we = new WorkhoursExtractor(rData, cal, format);
		cal.setTime(getBegin());
		while( cal.before(getEnd()) ) {
			we.processDay(getUser());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		we.computeReportEndData(getUser(), getEnd());
		we.clear();
		return rData;
	}

	/**
	 * Checks whether begin and end months are the same.
	 * @param aField the calendar field
	 * @return <code>true</code> if begin and end months are the same
	 */
	private boolean isSameField( int aField ) {
		Calendar cal = getCal();
		cal.setTime(getBegin());
		int begin = cal.get(aField);
		cal.setTime(getEnd());
		int end = cal.get(aField);
		return begin==end;
	}
	
	/**
	 * Returns the year as a string.
	 * @param aDate the date
	 * @return the year
	 */
	private String getYear( Date aDate ) {
		Calendar cal = getCal();
		cal.setTime(aDate);
		return String.valueOf(cal.get(Calendar.YEAR));
	}
	
	/**
	 * Returns the name of the month.
	 * @param aDate the date
	 * @param aNumeric <code>true</code> - returns numeric month name
	 * @return the month name
	 */
	private String getMonthName( Date aDate, boolean aNumeric ) {
		Calendar cal = getCal();
		cal.setTime(aDate);
		int month = cal.get(Calendar.MONTH);
		if( aNumeric ) {
			return String.valueOf(month+1);
		}
		if( month>=0 && month<12 ){
			return monthNames[month];
		}
		return monthNames[0];
	}
	
	/**
	 * Initializes the month names array.
	 */
	private void initializeMonthNames() {
		DateFormatSymbols dfs = new DateFormatSymbols();
		monthNames = dfs.getMonths();
	}
	
	/**
	 * Initializes the language properties of the report.
	 */
	private void initializeLanguage() {
		UserSettings wtd = new UserSettings();
		String lng = wtd.getUserSetting(getUser(), "language");
		lang = new ReportLanguage(lng);
	}
	
	/**
	 * Returns the calendar instance set in the user timezone.
	 * @return the calendar instance
	 */
	private Calendar getCal() {
		if( cal==null ) {
			cal = Calendar.getInstance(getTimezone());
		}
		return cal;
	}
}
