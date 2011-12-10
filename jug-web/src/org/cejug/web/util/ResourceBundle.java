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

import java.util.Locale;
import java.util.MissingResourceException;
import javax.faces.context.FacesContext;

/**
 * Encapsulates the complexity of getting the resource bundle from the context.
 * Actually, this is so complex that a better approach should be investigated, or
 * a solution presented to spec leaders.
 * @author Hildeberto Mendonca
 */
public class ResourceBundle {

    public String getMessage(String key) {
        return getMessageFromResourceBundle(key);
    }

    private String getMessageFromResourceBundle(String key) {
        java.util.ResourceBundle bundle = null;
        String bundleName = "org.cejug.web.bundles.Resources";
        String message = "";
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

        try {
            bundle = java.util.ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName));
        } catch (MissingResourceException e) {
            return "?";
        }
        if (bundle == null)
            return "?";
        try {
            message = bundle.getString(key);
        } catch (Exception e) {
        }
        return message;
    }

    private static ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = fallbackClass.getClass().getClassLoader();
        return loader;
    }
}