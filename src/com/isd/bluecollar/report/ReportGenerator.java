/**
 * 
 */
package com.isd.bluecollar.report;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.isd.bluecollar.data.ReportData;
import com.isd.bluecollar.data.WorkTimeData;
import com.isd.bluecollar.datatype.JsonRange;

/**
 * Report generator provides reporting functionality for mavicollar.
 * @author doan
 */
public class ReportGenerator {

	/** The user */
	private String user;
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
	
	/**
	 * Creates a new report generator.
	 * @param aRange the date range for the report 
	 * @param aFormat the date format parser
	 */
	public ReportGenerator( JsonRange aRange ) {
		initializeMonthNames();		
		parseRange(aRange);
	}
	
	/**
	 * Generates the EXCEL report in BASE64 encoding.
	 * @return the generated report
	 */
	public String generateReport() {
		XlsReport report = new XlsReport();
		report.setUser(getUser());
		report.setMonthRange(computeMonthRange());
		report.setYearRange(computeYearRange());
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
	 * Parses the report date range.
	 * @param aRange the range
	 */
	private void parseRange(JsonRange aRange) {
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
	private String computeYearRange() {
		if( isSameField(Calendar.YEAR) ) {
			return getYear(getBegin());
		} else {
			String bYear = getYear(getBegin());
			String eYear = getYear(getEnd());
			return bYear + " - " + eYear;
		}
	}
	
	/**
	 * Computes the month range.
	 * @return the month range as a string
	 */
	private String computeMonthRange() {
		if( isSameField(Calendar.YEAR) ) {
			if( isSameField(Calendar.MONTH) ) {
				return getMonthName(getBegin());
			} else {
				String beg = getMonthName(getBegin());
				String end = getMonthName(getEnd());
				return beg + " - " + end;
			}
		} else {
			String bMonth = getMonthName(getBegin());
			String bYear = getYear(getBegin());
			String eMonth = getMonthName(getEnd());
			String eYear = getYear(getEnd());
			return bMonth+". " + bYear + " - " + eMonth+". "+eYear;
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
		cal.setTime(getBegin());
		while( cal.before(getEnd()) ) {
			processDay(rData, cal, format);
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		computeReportEndData(rData, cal, format);
		return rData;
	}

	/**
	 * Computes the report end data, ensuring that the last date of the calendar
	 * matches the last day specified as the report end day.
	 * @param aRData the report data
	 * @param aCal the calendar
	 * @param aFormat the format
	 */
	private void computeReportEndData(ReportData aRData, Calendar aCal, SimpleDateFormat aFormat) {
		int lastDay = aCal.get(Calendar.DAY_OF_MONTH);
		long lastTime = aCal.getTimeInMillis();
		aCal.setTime(getEnd());
		int endDay = aCal.get(Calendar.DAY_OF_MONTH);
		long endTime = aCal.getTimeInMillis();
		if( (lastDay!=endDay) && (lastTime<endTime) ) {
			aCal.setTime(getEnd());
			processDay(aRData, aCal, aFormat);
		}
	}

	/**
	 * Process a single day for the report.
	 * @param anRData the report data
	 * @param aCal the calendar
	 * @param aFormat the day format
	 */
	private void processDay(ReportData anRData, Calendar aCal, SimpleDateFormat aFormat) {
		Date day = aCal.getTime();
		String dayString = aFormat.format(day);
		// Day title
		anRData.addDayTitle(dayString);
		// Get user projects for this day
		WorkTimeData wtd = new WorkTimeData();
		
		// What we are trying to achieve
		List<String> projects = wtd.getWorkdayProjects(getUser(), day);
		for( String project : projects ) {
			anRData.setHours(dayString, project, 0.0f);
		}
		
		// Weekend data
		if( isWeekend(aCal) ) {
			anRData.addInvalidDay(dayString);
		}
	}
	
	/**
	 * Checks if the calendar is set to a weekend day.
	 * @return <code>true</code> if calendar is set on a weekend
	 */
	private boolean isWeekend( Calendar aCal ) {
		int dow = aCal.get(Calendar.DAY_OF_WEEK);
		return (dow==Calendar.SUNDAY || dow==Calendar.SATURDAY);
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
	 * @return the month name
	 */
	private String getMonthName( Date aDate ) {
		Calendar cal = getCal();
		cal.setTime(aDate);
		int month = cal.get(Calendar.MONTH);
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
