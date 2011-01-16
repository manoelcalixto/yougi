package org.cejug.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.cejug.web.util.ResourceBundle;

@FacesConverter(value="GenderNumberToString")
public class GenderNumberToString implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value.equals("male"))
            return new Integer(1);
        else
            return new Integer(0);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Integer gender = (Integer) value;
        ResourceBundle bundle = new ResourceBundle();
        if(gender == 1)
            return bundle.getMessage("male");
        else
            return bundle.getMessage("female");
    }
}