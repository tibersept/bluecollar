/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller.report.excel;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.isd.bluecollar.controller.report.ReportGenerator;
import com.isd.bluecollar.data.report.ReportData;
import com.isd.bluecollar.datatype.json.JsonRange;

/**
 * Excel report generator.
 * @author doan
 */
public class XlsReportGenerator extends ReportGenerator {

	/** Array of string names */
	private String[] monthNames;
	
	/**
	 * Creates a new excel report generator.
	 * @param aUser the user
	 * @param aRange the report range
	 */
	public XlsReportGenerator(String aUser, JsonRange aRange) {
		super(aUser, aRange);
	}
	
	/**
	 * Generates the EXCEL report in BASE64 encoding.
	 * @return the generated report
	 */
	@Override
	public Object generateReport() {
		XlsReport report = new XlsReport(getLang());
		report.setUser(loadUserName());
		report.setMonthRange(computeMonthRange(false));
		report.setYearRange(computeYearRange(false));
		report.setCompanyName(loadCompanyName());
		report.setReportData(computeReportData());
		return report.generateReport();
	}
	
	/**
	 * Computes and returns the report name from the current data.
	 * @return the report name
	 */
	@Override
	protected String computeReportName() {
		String title = getLang().reportname;
		String months = computeMonthRange(true);
		String years = computeYearRange(true);
		return title+"-"+months+"-"+years+".xls";
	}
	
	/**
	 * Initializes the month names array.
	 */
	@Override
	protected void initializeMonthNames() {
		DateFormatSymbols dfs = new DateFormatSymbols();
		monthNames = dfs.getMonths();
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
	
	
}
