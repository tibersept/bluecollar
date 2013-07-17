package com.isd.bluecollar.report;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Excel report generator.
 * @author isakov
 */
public class XlsReport {

	/**
	 * Generates a new report and return the result as a byte stream.
	 */
	public byte[] generateReport() {
		
		Workbook wb = new HSSFWorkbook();
		wb.createSheet("First sheet");
		wb.createSheet("Second sheet");
		wb.createSheet("Third sheet");
		
		return null; 
	}
}
