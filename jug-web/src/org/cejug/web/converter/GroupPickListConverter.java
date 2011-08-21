/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cejug.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.cejug.entity.UserAccount;

/**
 *
 * @author Hildeberto Mendonca
 */
@FacesConverter(value="GroupPickListConverter")
public class GroupPickListConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value == null || value.isEmpty())
            return null;
        UserAccount userAccount = new UserAccount(value);
        return userAccount;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value == null)
            return null;
        return value.toString();
    }

}
