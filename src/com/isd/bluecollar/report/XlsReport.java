package com.isd.bluecollar.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * Excel report generator.
 * @author isakov
 */
public class XlsReport {
	
	/** Missing user constant */
	private static final String NO_USER = "missing user";
	/** Missing month constant */
	private static final String NO_MONTH = "missing month";
	/** Missing year constant */
	private static final String NO_YEAR = "missing year";
	
	/** List of invalid columns when generating a report. */
	private HashSet<Integer> invalidColumns;
	/** The owner of the report */
	private String user;
	/** The month range */
	private String monthRange;
	/** The year range */
	private String yearRange;
	/** The cell styler */
	private XlsCellStyler styler;
	
	/**
	 * Create a new test class.
	 */
	public XlsReport() {
		invalidColumns = new HashSet<Integer>();
	}
	
	/**
	 * Returns the user of the report.
	 * @return the report user
	 */
	public String getUser() {
		if( user!=null ) {
			return user;
		}
		return NO_USER;
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
		return NO_MONTH;
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
		return NO_YEAR;
	}
	
	/**
	 * Sets the year range as a string.
	 * @param aYearRange the year range
	 */
	public void setYearRange(String aYearRange) {
		yearRange = aYearRange;
	}

	/**
	 * Generates a new report and return the result as a byte stream.
	 * @return BASE64 encoded version of the byte array
	 */
	public String generateReport() {		
		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = createSheet(wb);
		setStyler(new XlsCellStyler(wb));
		createTopRow(createHelper, sheet);
		
		int beg = 1;
		int end = 31;
		int projectCount = 4;
		
		markInvalidColumns(beg,end);
		createTable(createHelper, sheet, beg, end, projectCount);		
		createFooter(createHelper, sheet, beg, end, projectCount);
		
		return convertToBase64(wb); 
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
		cell.setCellValue(aCreateHelper.createRichTextString("Name:"));
		createTextInputField(aSheet, aCreateHelper, inputLeftStyle, row, 2, 4, 11, getUser());
				
		cell = row.createCell(13);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString("Monat:"));
		createTextInputField(aSheet, aCreateHelper, inputCenterStyle, row, 2, 17, 24, getMonthRange());
				
		cell = row.createCell(26);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(aCreateHelper.createRichTextString("Jahr:"));
		createIntegerInputField(aSheet, aCreateHelper, inputCenterStyle, row, 2, 29, 33, getYearRange());
	}
	
	/**
	 * Creates the footer of the worksheet.
	 * @param createHelper the create helper
	 * @param sheet
	 * @param infoStyle
	 * @param infoSmallStyle
	 * @param inputStyleCenter
	 * @param columnStyle
	 * @param filledColumnStyle
	 * @param beg
	 * @param end
	 * @param projectCount
	 */
	private void createFooter(CreationHelper createHelper, Sheet sheet, int beg, int end, int projectCount) {
		CellStyle infoStyle = getStyler().getStyle(XlsCellStyler.INFO);
		CellStyle infoSmallStyle = getStyler().getStyle(XlsCellStyler.SMALL_INFO);
		CellStyle inputCenterStyle = getStyler().getStyle(XlsCellStyler.INPUT_CENTER);
		CellStyle columnStyle = getStyler().getStyle(XlsCellStyler.COLUMN);
		CellStyle filledColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_FILLED);
		Row row = sheet.createRow(12+projectCount);
		Cell cell = row.createCell(0);
		cell.setCellStyle(infoStyle);
		cell.setCellValue(createHelper.createRichTextString("Überstundenausgleich:"));		
		createTableFooterRow(createHelper, sheet, columnStyle, filledColumnStyle, 13+projectCount, beg, end);
		
		createFooterItem(createHelper, sheet, infoStyle, inputCenterStyle, 17+projectCount, 8, 14, "Gesamtstunden:");
		createFooterItem(createHelper, sheet, infoStyle, inputCenterStyle, 19+projectCount, 8, 14, "Sollstunden:");
		
		int rowIndex = 21+projectCount;
		row = createFooterItem(createHelper, sheet, infoStyle, inputCenterStyle, rowIndex, 8, 14, "Überstunden:");		
		createTextInputField(sheet, createHelper, inputCenterStyle, row, rowIndex, 20, 33, "");
		
		rowIndex = 22+projectCount;
		row = sheet.createRow(rowIndex);
		createTextInputField(sheet, createHelper, infoSmallStyle, row, rowIndex, 20, 33, "Ort, Datum, Unterschrift Mitarbeiter PiSA sales GmbH");
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
	 * @param beg the range begin
	 * @param end the range end
	 * @param projectCount the project count
	 */
	private void createTable(CreationHelper createHelper, Sheet sheet, int beg, int end, int projectCount) {
		CellStyle columnStyle = getStyler().getStyle(XlsCellStyler.COLUMN);
		CellStyle filledColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_FILLED);
		CellStyle grayedColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_GRAYED);
		CellStyle grayedFilledColumnStyle = getStyler().getStyle(XlsCellStyler.COLUMN_FILLED_AND_GRAYED);
		
		createTableHeaderRow(createHelper, sheet, columnStyle, filledColumnStyle, 5, beg, end);
				
		for( int i=0; i<projectCount; i++ ) {
			int rowIndex = 6+i;
			if( i%2 == 0 ) {
				createTableRow(createHelper, sheet, columnStyle, filledColumnStyle, rowIndex, beg, end,"PiSA sales Web Client Entwicklung");
			} else {
				createTableRow(createHelper, sheet, grayedColumnStyle, grayedFilledColumnStyle, rowIndex, beg, end,"PiSA sales Web Client Entwicklung");
			}		
		}
		
		if( projectCount%2==0 ) {
			createTableRow(createHelper, sheet, columnStyle, filledColumnStyle, 6+projectCount, beg, end, "Krankheit");
			createTableRow(createHelper, sheet, grayedColumnStyle, grayedFilledColumnStyle, 7+projectCount, beg, end, "Urlaub");
		} else {
			createTableRow(createHelper, sheet, grayedColumnStyle, grayedFilledColumnStyle, 6+projectCount, beg, end, "Krankheit");
			createTableRow(createHelper, sheet, columnStyle, filledColumnStyle, 7+projectCount, beg, end, "Urlaub");
		}
				
		createTableFooterRow(createHelper, sheet, columnStyle, filledColumnStyle, 9+projectCount, beg, end);
	}
	
	/**
	 * Marks all invalid days between beg and end.
	 * @param beg the begin date
	 * @param end the end date
	 */
	private void markInvalidColumns( int beg, int end ) {
		if( beg<end ) {
			for( int i=beg; i<=end; i++ ) {
				if( Math.random() > 0.5 ) {
					invalidColumns.add(i);
				}
			}
		}
	}

	/**
	 * Creates the worksheet.
	 * @param wb the workbook
	 * @return the worksheet
	 */
	private Sheet createSheet(Workbook wb) {
		Sheet sheet = wb.createSheet("Juli 2013");
		sheet.setDisplayGridlines(false);
		sheet.setDefaultColumnWidth(4);
		sheet.setDefaultRowHeight((short)340);
		sheet.setColumnWidth(33, 15*256);
		sheet.setColumnWidth(34, 40*256);
		return sheet;
	}
	
	/**
	 * Creates a row in the main table.
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 * @param columnStyle the column style
	 * @param filledColumnStyle the filled column style
	 * @param rowIndex the row index
	 * @param beg the range begin
	 * @param end the range end
	 * @param title the project title
	 */
	private void createTableRow(CreationHelper createHelper, Sheet sheet, CellStyle columnStyle, CellStyle filledColumnStyle, int rowIndex, int beg, int end, String title) {
		Row row = sheet.createRow(rowIndex);
		
		for( int i=0,j=beg; j<=end; i++,j++ ) {			
			Cell cell = row.createCell(i);
			if( isInvalidColumn(j) ) {
				cell.setCellStyle(filledColumnStyle);
			} else {
				cell.setCellStyle(columnStyle);
			}			
		}
		
		Cell cell = row.createCell(end+1);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(0.0);
		
		cell = row.createCell(end+2);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(rowIndex);
		
		cell = row.createCell(end+3);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(title);
	}
	
	/**
	 * Creates the footer row for the main table.
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 * @param columnStyle the column style
	 * @param filledColumnStyle the filled column style
	 * @param rowIndex the row index
	 * @param beg the range begin
	 * @param end the range end
	 */
	private void createTableFooterRow(CreationHelper createHelper, Sheet sheet, CellStyle columnStyle, CellStyle filledColumnStyle, int rowIndex, int beg, int end) {
		Row row = sheet.createRow(rowIndex);
		
		for( int i=0,j=beg; j<=end; i++,j++ ) {			
			Cell cell = row.createCell(i);
			if( isInvalidColumn(j) ) {
				cell.setCellStyle(filledColumnStyle);
			} else {
				cell.setCellStyle(columnStyle);
			}			
		}
		
		Cell cell = row.createCell(end+1);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(0.0);		
	}

	/**
	 * Creates the header row for the main table.
	 * @param createHelper the workbook create helper
	 * @param sheet the worksheet
	 * @param columnStyle the column style
	 * @param filledColumnStyle the filled column style
	 * @param rowIndex the row index
	 * @param beg the range begin
	 * @param end the range end
	 */
	private void createTableHeaderRow(CreationHelper createHelper, Sheet sheet, CellStyle columnStyle, CellStyle filledColumnStyle, int rowIndex, int beg, int end) {
		Row row = sheet.createRow(rowIndex);
		
		for( int i=0,j=beg; j<=end; i++,j++ ) {			
			Cell cell = row.createCell(i);
			if( isInvalidColumn(j) ) {
				cell.setCellStyle(filledColumnStyle);
			} else {
				cell.setCellStyle(columnStyle);
			}
			cell.setCellValue(j);			
		}
		
		Cell cell = row.createCell(end+1);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(createHelper.createRichTextString("Std."));
		
		cell = row.createCell(end+2);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(createHelper.createRichTextString("Nr."));
		
		cell = row.createCell(end+3);
		cell.setCellStyle(columnStyle);
		cell.setCellValue(createHelper.createRichTextString("Name"));
	}
	
	/**
	 * Checks whether the column maps to a holiday or weekend.
	 * @param day the day
	 * @return <code>true</code> if day maps to a holiday or weekend
	 */
	private boolean isInvalidColumn( int day ) {
		return invalidColumns.contains(day);
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
}
