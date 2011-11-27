package org.cejug.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * This class groups a set of methods to deal with special text requirements
 * that are not already covered by the Java API.
 * @author Hildeberto Mendonca
 */
public class TextUtils {

    /** Receives a sentence and converts the first letter of each word to a
     * capital letter and the rest of each word to lowercase. */
    public static String capitalizeFirstCharWords(String sentence) {
        final StringBuilder result = new StringBuilder(sentence.length());
        String[] words = sentence.split("\\s");
        for(int i = 0,length = words.length; i < length; ++i) {
            if(i > 0)
                result.append(" ");
            result.append(Character.toUpperCase(words[i].charAt(0)))
                  .append(words[i].substring(1).toLowerCase());
        }
        return result.toString();
    }
    
    public static String getFormattedDate(Date date, String formatDate) {
    	if(date == null)
    		return "";
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(formatDate);
    	return sdf.format(date);
    }
    
    public static String getFormattedTime(Date time, String formatTime, String timezone) {
    	if(time == null)
    		return "";
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(formatTime);
    	TimeZone tz = TimeZone.getTimeZone(timezone);
    	sdf.setTimeZone(tz);
    	return sdf.format(time);
    }
    
    public static String getFormattedDateTime(Date dateTime, String formatDateTime, String timezone) {
    	if(dateTime == null)
    		return "";
    	
    	SimpleDateFormat sdf = new SimpleDateFormat(formatDateTime);
    	TimeZone tz = TimeZone.getTimeZone(timezone);
    	sdf.setTimeZone(tz);
    	return sdf.format(dateTime);
    }
}