/**
 * 
 */
package com.isd.bluecollar.controller.report.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Cell styler contains a list of styles used to format the spreadsheet cells. 
 * @author doan
 */
public class XlsCellStyler {

	/** Info style */
	public static final String INFO = "info";
	/** Small info style */
	public static final String SMALL_INFO = "smallInfo";
	/** Input field with left alignment */
	public static final String INPUT_LEFT = "inputLeft";
	/** Input field with centered alignment */
	public static final String INPUT_CENTER = "inputCenter";
	/** Column/table cell style */
	public static final String COLUMN = "column";
	/** Column/table cell filled style*/
	public static final String COLUMN_FILLED = "columnFilled";
	/** Grayed table cell style */
	public static final String COLUMN_GRAYED = "columnGrayed";
	/** Filled grayed table cell style */
	public static final String COLUMN_FILLED_AND_GRAYED = "columnFilledGrayed";
	
	/** The style map */
	private Map<String, CellStyle> styleMap;
	
	/**
	 * Creates a new styler instance.
	 * @param aWb the workbook
	 */
	public XlsCellStyler( Workbook aWb ) {
		styleMap = new HashMap<String, CellStyle>();		
		styleMap.put(INFO, getInfoCellStyle(aWb));
		styleMap.put(SMALL_INFO, getInfoSmallCellStyle(aWb));
		styleMap.put(INPUT_LEFT, getInputStyle(aWb, CellStyle.ALIGN_LEFT));
		styleMap.put(INPUT_CENTER, getInputStyle(aWb, CellStyle.ALIGN_CENTER));
		styleMap.put(COLUMN, getColumnCellStyle(aWb,false,false));
		styleMap.put(COLUMN_FILLED, getColumnCellStyle(aWb,true,false));
		styleMap.put(COLUMN_GRAYED, getColumnCellStyle(aWb, false, true));
		styleMap.put(COLUMN_FILLED_AND_GRAYED, getColumnCellStyle(aWb, true, true));
	}
	
	/**
	 * Retrieves the style matching the style name. Check the style
	 * constants of this class, style is one of:
	 * <ul>
	 * <li>{@link #INFO}</li>
	 * <li>{@link #SMALL_INFO}</li>
	 * <li>{@link #INPUT_LEFT}</li>
	 * <li>{@link #INPUT_CENTER}</li>
	 * <li>{@link #COLUMN}</li>
	 * <li>{@link #COLUMN_FILLED}</li>
	 * <li>{@link #COLUMN_GRAYED}</li>
	 * <li>{@link #COLUMN_FILLED_AND_GRAYED}</li>
	 * </ul>
	 * @param aStyleName the style name
	 * @return the cell style corresponding to the style name
	 */
	public CellStyle getStyle( String aStyleName ) {
		if( styleMap.containsKey(aStyleName) ) {
			return styleMap.get(aStyleName);
		}
		return styleMap.get(INFO);
	}
	
	/**
	 * Creates and returns the input cell style.
	 * @param wb the workbook
	 * @param alignment the alignment
	 * @return the input cell style
	 */
	private CellStyle getInputStyle(Workbook wb, short alignment) {
		CellStyle inputStyle = wb.createCellStyle();		
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		inputStyle.setBorderBottom(CellStyle.BORDER_THIN);
		inputStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		inputStyle.setFont(font);
		inputStyle.setAlignment(alignment);		
		return inputStyle;
	}

	/**
	 * Creates and returns info cell style.
	 * @param wb the workbook
	 * @return the info cell style
	 */
	private CellStyle getInfoCellStyle(Workbook wb) {
		CellStyle infoStyle = wb.createCellStyle();		
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		infoStyle.setFont(font);		
		return infoStyle;
	}
	
	/**
	 * Creates and returns small info cell style.
	 * @param wb the workbook
	 * @return the info cell style
	 */
	private CellStyle getInfoSmallCellStyle(Workbook wb) {
		CellStyle infoStyle = wb.createCellStyle();		
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		infoStyle.setFont(font);
		infoStyle.setAlignment(CellStyle.ALIGN_CENTER);
		return infoStyle;
	}
	
	/**
	 * Creates and returns the column cell style.
	 * @param wb the workbook
	 * @param filled <code>true</code> to fill cell content
	 * @param grayed <code>true</code> to gray background
	 * @return the column cell style
	 */
	private CellStyle getColumnCellStyle(Workbook wb, boolean filled, boolean grayed) {
		CellStyle columnStyle = wb.createCellStyle();		
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		columnStyle.setFont(font);		
		columnStyle.setAlignment(CellStyle.ALIGN_CENTER);
		columnStyle.setBorderTop(CellStyle.BORDER_THIN);
		columnStyle.setBorderBottom(CellStyle.BORDER_THIN);
		columnStyle.setBorderLeft(CellStyle.BORDER_THIN);
		columnStyle.setBorderRight(CellStyle.BORDER_THIN);
		
		if( grayed ) {
			columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);			
			if( filled ) {
				columnStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
				columnStyle.setFillPattern(CellStyle.THIN_HORZ_BANDS);
				columnStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
			} else {
				columnStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());	
			}
		} else if( filled ) {
			columnStyle.setFillPattern(CellStyle.THIN_HORZ_BANDS);
		}
		
		return columnStyle;
	}
}
