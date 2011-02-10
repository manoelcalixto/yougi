package org.cejug.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author Hildeberto Mendonca
 */
@FacesConverter(value="AgeNumberToString")
public class AgeNumberToString implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return new Integer(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Integer gender = (Integer) value;

        if(gender > 0)
            return gender.toString();
        else
            return "";
    }
}