/**
 * 23.05.2015 
 */
package com.isd.bluecollar.controller.report.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.isd.bluecollar.controller.report.ReportLanguage;
import com.isd.bluecollar.data.internal.Project;
import com.isd.bluecollar.data.report.ReportData;

/**
 * Excel report generator.
 * @author doan
 */
public class XlsReport {
	
	/** Offset relative to day columns for the column containing cumulative hours */
	private final static int OFFSET_HOURS = 1;
	/** Offset relative to day columns for the column containing project numbers */
	private final static int OFFSET_PROJECT_NUMBER = 2;
	/** Offset relative to day columns for the column containing project names */
	private final static int OFFSET_PROJECT_NAME = 3;
	
	/** The owner of the report */
	private String user;
	/** The month range */
	private String monthRange;
	/** The year range */
	private String yearRange;
	/** The company name on the report */
	private String companyName;
	/** The report data - day & project hours */
	private ReportData reportData;
	/** The cell styler */
	private XlsCellStyler styler;
	/** The report language */
	private ReportLanguage lang;
	
	/**
	 * Create a new test class.
	 */
	public XlsReport( ReportLanguage aLang ) {
		lang = aLang;
	}
	
	/**
	 * Returns the user of the report.
	 * @return the report user
	 */
	public String getUser() {
		if( user!=null ) {
			return user;
		}
		return lang.nouser;
	}
	
	/**
	 * Sets the user of the report.
	 * @param aUser the user
	 */
	public void setUser( String aUser ) {
		user = aUser;
	}
		
	/**
	 * Returns the month range as a string.
	 * @return the month range
	 */
	public String getMonthRange() {
		if( monthRange!=null ) {
			return monthRange;
		}
		return lang.nomonth;
	}
	
	/**
	 * Sets the month range.
	 * @param aMonthRange the month range
	 */
	public void setMonthRange( String aMonthRange ) {
		monthRange = aMonthRange;
	}
	
	/**
	 * Returns the year range as a string.
	 * @return the year range
	 */
	public String getYearRange() {
		if( yearRange!=null ) {
			return yearRange;
		}
		return lang.noyear;
	}
	
	/**
	 * Sets the year range as a string.
	 * @param aYearRange the year range
	 */
	public void setYearRange( String aYearRange ) {
		yearRange = aYearRange;
	}
	
	/**
	 * Returns the company name for the report.
	 * @return the company name
	 */
	public String getCompanyName() {
		return companyName;
	}
	
	/**
	 * Sets the company name for the report.
	 * @param aCompanyName the company name
	 */
	public void setCompanyName( String aCompanyName ) {
		companyName = aCompanyName;
	}
	
	/**
	 * Returns the report data.
	 * @return the report data
	 */
	public ReportData getReportData() {
		return reportData;
	}
	
	/**
	 * Sets the report data.
	 * @param aData the report data
	 */
	public void setReportData( ReportData aData ) {
		reportData = aData;
	}
	
	/**
	 * Generates a new report and return the result as a byte stream. If the
	 * input data of the report is invalid then this method returns an empty
	 * string.
	 * @return BASE64 encoded version of the byte array
	 */
	public String generateReport() {
		if( validateInputData() ) {
			Workbook wb = new HSSFWorkbook();
			CreationHelper createHelper = wb.getCreationHelper();
			Sheet sheet = createSheet(wb);
			setStyler(new XlsCellStyler(wb));
			
			createTopRow(createHelper, sheet);
			createTable(createHelper, sheet);
			createFooter(createHelper, sheet);
			
			clearReportData();
			
			return convertToBase64(wb);
		} else {
			return "";
		}
	}

	/**
	 * Validates the report input data.
	 * @return <code>true</code> if input data is valid
	 */
	private boolean validateInputData() {
		return getUser()!=null && getMonthRange()!=null 
				&& getYearRange()!=null && getReportData()!=null; 
	}
	
	/**
	 * Clears the report data.
	 */
	private void clearReportData() {
		getReportData().clear();
	}

	/**
	 * Retrieves the cell styler.
	 * @return the cell styler
	 */
	private XlsCellStyler getStyler() {
		return styler;
	}
	
	/**
	 * Sets the cell styler.
	 * @param aStyler the styler
	 */
	private void setStyler( XlsCellStyler aStyler ) {
		styler = aStyler;
	}
	
	/**
	 * Creates the top row containing user information, and time range information.
	 * @param aCreateHelper the workbook creation helper
	 * @param aSheet the worksheet
	 */
	private void createTopRow( CreationHelper aCreateHelper, Sheet aSheet ) {
		Row row = aSheet.createRow(2);
		Cell cell = row.createCell(0);
		CellStyle infoStyle = getStyler().getStyle(XlsCellStyler.INFO);
		CellStyle inputLeftStyle = getStyler().getStyle(XlsCellStyler.INPUT_LEFT);
		CellStyle inputCenterStyle = getStyler().getStyle(XlsCellStyler.INPUT_CENTER);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.labelname));
		createTextInputField(aSheet, aCreateHelper, inputLeftStyle, row, 2, 4, 11, getUser());
				
		cell = row.createCell(13);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.labelmonth));
		createTextInputField(aSheet, aCreateHelper, inputCenterStyle, row, 2, 17, 24, getMonthRange());
				
		cell = row.createCell(26);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.labelyear));
		createIntegerInputField(aSheet, aCreateHelper, inputCenterStyle, row, 2, 29, 33, getYearRange());
	}
	
	/**
	 * Creates the footer of the worksheet.
	 * @param aCreateHelper the create helper
	 * @param aSheet the worksheet
	 */
	private void createFooter( CreationHelper aCreateHelper, Sheet aSheet ) {
		CellStyle infoStyle = getStyler().getStyle(XlsCellStyler.INFO);
		CellStyle infoSmallStyle = getStyler().getStyle(XlsCellStyler.SMALL_INFO);
		CellStyle inputCenterStyle = getStyler().getStyle(XlsCellStyler.INPUT_CENTER);
		int projectCount = getReportData().getProjectCount();
		
		Row row = aSheet.createRow(10+projectCount);
		Cell cell = row.createCell(0);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.labelovertimecompensation));		
		createTableFooterRowOvertime(aCreateHelper, aSheet, 11+projectCount);
		
		row = aSheet.createRow(12+projectCount);
		cell = row.createCell(0);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.labeltelework));
		createTableFooterRowTelework(aCreateHelper, aSheet, 13+projectCount);
		
		String totalHours = String.valueOf(getReportData().getTotalWorkhours());
		createFooterItem(aCreateHelper, aSheet, infoStyle, inputCenterStyle, 16+projectCount, 8, 14, lang.labeltotalhours, totalHours);
		String requiredHours = String.valueOf(getReportData().getRequiredWorkhours());
		createFooterItem(aCreateHelper, aSheet, infoStyle, inputCenterStyle, 18+projectCount, 8, 14, lang.labelrequiredhours, requiredHours);		
		int rowIndex = 20+projectCount;
		row = createFooterItem(aCreateHelper, aSheet, infoStyle, inputCenterStyle, rowIndex, 8, 14, lang.labelovertime, "");
		
		// signature input field
		createTextInputField(aSheet, aCreateHelper, inputCenterStyle, row, rowIndex, 20, 33, "");		
		rowIndex = 21+projectCount;
		row = aSheet.createRow(rowIndex);
		String signatureFieldTitle = lang.labelpds;
		if( getCompanyName()!=null && getCompanyName().length()>0 ) {
			signatureFieldTitle += " " + lang.labelemployee + " " + getCompanyName();
		}
		createTextInputField(aSheet, aCreateHelper, infoSmallStyle, row, rowIndex, 20, 33, signatureFieldTitle);
	}

	/**
	 * Creates a footer info item.
	 * @param aCreateHelper
	 * @param aSheet
	 * @param anInfoStyle
	 * @param anInputStyleCenter
	 * @param aRowIndex
	 * @param aCellBeg
	 * @param aCellEnd
	 * @param aTitle
	 * @param aData
	 */
	private Row createFooterItem( CreationHelper aCreateHelper, Sheet aSheet,
			CellStyle anInfoStyle, CellStyle anInputStyleCenter, int aRowIndex,
			int aCellBeg, int aCellEnd, String aTitle, String aData ) {				
		Row row = aSheet.createRow(aRowIndex);
		Cell cell = row.createCell(0);
		cell.setCellStyle(anInfoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(aTitle));		
		createTextInputField(aSheet, aCreateHelper, anInputStyleCenter, row, aRowIndex, aCellBeg, aCellEnd, aData);
		return row;
	}

	/**
	 * Creates the main table.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 */
	private void createTable( CreationHelper aCreateHelper, Sheet aSheet ) {
		createTableDayNamesRow(aCreateHelper, aSheet, 4);
		createTableHeaderRow(aCreateHelper, aSheet, 5);
		
		List<Project> projectList = getReportData().getProjects();
		int rowIndex = 6;
		for( Project project : projectList ) {
			String translatedProject = translateProjectName(project.getName());
			createTableRow(aCreateHelper, aSheet, rowIndex, project.getId(), translatedProject);
			rowIndex++;
		}
		
		int projectCount = getReportData().getProjectCount();
		
		createTableDayNamesRow(aCreateHelper, aSheet, projectCount+6);
		createTableFooterRowDayTotals(aCreateHelper, aSheet, projectCount+8);
	}

	/**
	 * Creates the worksheet.
	 * @param aWb the workbook
	 * @return the worksheet
	 */
	private Sheet createSheet( Workbook aWb ) {
		Sheet sheet = aWb.createSheet(getMonthRange() + " " + getYearRange());
		sheet.setDisplayGridlines(false);
		sheet.setDefaultColumnWidth(3);
		sheet.setDefaultRowHeight((short)340);
		int dayCount = getReportData().getDayCount();
		sheet.setColumnWidth(dayCount+OFFSET_PROJECT_NUMBER, 15*256);
		sheet.setColumnWidth(dayCount+OFFSET_PROJECT_NAME, 40*256);
		return sheet;
	}
	
	/**
	 * Creates a row in the main table.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the row index
	 * @param aProjectId the project id
	 * @param aProject the project title
	 */
	private void createTableRow( CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex, long aProjectId, String aProject ) {
		CellStyle cellStyle, floatCellStyle, filledCellStyle;
		if( aRowIndex%2 == 0 ) {
			cellStyle = getStyler().getStyle(XlsCellStyler.CELL);
			floatCellStyle = getStyler().getStyle(XlsCellStyler.CELL_FLOAT);
			filledCellStyle = getStyler().getStyle(XlsCellStyler.CELL_FILLED_FLOAT);
		} else {
			cellStyle = getStyler().getStyle(XlsCellStyler.CELL_GRAYED);
			floatCellStyle = getStyler().getStyle(XlsCellStyler.CELL_GRAYED_FLOAT);
			filledCellStyle = getStyler().getStyle(XlsCellStyler.CELL_FILLED_AND_GRAYED_FLOAT);
		}

		Row row = aSheet.createRow(aRowIndex);
		
		List<String> dayList = getReportData().getDayIndexTitles();
		int cellColumn = 0;
		for( String day : dayList ) {			
			Cell cell = row.createCell(cellColumn);
			if( isInvalidDay(day) ) {
				cell.setCellStyle(filledCellStyle);
			} else {
				cell.setCellStyle(floatCellStyle);
			}
			cell.setCellValue(getReportData().getProjectHoursOnDay(day, aProject));
			cellColumn++;
		}
		
		int dayCount = getReportData().getDayCount();
		
		Cell cell = row.createCell(dayCount+OFFSET_HOURS);
		cell.setCellStyle(floatCellStyle);
		cell.setCellValue(getReportData().getTotalProjectHours(aProject));
		
		cell = row.createCell(dayCount+OFFSET_PROJECT_NUMBER);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(aProjectId);
		
		cell = row.createCell(dayCount+OFFSET_PROJECT_NAME);
		cell.setCellStyle(cellStyle);
		cell.setCellValue(aProject);
	}
	
	/**
	 * Creates a footer row under the main table containing the day totals and the general total workhours.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the row index
	 */
	private void createTableFooterRowDayTotals( CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex ) {
		List<String> dayList = getReportData().getDayIndexTitles();
		List<Float> hours = new ArrayList<Float>(dayList.size());
		float total = 0.0f;
		for( String day : dayList ) {
			float dayHours = getReportData().getTotalDayHours(day);
			hours.add(dayHours);
			total += dayHours;
		}
		createTableFooterRow(aCreateHelper, aSheet, aRowIndex, hours, total);
	}
	
	/**
	 * Creates a footer row under the main table containing the telework compensation hours.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the row index
	 */
	private void createTableFooterRowTelework(  CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex ) {
		List<String> dayList = getReportData().getDayIndexTitles();
		List<Float> hours = new ArrayList<Float>(dayList.size());
		float total = 0.0f;
		for( int i=0; i< dayList.size(); i++ ) {
			hours.add(0.0f);
		}
		createTableFooterRow(aCreateHelper, aSheet, aRowIndex, hours, total);
	}
	
	/**
	 * Creates a footer row under the main table containing the overtime compensation hours.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the row index
	 */
	private void createTableFooterRowOvertime(  CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex ) {
		List<String> dayList = getReportData().getDayIndexTitles();
		List<Float> hours = new ArrayList<Float>(dayList.size());
		float total = 0.0f;
		for( int i=0; i< dayList.size(); i++ ) {
			hours.add(0.0f);
		}
		createTableFooterRow(aCreateHelper, aSheet, aRowIndex, hours, total);
	}
	
	/**
	 * Creates the footer row for the main table.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the row index
	 */
	private void createTableFooterRow( CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex, List<Float> anHoursList, float aTotal ) {
		CellStyle floatCellStyle = getStyler().getStyle(XlsCellStyler.CELL_FLOAT);
		CellStyle filledCellStyle = getStyler().getStyle(XlsCellStyler.CELL_FILLED_FLOAT);
		
		Row row = aSheet.createRow(aRowIndex);
		
		List<String> dayList = getReportData().getDayIndexTitles();
		int cellColumn=0;
		for( String day : dayList ) {			
			Cell cell = row.createCell(cellColumn);
			if( isInvalidDay(day) ) {
				cell.setCellStyle(filledCellStyle);
			} else {
				cell.setCellStyle(floatCellStyle);
			}
			cell.setCellValue(anHoursList.get(cellColumn));
			cellColumn++;
		}
		int dayCount = getReportData().getDayCount();
		
		Cell cell = row.createCell(dayCount+OFFSET_HOURS);
		cell.setCellStyle(floatCellStyle);
		cell.setCellValue(aTotal);
	}

	/**
	 * Creates the header row for the main table.
	 * @param aCreateHelper the workbook create helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the row index
	 */
	private void createTableHeaderRow( CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex ) {
		Row row = aSheet.createRow(aRowIndex);
		CellStyle columnStyle = getStyler().getStyle(XlsCellStyler.CELL);
		CellStyle columnMedium = getStyler().getStyle(XlsCellStyler.CELL_BOLD_BORDER);
		
		List<String> dayList = getReportData().getDayIndexTitles();
		int cellColumn = 0;
		Cell cell = null;
		for( String day : dayList ) {
			cell = row.createCell(cellColumn);
			cell.setCellStyle(columnMedium);
			cell.setCellValue(aCreateHelper.createRichTextString(day));						
			cellColumn++;
		}
		
		int dayCount = getReportData().getDayCount();
		
		cell = row.createCell(dayCount+OFFSET_HOURS);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.columnhours));
				
		cell = row.createCell(dayCount+OFFSET_PROJECT_NUMBER);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.columnnumbers));
		
		cell = row.createCell(dayCount+OFFSET_PROJECT_NAME);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(aCreateHelper.createRichTextString(lang.columnname));
	}
	
	/**
	 * Creates a row containing the abbreviated names of the weekdays.
	 * @param aCreateHelper the Apache POI content creation helper
	 * @param aSheet the worksheet
	 * @param aRowIndex the index of the row which will be created
	 */
	private void createTableDayNamesRow( CreationHelper aCreateHelper, Sheet aSheet, int aRowIndex ) {
		CellStyle style = getStyler().getStyle(XlsCellStyler.CELL_RIGHT_ALIGNED);
		
		Row row = aSheet.createRow(aRowIndex);
		int cellColumn = 0;
		List<String> dayNames = getReportData().getDayNameTitles();
		for( String name : dayNames ) {
			Cell cell = row.createCell(cellColumn);
			cell.setCellStyle(style);
			cell.setCellValue(aCreateHelper.createRichTextString(name));
			cellColumn++;
		}
	}
	
	/**
	 * Checks whether the column maps to a holiday or weekend.
	 * @param aDay the day
	 * @return <code>true</code> if day maps to a holiday or weekend
	 */
	private boolean isInvalidDay( String aDay ) {
		return getReportData().isFreeDay(aDay);
	}

	/**
	 * Creates an input field marking in the provided region.
	 * @param aSheet the worksheet
	 * @param aHelper workbook creation helper 
	 * @param anInputStyle the input style
	 * @param aRow the row
	 * @param aRowIndex the row index
	 * @param aBeg the range begin
	 * @param anEnd the range end 
	 * @param aData the data to be inserted into the input cell range
	 */
	private void createTextInputField( Sheet aSheet, CreationHelper aHelper, CellStyle anInputStyle, Row aRow, int aRowIndex, int aBeg, int anEnd, String aData ) {
		Cell firstCell = aRow.createCell(aBeg);
		firstCell.setCellStyle(anInputStyle);
		firstCell.setCellValue(aHelper.createRichTextString(aData));
		for( int i=(aBeg+1); i<(anEnd+1); i++ ) {
			aRow.createCell(i).setCellStyle(anInputStyle);
		}
		aSheet.addMergedRegion(new CellRangeAddress(aRowIndex, aRowIndex, aBeg, anEnd));				
	}
	
	/**
	 * Creates an input field marking in the provided region.
	 * @param aSheet the worksheet
	 * @param aHelper workbook creation helper 
	 * @param anInputStyle the input style
	 * @param aRow the row
	 * @param aRowIndex the row index
	 * @param aBeg the range begin
	 * @param anEnd the range end 
	 * @param aData the data to be inserted into the input cell range
	 */
	private void createIntegerInputField( Sheet aSheet, CreationHelper aHelper, CellStyle anInputStyle, Row aRow, int aRowIndex, int aBeg, int anEnd, String aData ) {
		Cell firstCell = aRow.createCell(aBeg);		
		firstCell.setCellStyle(anInputStyle);
		try {
			firstCell.setCellValue(Integer.valueOf(aData));
		} catch( NumberFormatException e ) {
			// do nothing
		}
		for( int i=(aBeg+1); i<(anEnd+1); i++ ) {
			aRow.createCell(i).setCellStyle(anInputStyle);
		}
		aSheet.addMergedRegion(new CellRangeAddress(aRowIndex, aRowIndex, aBeg, anEnd));				
	}

	/**
	 * Converts the workbook first to a byte array and then to a BASE64 encoded string.
	 * @param aWb the workbook
	 * @return BASE64 encoded version of the byte array
	 */
	private String convertToBase64( Workbook aWb ) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		String output = "<empty>";
		try {
			aWb.write(bos);

			byte[] content = bos.toByteArray();
			output = Base64.encodeBase64String(content);
		} catch (UnsupportedEncodingException e) {
			// do nothing
		} catch (IOException e) {		
			// do nothing
		}
		return output;
	}
	
	/**
	 * Translates a project string.
	 * @param aProject the project name
	 * @return the translated project name
	 */
	private String translateProjectName( String aProject ) {
		if( aProject!=null ) {
			String project = aProject.toLowerCase();
			if( project.equals("sickness") ) {
				return lang.sickness;
			} else if( project.equals("vacation") ) {
				return lang.vacation;
			}
			return aProject;
		}
		return "";
	}
}
