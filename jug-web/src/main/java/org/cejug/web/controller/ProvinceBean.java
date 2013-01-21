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
package org.cejug.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;
import org.cejug.business.LocationBsn;
import org.cejug.entity.Country;
import org.cejug.entity.Province;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class ProvinceBean {

    @EJB
    private LocationBsn locationBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private Country selectedCountry;

    private Province province;

    public ProvinceBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public List<SelectItem> getCountries() {
        List<Country> countries = locationBsn.findCountries();
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        SelectItem selectItem = new SelectItem("", "Select...");
        selectItems.add(selectItem);
        for(Country country: countries) {
            selectItem = new SelectItem(country.getAcronym(), country.getName());
            selectItems.add(selectItem);
        }
        return selectItems;
    }

    public SelectItem[] getAssociatedCountries() {
        List<Country> countries = locationBsn.findAssociatedCountries();
        SelectItem[] options = new SelectItem[countries.size() + 1];
        options[0] = new SelectItem("", "Select");
        for(int i = 0; i < countries.size(); i++) {
            options[i + 1] = new SelectItem(countries.get(i), countries.get(i).getName());
        }
        return options;
    }

    public List<Province> getProvinces() {
        return locationBsn.findProvinces();
    }

    public String getSelectedCountry() {
        if(selectedCountry == null)
            return null;

        return selectedCountry.getAcronym();
    }

    public void setSelectedCountry(String acronym) {
        if(acronym == null || acronym.isEmpty())
            return;

        this.selectedCountry = locationBsn.findCountry(acronym);
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.province = locationBsn.findProvince(id);
            this.selectedCountry = this.province.getCountry();
        }
        else {
            this.province = new Province();
        }
    }

    public String save() {
        this.province.setCountry(selectedCountry);
        locationBsn.saveProvince(this.province);
        return "provinces?faces-redirect=true";
    }

    public String remove() {
        locationBsn.removeProvince(province.getId());
        return "provinces?faces-redirect=true";
    }
}