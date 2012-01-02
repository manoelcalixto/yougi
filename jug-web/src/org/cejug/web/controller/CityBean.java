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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.cejug.business.LocationBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.entity.UserAccount;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@SessionScoped
public class CityBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private LocationBsn locationBsn;

    @EJB
    private UserAccountBsn userAccountBsn;

    private String id;
    private Country selectedCountry;
    private Province selectedProvince;
    private City city;

    public CityBean() {
        
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
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

    public SelectItem[] getProvinces() {
        List<Province> provinces = locationBsn.findProvinces(selectedCountry);
        SelectItem[] selectItems = new SelectItem[provinces.size() + 1];
        SelectItem selectItem = new SelectItem("", "Select...");
        int i = 0;
        selectItems[i++] = selectItem;
        for(Province province: provinces) {
            selectItem = new SelectItem(province.getId(), province.getName());
            selectItems[i++] = selectItem;
        }
        return selectItems;
    }

    public List<City> getCities() {
        return locationBsn.findCities();
    }

    public List<UserAccount> getInhabitants() {
        return userAccountBsn.findInhabitantsFrom(this.city);
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

    public String getSelectedProvince() {
        if(selectedProvince == null)
            return null;

        return selectedProvince.getId();
    }

    public void setSelectedProvince(String id) {
        if(id == null || id.isEmpty())
            return;

        this.selectedProvince = locationBsn.findProvince(id);
    }

    @PostConstruct
    public void load() {
        this.city = new City();
    }

    public String load(String id) {
        this.id = id;
        this.city = locationBsn.findCity(id);
        this.selectedCountry = this.city.getCountry();
        this.selectedProvince = this.city.getProvince();
        return "city?faces-redirect=true";
    }

    public String save() {
        this.city.setCountry(selectedCountry);
        this.city.setProvince(selectedProvince);
        locationBsn.saveCity(this.city);

        removeSessionScoped();

        return "cities?faces-redirect=true";
    }

    public String remove() {
        locationBsn.removeCity(city.getId());

        removeSessionScoped();

        return "cities?faces-redirect=true";
    }

    public String cancel() {
        removeSessionScoped();
        return "cities?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("cityBean");
    }
}