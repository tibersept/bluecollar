/**
 * 
 */
package com.isd.bluecollar.report;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

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
	 * @param user the report user
	 */
	public void setUser(String user) {
		this.user = user;
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
	 * Parses the report date range.
	 * @param aRange the range
	 */
	private void parseRange(JsonRange aRange) {
		setBegin(aRange.getBeginDate());
		setEnd(aRange.getEndDate());
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
	 * Checks whether begin and end months are the same.
	 * @param aField the calendar field
	 * @return <code>true</code> if begin and end months are the same
	 */
	private boolean isSameField( int aField ) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getBegin());
		int beginMonth = cal.get(aField);
		cal.setTime(getEnd());
		int endMonth = cal.get(aField);
		return beginMonth==endMonth;
	}
	
	/**
	 * Returns the year as a string.
	 * @param aDate the date
	 * @return the year
	 */
	private String getYear( Date aDate ) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(aDate);
		return String.valueOf(cal.get(Calendar.YEAR));
	}
	
	/**
	 * Returns the name of the month.
	 * @param aDate the date
	 * @return the month name
	 */
	private String getMonthName( Date aDate ) {
		Calendar cal = Calendar.getInstance();
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
	
}
