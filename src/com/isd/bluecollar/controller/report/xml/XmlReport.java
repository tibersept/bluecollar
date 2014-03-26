/**
 * 
 */
package com.isd.bluecollar.controller.report.xml;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.isd.bluecollar.data.report.ProjectTaskTimeRange;
import com.isd.bluecollar.datatype.Range;

/**
 * Generates an XML report.
 * @author doan
 */
public class XmlReport {

	/** The user for whom the report is generated */
	private String user;
	/** The report timezone */
	private TimeZone tz;
	/** The report locale */
	private Locale loc;
	/** The report range */
	private Range<Long> reportRange;
	/** A collection of all report tasks */
	private List<ProjectTaskTimeRange> reportTasks;
}
