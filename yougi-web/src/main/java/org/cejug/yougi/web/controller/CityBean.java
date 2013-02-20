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
package org.cejug.yougi.web.controller;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.yougi.business.LocationBsn;
import org.cejug.yougi.business.UserAccountBsn;
import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.Country;
import org.cejug.yougi.entity.Province;
import org.cejug.yougi.entity.UserAccount;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class CityBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @EJB
    private LocationBsn locationBsn;

    @EJB
    private UserAccountBsn userAccountBsn;
    
    @ManagedProperty(value = "#{param.id}")
    private String id;
    
    @ManagedProperty(value="#{locationBean}")
    private LocationBean locationBean;

    private City city;

    public CityBean() {
        this.city = new City();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<City> getCities() {
        return locationBsn.findCities();
    }

    public List<UserAccount> getInhabitants() {
        return userAccountBsn.findInhabitantsFrom(this.city);
    }
    
    public List<String> getTimeZones() {
        return locationBsn.getTimeZones();
    }

    @PostConstruct
    public void load() {
        if (this.id != null && !this.id.isEmpty()) {
            this.city = locationBsn.findCity(id);
            
            locationBean.initialize();

            if (this.city.getCountry() != null) {
                locationBean.setSelectedCountry(this.city.getCountry().getAcronym());
            }

            if (this.city.getProvince() != null) {
                locationBean.setSelectedProvince(this.city.getProvince().getId());
            }
        }
    }

    public String save() {
        Country country = this.locationBean.getCountry();
        if (country != null) {
            this.city.setCountry(country);
        }

        Province province = this.locationBean.getProvince();
        if (province != null) {
            this.city.setProvince(province);
        }
        
        locationBsn.saveCity(this.city);
        
        return "cities?faces-redirect=true";
    }

    public String remove() {
        locationBsn.removeCity(city.getId());
        return "cities?faces-redirect=true";
    }

    public String cancel() {
        return "cities?faces-redirect=true";
    }
}