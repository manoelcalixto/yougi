/* Jug Management is a web application conceived to manage user groups or 
 * communities focused on a certain domain of knowledge, whose members are 
 * constantly sharing information and participating in social and educational 
 * events. Copyright (C) 2011 Ceara Java User Group - CEJUG.
 * 
 * This application is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or (at your 
 * option) any later version.
 * 
 * This application is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.
 * 
 * There is a full copy of the GNU Lesser General Public License along with 
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place, 
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.web.util;

import java.util.Date;
import java.util.StringTokenizer;

import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.util.TextUtils;

/**
 * @author Hildeberto Mendonca
 */
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
    	return getFormattedDate(date, rb.getMessage("formatDate"));
    }
    
    public static String getFormattedTime(Date time) {
    	if(time == null)
    		return "";
    	
    	ResourceBundle rb = new ResourceBundle();
    	return getFormattedTime(time, rb.getMessage("formatTime"), "GMT-3");
    }
    
    public static String getFormattedDateTime(Date dateTime) {
    	if(dateTime == null)
    		return "";
    	
    	ResourceBundle rb = new ResourceBundle();
    	return getFormattedDateTime(dateTime, rb.getMessage("formatDateTime"), "GMT-3");
    }
    
    public static String printAddress(String address, Country country, Province province, City city, String postalCode) {
        StringBuilder fullAddress = new StringBuilder();
        String commaSeparator = ", ";
        if(address != null && !address.isEmpty())
            fullAddress.append(address);
        
        if(city != null) {
            if(!fullAddress.toString().isEmpty())
                fullAddress.append(commaSeparator);
            
            fullAddress.append(city.getName());
        }
        
        if(province != null) {
            if(!fullAddress.toString().isEmpty())
                fullAddress.append(commaSeparator);
            
            fullAddress.append(province.getName());
        }
        
        if(country != null) {
            if(!fullAddress.toString().isEmpty())
                fullAddress.append(" - ");
            
            fullAddress.append(country.getName());
        }
        
        if(postalCode != null) {
            if(!fullAddress.toString().isEmpty())
                fullAddress.append(".");
            ResourceBundle rb = new ResourceBundle();
            fullAddress.append(" ");
            fullAddress.append(rb.getMessage("postalCode"));
            if(country != null) {
                fullAddress.append(": ");
                fullAddress.append(country.getName());
            }
        }
        
        return fullAddress.toString();
    }
}