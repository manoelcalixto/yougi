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