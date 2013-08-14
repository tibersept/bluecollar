/**
 * 11.08.2013
 */
package com.isd.bluecollar.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Report language loads report language properties and provides and easy access interface
 * to these.
 * @author doan
 */
public class ReportLanguage {

	/** No user declared */
	public final String nouser;
	/** No month declared */
	public final String nomonth;
	/** No year declared */
	public final String noyear;
	/** Name label */
	public final String labelname;
	/** Month label */
	public final String labelmonth;
	/** Year label */
	public final String labelyear;
	/** Overtime compensation label */
	public final String labelovertimecompensation;
	/** Total hours compensation label */
	public final String labeltotalhours;
	/** Required hours label */
	public final String labelrequiredhours;
	/** Overtime label */
	public final String labelovertime;
	/** Location date signature label */
	public final String labelpds;
	/** Employee label */
	public final String labelemployee;
	/** Hours column label */
	public final String columnhours;
	/** Numbers column label */
	public final String columnnumbers;
	/** Name column label */
	public final String columnname;
	/** Sickness string */
	public final String sickness;
	/** Vacation string */
	public final String vacation;
	/** Report name */
	public final String reportname;
	
	/** Language properties */
	private final Properties language;
	
	/**
	 * Creates a new instance of the report language.
	 * @param aLng the language code (en|de|fr)
	 */
	public ReportLanguage( String aLng ) {
		language = new Properties();
		loadLanguage(aLng);
		nouser = language.getProperty("nouser");
		nomonth = language.getProperty("nomonth");
		noyear = language.getProperty("noyear");
		labelname = language.getProperty("labelname");
		labelmonth = language.getProperty("labelmonth");
		labelyear = language.getProperty("labelyear");
		labelovertimecompensation = language.getProperty("labelovertimecompensation");
		labeltotalhours = language.getProperty("labeltotalhours");
		labelrequiredhours = language.getProperty("labelrequiredhours");
		labelovertime = language.getProperty("labelovertime");
		labelpds = language.getProperty("labelpds");
		labelemployee = language.getProperty("labelemployee");
		columnhours = language.getProperty("columnhours");
		columnnumbers = language.getProperty("columnnumbers");
		columnname = language.getProperty("columnname");
		sickness = language.getProperty("sickness");
		vacation = language.getProperty("vacation");
		reportname = language.getProperty("reportname");
	}
	
	/**
	 * Loads the language properties file.
	 * @param aLng the language code
	 */
	private void loadLanguage( String aLng ) {
		String lng = aLng;
		if( lng==null || !(lng.length()>0) ) {
			lng = "en";
		}
		String languageFilename = "language-"+lng.toLowerCase()+".properties";
		InputStream is = this.getClass().getResourceAsStream(languageFilename);
		try {
			language.load(is);
		} catch (IOException e) {
			loadFixedLanguage();
		}
	}
	
	/**
	 * Loads fixed language properties.
	 */
	private void loadFixedLanguage() {
		language.setProperty("nouser", "missing user");
		language.setProperty("nomonth", "missing month data");
		language.setProperty("noyear", "missing year data");
		language.setProperty("labelname", "Name:");
		language.setProperty("labelmonth", "Month:");
		language.setProperty("labelyear", "Year:");
		language.setProperty("labelovertimecompensation", "Overtime compensation:");
		language.setProperty("labeltotalhours", "Total hours:");
		language.setProperty("labelrequiredhours", "Required hours:");
		language.setProperty("labelovertime", "Overtime:");
		language.setProperty("labelpds", "Location, Date, Signature");
		language.setProperty("labelemployee", "Employee");
		language.setProperty("columnhours", "Hrs.");
		language.setProperty("columnnumbers", "Nr.");
		language.setProperty("columnname", "Name");
		language.setProperty("sickness", "Sickness");
		language.setProperty("vacation", "Vacation");
		language.setProperty("reportname", "WorkhoursReport");
	}
	
}