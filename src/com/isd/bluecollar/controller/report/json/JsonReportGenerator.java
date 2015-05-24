/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller.report.json;

import java.util.Calendar;
import java.util.List;

import com.isd.bluecollar.controller.report.ReportGenerator;
import com.isd.bluecollar.data.internal.Range;
import com.isd.bluecollar.data.json.JsonProject;
import com.isd.bluecollar.data.json.JsonRange;
import com.isd.bluecollar.data.json.JsonReport;
import com.isd.bluecollar.data.store.Project;
import com.isd.bluecollar.data.store.TimeRange;

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
	public JsonReportGenerator( String aUser, JsonRange aRange ) {
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
		Project projectEntity = new Project();
		List<String> projects = projectEntity.getProjects(getUser(), true);
		
		TimeRange range = new TimeRange();
		for( String p : projects ) {
			List<Range<Long>> timeRanges = range.getRanges(getUser(), p, getBegin(), getEnd());
			if( timeRanges.size() > 0 ) {
				JsonProject jsonProject = new JsonProject(p);
				for( Range<Long> timeRange : timeRanges ) {
					JsonRange jsonRange = new JsonRange();
					jsonRange.setBegin(String.valueOf(timeRange.getBegin()));
					jsonRange.setEnd(String.valueOf(timeRange.getEnd()));
					jsonProject.addRange(jsonRange);
				}
				aReport.addProject(jsonProject);
			}
		}
	}
	
}
