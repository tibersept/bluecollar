/**
 * 
 */
package com.isd.bluecollar.controller.report.xml;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.isd.bluecollar.controller.report.ReportGenerator;
import com.isd.bluecollar.data.report.ProjectTimeRange;
import com.isd.bluecollar.datatype.internal.Range;
import com.isd.bluecollar.datatype.json.JsonInputRange;
import com.isd.bluecollar.datatype.json.JsonReport;

/**
 * Generates an XML report.
 * @author doan
 */
public class JsonReportGenerator extends ReportGenerator{
	
	/**
	 * Creates a new JSON report generator.
	 * @param aUser the user
	 * @param aRange the range
	 */
	public JsonReportGenerator( String aUser, JsonInputRange aRange ) {
		super(aUser, aRange);
	}
	/**
	 * @inheritDoc
	 * @see com.isd.bluecollar.controller.report.ReportGenerator#generateReport()
	 */
	@Override
	public Object generateReport() {
		JsonReport report = new JsonReport();
		report.setUser(getUser());
		report.setLanguage(getLang().getLanguage());
		setReportRange(report);
		setReportContent(report);
		return report;
	}
	
	/**
	 * @inheritDoc
	 * @see com.isd.bluecollar.controller.report.ReportGenerator#computeReportName()
	 */
	@Override
	protected String computeReportName() {
		return getLang().reportname;
	}
	
	/**
	 * @inheritDoc
	 * @see com.isd.bluecollar.controller.report.ReportGenerator#initializeMonthNames()
	 */
	@Override
	protected void initializeMonthNames() {
		// do nothing
	}
	
	/**
	 * Sets the project range for the report.
	 * @param aReport a JSON report
	 */
	private void setReportRange( JsonReport aReport ) {
		Calendar cal = getCal();
		cal.setTime(getRange().getBegin());
		aReport.setBegin(String.valueOf(cal.getTimeInMillis()));
		cal.setTime(getRange().getEnd());
		aReport.setEnd(String.valueOf(cal.getTimeInMillis()));
	}
	
	/**
	 * Adds projects and project tasks to the report that fall into the project time range.
	 * @param aReport the report
	 */
	private void setReportContent( JsonReport aReport ) {
		
	}
	
}
