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
package org.cejug.web.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.cejug.web.util.ResourceBundleHelper;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
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
        ResourceBundleHelper bundle = new ResourceBundleHelper();
        if(gender == 1)
            return bundle.getMessage("male");
        else
            return bundle.getMessage("female");
    }
}