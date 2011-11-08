package org.cejug.web.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.TimeZone;

import org.cejug.util.TextUtils;

public final class WebTextUtils extends TextUtils {

	/** This method replaces every line break in the text by a html paragraph.
     *  Empty lines are ignored. It returns a text that appears formatted in a html page. */
    public static String convertLineBreakToHTMLParagraph(String str) {
    	if(str == null)
    		return null;
    	
		StringBuilder formattedStr = new StringBuilder();
		
		StringTokenizer st = new StringTokenizer(str, "\n");
		String token;
		while(st.hasMoreTokens()) {
			token = st.nextToken().trim();
			if(!token.isEmpty()) {
				formattedStr.append("<p>");
				formattedStr.append(token);
				formattedStr.append("</p>");
			}
		}
		
		return formattedStr.toString();
    }
    
    public static String getFormattedDate(Date date) {
    	if(date == null)
    		return "";
    	
    	ResourceBundle rb = new ResourceBundle();
    	String formatDate = rb.getMessage("formatDate");
    	SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
    	return sdf.format(date);
    }
    
    public static String getFormattedTime(Date time) {
    	if(time == null)
    		return "";
    	
    	ResourceBundle rb = new ResourceBundle();
    	String formatTime = rb.getMessage("formatTime");
    	SimpleDateFormat sdf = new SimpleDateFormat(formatTime);
    	TimeZone tz = TimeZone.getTimeZone("GMT-3");
    	sdf.setTimeZone(tz);
    	return sdf.format(time);
    }
    
    public static String getFormattedDateTime(Date dateTime) {
    	if(dateTime == null)
    		return "";
    	
    	ResourceBundle rb = new ResourceBundle();
    	String formatDateTime = rb.getMessage("formatDateTime");
    	SimpleDateFormat sdf = new SimpleDateFormat(formatDateTime);
    	TimeZone tz = TimeZone.getTimeZone("GMT-3");
    	sdf.setTimeZone(tz);
    	return sdf.format(dateTime);
    }
}