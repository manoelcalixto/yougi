package org.cejug.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.cejug.entity.DeactivationType;
import org.cejug.web.util.ResourceBundle;

/**
 * DeactivationType is a Enum. This converter is responsible for transforming
 * that Enum in something readable for the end-user, using ResourceBundle.
 * @author Hildeberto Mendonca
 */
@FacesConverter(value="DeactivationTypeEnumToString")
public class DeactivationTypeEnumToString implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if(value.equals("ownwill"))
            return DeactivationType.OWNWILL;
        else if(value.equals("administrative"))
            return DeactivationType.ADMINISTRATIVE;
        else
            return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        DeactivationType deactivationType = (DeactivationType) value;
        ResourceBundle bundle = new ResourceBundle();
        if(deactivationType == DeactivationType.OWNWILL)
            return bundle.getMessage("ownWill");
        else if(deactivationType == DeactivationType.ADMINISTRATIVE)
            return bundle.getMessage("administrative");
        else
            return "";
    }
}