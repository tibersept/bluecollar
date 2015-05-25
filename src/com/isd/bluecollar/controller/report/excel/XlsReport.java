/**
 * 23.05.2015 
 */
package com.isd.bluecollar.controller.report.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
	public void setUser(String aUser) {
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
	public void setMonthRange(String aMonthRange) {
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
	public void setYearRange(String aYearRange) {
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
	public void setCompanyName(String aCompanyName) {
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
	private void setStyler(XlsCellStyler aStyler) {
		styler = aStyler;
	}
	
	/**
	 * Creates the top row containing user information, and time range information.
	 * @param aCreateHelper the workbook creation helper
	 * @param aSheet the worksheet
	 */
	private void createTopRow(CreationHelper aCreateHelper, Sheet aSheet) {
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
	 * @param createHelper the create helper
	 * @param sheet the worksheet
	 */
	private void createFooter( CreationHelper createHelper, Sheet sheet ) {
		CellStyle infoStyle = getStyler().getStyle(XlsCellStyler.INFO);
		CellStyle infoSmallStyle = getStyler().getStyle(XlsCellStyler.SMALL_INFO);
		CellStyle inputCenterStyle = getStyler().getStyle(XlsCellStyler.INPUT_CENTER);
		int projectCount = getReportData().getProjectCount();
		
		Row row = sheet.createRow(10+projectCount);
		Cell cell = row.createCell(0);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(createHelper.createRichTextString(lang.labelovertimecompensation));		
		createTableFooterRow(createHelper, sheet, 11+projectCount);
		
		row = sheet.createRow(12+projectCount);
		cell = row.createCell(0);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(createHelper.createRichTextString(lang.labeltelework));
		createTableFooterRow(createHelper, sheet, 13+projectCount);
		
		createFooterItem(createHelper, sheet, infoStyle, inputCenterStyle, 16+projectCount, 8, 14, lang.labeltotalhours);
		createFooterItem(createHelper, sheet, infoStyle, inputCenterStyle, 18+projectCount, 8, 14, lang.labelrequiredhours);
		
		int rowIndex = 20+projectCount;
		row = createFooterItem(createHelper, sheet, infoStyle, inputCenterStyle, rowIndex, 8, 14, lang.labelovertime);		
		createTextInputField(sheet, createHelper, inputCenterStyle, row, rowIndex, 20, 33, "");
		
		rowIndex = 21+projectCount;
		row = sheet.createRow(rowIndex);
		String signatureFieldTitle = lang.labelpds;
		if( getCompanyName()!=null && getCompanyName().length()>0 ) {
			signatureFieldTitle += " " + lang.labelemployee + " " + getCompanyName();
		}
		createTextInputField(sheet, createHelper, infoSmallStyle, row, rowIndex, 20, 33, signatureFieldTitle);
	}

	/**
	 * Creates a footer info item.
	 * @param createHelper
	 * @param sheet
	 * @param infoStyle
	 * @param inputStyleCenter
	 * @param rowIndex
	 * @param cellBeg
	 * @param cellEnd
	 * @param title
	 */
	private Row createFooterItem(CreationHelper createHelper, Sheet sheet,
			CellStyle infoStyle, CellStyle inputStyleCenter, int rowIndex,
			int cellBeg, int cellEnd, String title) {				
		Row row = sheet.createRow(rowIndex);
		Cell cell = row.createCell(0);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(createHelper.createRichTextString(title));		
		createTextInputField(sheet, createHelper, inputStyleCenter, row, rowIndex, cellBeg, cellEnd, "");
		return row;
	}

	/**
	 * Creates the main table.
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 */
	private void createTable(CreationHelper createHelper, Sheet sheet) {
		createTableDayNamesRow(createHelper, sheet, 4);
		createTableHeaderRow(createHelper, sheet, 5);
		
		List<String> projectList = getReportData().getProjectTitles();
		int rowIndex = 6;
		for( String project : projectList ) {
			String translatedProject = translateProjectName(project);
			createTableRow(createHelper, sheet, rowIndex, translatedProject);
			rowIndex++;
		}
		
		int projectCount = getReportData().getProjectCount();
		
		createTableDayNamesRow(createHelper, sheet, projectCount+6);
		createTableFooterRow(createHelper, sheet, projectCount+8);
	}

	/**
	 * Creates the worksheet.
	 * @param wb the workbook
	 * @return the worksheet
	 */
	private Sheet createSheet(Workbook wb) {
		Sheet sheet = wb.createSheet(getMonthRange() + " " + getYearRange());
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
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 * @param rowIndex the row index
	 * @param title the project title
	 */
	private void createTableRow(CreationHelper createHelper, Sheet sheet, int rowIndex, String title) {
		CellStyle columnStyle, filledColumnStyle;
		if( rowIndex%2 == 0 ) {
			columnStyle = getStyler().getStyle(XlsCellStyler.COLUMN);
			filledColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_FILLED);
		} else {
			columnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_GRAYED);
			filledColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_FILLED_AND_GRAYED);
		}

		Row row = sheet.createRow(rowIndex);
		
		List<String> dayList = getReportData().getDayIndexTitles();
		int cellColumn = 0;
		for( String day : dayList ) {			
			Cell cell = row.createCell(cellColumn);
			if( isInvalidDay(day) ) {
				cell.setCellStyle(filledColumnStyle);
			} else {
				cell.setCellStyle(columnStyle);
			}
			cell.setCellValue(getReportData().getHours(day, title));
			cellColumn++;
		}
		
		int dayCount = getReportData().getDayCount();
		
		Cell cell = row.createCell(dayCount+OFFSET_HOURS);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(0.0);
		
		cell = row.createCell(dayCount+OFFSET_PROJECT_NUMBER);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(Math.abs(title.hashCode()%1000));
		
		cell = row.createCell(dayCount+OFFSET_PROJECT_NAME);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(title);
	}
	
	/**
	 * Creates the footer row for the main table.
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 * @param rowIndex the row index
	 */
	private void createTableFooterRow(CreationHelper createHelper, Sheet sheet, int rowIndex) {
		CellStyle columnStyle = getStyler().getStyle(XlsCellStyler.COLUMN);
		CellStyle filledColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_FILLED);
		
		Row row = sheet.createRow(rowIndex);
		
		List<String> dayList = getReportData().getDayIndexTitles();
		int cellColumn=0;
		for( String day : dayList ) {			
			Cell cell = row.createCell(cellColumn);
			if( isInvalidDay(day) ) {
				cell.setCellStyle(filledColumnStyle);
			} else {
				cell.setCellStyle(columnStyle);
			}
			cellColumn++;
		}
		
		int dayCount = getReportData().getDayCount();
		
		Cell cell = row.createCell(dayCount+OFFSET_HOURS);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(0.0);
	}

	/**
	 * Creates the header row for the main table.
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 * @param rowIndex the row index
	 */
	private void createTableHeaderRow(CreationHelper createHelper, Sheet sheet, int rowIndex) {
		Row row = sheet.createRow(rowIndex);
		CellStyle columnStyle = getStyler().getStyle(XlsCellStyler.COLUMN);
		CellStyle columnMedium = getStyler().getStyle(XlsCellStyler.COLUMN_MEDIUM);
		CellStyle columnEndMedium = getStyler().getStyle(XlsCellStyler.COLUMN_END_MEDIUM);
		
		List<String> dayList = getReportData().getDayIndexTitles();
		int cellColumn = 0;
		Cell cell = null;
		for( String day : dayList ) {
			cell = row.createCell(cellColumn);
			cell.setCellStyle(columnMedium);
			cell.setCellValue(createHelper.createRichTextString(day));						
			cellColumn++;
		}
		if( cell!=null ) {
			cell.setCellStyle(columnEndMedium);
		}
		
		int dayCount = getReportData().getDayCount();
		
		cell = row.createCell(dayCount+OFFSET_HOURS);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(createHelper.createRichTextString(lang.columnhours));
				
		cell = row.createCell(dayCount+OFFSET_PROJECT_NUMBER);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(createHelper.createRichTextString(lang.columnnumbers));
		
		cell = row.createCell(dayCount+OFFSET_PROJECT_NAME);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(createHelper.createRichTextString(lang.columnname));
	}
	
	/**
	 * Creates a row containing the abbreviated names of the weekdays.
	 * @param createHelper the Apache POI content creation helper
	 * @param sheet the worksheet
	 * @param rowIndex the index of the row which will be created
	 */
	private void createTableDayNamesRow(CreationHelper createHelper, Sheet sheet, int rowIndex) {
		CellStyle style = getStyler().getStyle(XlsCellStyler.CELL_RIGHT_ALIGNED);
		
		Row row = sheet.createRow(rowIndex);
		int cellColumn = 0;
		List<String> dayNames = getReportData().getDayNameTitles();
		for( String name : dayNames ) {
			Cell cell = row.createCell(cellColumn);
			cell.setCellStyle(style);
			cell.setCellValue(createHelper.createRichTextString(name));
			cellColumn++;
		}
	}
	
	/**
	 * Checks whether the column maps to a holiday or weekend.
	 * @param aDay the day
	 * @return <code>true</code> if day maps to a holiday or weekend
	 */
	private boolean isInvalidDay( String aDay ) {
		return getReportData().isInvalidDay(aDay);
	}

	/**
	 * Creates an input field marking in the provided region.
	 * @param sheet the worksheet
	 * @param helper workbook creation helper 
	 * @param inputStyle the input style
	 * @param row the row
	 * @param rowIndex the row index
	 * @param beg the range begin
	 * @param end the range end 
	 * @param data the data to be inserted into the input cell range
	 */
	private void createTextInputField(Sheet sheet, CreationHelper helper, CellStyle inputStyle, Row row, int rowIndex, int beg, int end, String data) {
		Cell firstCell = row.createCell(beg);
		firstCell.setCellStyle(inputStyle);
		firstCell.setCellValue(helper.createRichTextString(data));
		for( int i=(beg+1); i<(end+1); i++ ) {
			row.createCell(i).setCellStyle(inputStyle);
		}
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, beg, end));				
	}
	
	/**
	 * Creates an input field marking in the provided region.
	 * @param sheet the worksheet
	 * @param helper workbook creation helper 
	 * @param inputStyle the input style
	 * @param row the row
	 * @param rowIndex the row index
	 * @param beg the range begin
	 * @param end the range end 
	 * @param data the data to be inserted into the input cell range
	 */
	private void createIntegerInputField(Sheet sheet, CreationHelper helper, CellStyle inputStyle, Row row, int rowIndex, int beg, int end, String data) {
		Cell firstCell = row.createCell(beg);		
		firstCell.setCellStyle(inputStyle);
		try {
			firstCell.setCellValue(Integer.valueOf(data));
		} catch( NumberFormatException e ) {
			// do nothing
		}
		for( int i=(beg+1); i<(end+1); i++ ) {
			row.createCell(i).setCellStyle(inputStyle);
		}
		sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, beg, end));				
	}

	/**
	 * Converts the workbook first to a byte array and then to a BASE64 encoded string.
	 * @param aWb the workbook
	 * @return BASE64 encoded version of the byte array
	 */
	private String convertToBase64(Workbook aWb) {
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
