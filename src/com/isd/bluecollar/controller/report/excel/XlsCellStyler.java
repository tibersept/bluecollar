/**
 * 23.05.2015
 */
package com.isd.bluecollar.controller.report.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
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
	/** Cell style with medium top/bottom border and bold text  */
	public static final String CELL_BOLD_BORDER = "cellBoldBorder";
	/** Normal cell style */
	public static final String CELL = "cell";
	/** Float content cell style */
	public static final String CELL_FLOAT = "cellFloat";
	/** Filled cell style*/
	public static final String CELL_FILLED_FLOAT = "cellFilled";
	/** Grayed cell style */
	public static final String CELL_GRAYED = "cellGrayed";
	/** Grayed float cell style */
	public static final String CELL_GRAYED_FLOAT = "cellGrayedFloat";
	/** Filled grayed cell style */
	public static final String CELL_FILLED_AND_GRAYED_FLOAT = "cellFilledGrayed";
	/** Right aligned cell style */
	public static final String CELL_RIGHT_ALIGNED = "cellRightAligned";
	
	/** The style map */
	private Map<String, CellStyle> styleMap;
	
	/**
	 * Creates a new styler instance.
	 * @param aWb the workbook
	 */
	public XlsCellStyler( Workbook aWb ) {
		styleMap = new HashMap<String, CellStyle>();		
		styleMap.put(INFO, getInfo(aWb));
		styleMap.put(SMALL_INFO, getInfoSmall(aWb));
		styleMap.put(INPUT_LEFT, getInput(aWb, CellStyle.ALIGN_LEFT));
		styleMap.put(INPUT_CENTER, getInput(aWb, CellStyle.ALIGN_CENTER));
		styleMap.put(CELL, getCell(aWb,false,false));
		styleMap.put(CELL_FLOAT, setFloat(aWb,getCell(aWb,false,false)));
		styleMap.put(CELL_BOLD_BORDER, setBold(aWb, setBorder(getCell(aWb,false,false),1,0,1,0)));
		styleMap.put(CELL_FILLED_FLOAT, setFloat(aWb, getCell(aWb,true,false)));
		styleMap.put(CELL_GRAYED, getCell(aWb, false, true));
		styleMap.put(CELL_GRAYED_FLOAT, setFloat(aWb, getCell(aWb, false, true)));
		styleMap.put(CELL_FILLED_AND_GRAYED_FLOAT, setFloat(aWb, getCell(aWb,true,true)));
		styleMap.put(CELL_RIGHT_ALIGNED, getRightAligned(aWb));
	}
	
	/**
	 * Retrieves the style matching the style name. Check the style
	 * constants of this class, style is one of:
	 * <ul>
	 * <li>{@link #INFO}</li>
	 * <li>{@link #SMALL_INFO}</li>
	 * <li>{@link #INPUT_LEFT}</li>
	 * <li>{@link #INPUT_CENTER}</li>
	 * <li>{@link #CELL}</li>
	 * <li>{@link #CELL_FLOAT}</li>
	 * <li>{@link #CELL_BOLD_BORDER}</li>
	 * <li>{@link #CELL_FILLED_FLOAT}</li>
	 * <li>{@link #CELL_GRAYED}</li>
	 * <li>{@link #CELL_GRAYED_FLOAT}</li>
	 * <li>{@link #CELL_FILLED_AND_GRAYED_FLOAT}</li>
	 * <li>{@link #CELL_RIGHT_ALIGNED}</li>
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
	private CellStyle getInput(Workbook wb, short alignment) {
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
	private CellStyle getInfo(Workbook wb) {
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
	private CellStyle getInfoSmall(Workbook wb) {
		CellStyle infoStyle = wb.createCellStyle();		
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		infoStyle.setFont(font);
		infoStyle.setAlignment(CellStyle.ALIGN_CENTER);
		return infoStyle;
	}
	
	/**
	 * Creates and returns the table cell style.
	 * @param wb the workbook
	 * @param filled <code>true</code> to fill cell content
	 * @param grayed <code>true</code> to gray background 
	 * @return the column cell style
	 */
	private CellStyle getCell(Workbook wb, boolean filled, boolean grayed) {
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
				columnStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
				columnStyle.setFillPattern(CellStyle.THIN_BACKWARD_DIAG);
				columnStyle.setFillBackgroundColor(IndexedColors.PALE_BLUE.getIndex());
			} else {
				columnStyle.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());	
			}
		} else if( filled ) {
			columnStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
			columnStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
			columnStyle.setFillPattern(CellStyle.THIN_BACKWARD_DIAG);
			columnStyle.setFillBackgroundColor(IndexedColors.WHITE.getIndex());
		}
		
		return columnStyle;
	}
	
	/**
	 * Returns a style which only enforces right alignment and nothing else.
	 * @return the right alignment style
	 */
	private CellStyle getRightAligned( Workbook wb ) {
		CellStyle style = wb.createCellStyle();
		style.setAlignment(CellStyle.ALIGN_RIGHT);
		return style;
	}
	
	/**
	 * Sets the font of the cell style to bold.
	 * @param wb the workbook
	 * @param aStyle the cell style
	 * @return the cell style
	 */
	private CellStyle setBold( Workbook wb, CellStyle aStyle ) {
		Font font = wb.createFont();
		font.setFontName(HSSFFont.FONT_ARIAL);
		font.setFontHeightInPoints((short)10);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		aStyle.setFont(font);
		return aStyle;
	}
	
	/**
	 * Sets the cell style to float data format.
	 * @param wb the workbook
	 * @param aStyle the cell style
	 * @return the cell style
	 */
	private CellStyle setFloat( Workbook wb, CellStyle aStyle ) {
		DataFormat format = wb.createDataFormat();
		short indexedFormat = format.getFormat("0.0");
		aStyle.setDataFormat(indexedFormat);
		return aStyle;
	}
	
	/**
	 * Sets the cell style to integer data format.
	 * @param wb the workbook
	 * @param aStyle the cell style
	 * @return the cell style
	 * 
	 */
	@SuppressWarnings("unused")
	private CellStyle setInteger( Workbook wb, CellStyle aStyle ) {
		DataFormat format = wb.createDataFormat();
		short indexedFormat = format.getFormat("0");
		aStyle.setDataFormat(indexedFormat);
		return aStyle;
	}
	
	/**
	 * Sets the borders of a cell style.
	 * @param aStyle the style
	 * @param aTop the top border 
	 * @param aRight the right border
	 * @param aBottom the bottom border
	 * @param aLeft the left border
	 * @return the cell style
	 */
	private CellStyle setBorder( CellStyle aStyle, int aTop, int aRight, int aBottom, int aLeft ) {
		aStyle.setBorderTop(getBorderStyle(aTop));
		aStyle.setBorderRight(getBorderStyle(aRight));
		aStyle.setBorderBottom(getBorderStyle(aBottom));
		aStyle.setBorderLeft(getBorderStyle(aLeft));
		return aStyle;
	}
	
	/**
	 * Converts the internal encoding of cell style to actual constants.
	 * @param aBorderEncoding the internal encoding of border style
	 * @return the converted POI cell style for border
	 */
	private short getBorderStyle( int aBorderEncoding ) {
		switch(aBorderEncoding) {
		case 1:
			return CellStyle.BORDER_MEDIUM;
		case 2:
			return CellStyle.BORDER_THICK;
		default:
			return CellStyle.BORDER_THIN;
		}
	}
}
