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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.cejug.yougi.business.LocationBsn;
import org.cejug.yougi.entity.City;
import org.cejug.yougi.entity.Country;
import org.cejug.yougi.entity.Province;

/**
 * This class is used to manage the update of the fields country, province and
 * city, based on the selection of the user. When the user selects the country,
 * its provinces and cities are listed in the respective fields. When the user
 * selects a province, its cities are listed in the respective field. This class
 * should be used every time at least 2 of the location fields are presented to
 * the user.
 *
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 *
 */
@ManagedBean
@SessionScoped
public class LocationBean {

    static final Logger logger = Logger.getLogger("org.cejug.web.controller.LocationBean");

    @EJB
    private LocationBsn locationBsn;

    private List<Country> countries;

    private List<Province> provinces;

    private List<City> cities;

    private String selectedCountry;

    private String selectedProvince;

    private String selectedCity;

    private String cityNotListed;

    private boolean initialized;

    public LocationBean() {
        logger.info("A new locationBean created.");
    }

    public List<Country> getCountries() {
        if (this.countries == null) {
            this.countries = locationBsn.findCountries();
        }
        return this.countries;
    }

    public List<Province> getProvinces() {
        if (this.selectedCountry != null) {
            Country country = new Country(selectedCountry);
            this.provinces = locationBsn.findProvinces(country);
            return this.provinces;
        } else {
            return null;
        }
    }

    public List<City> getCities() {
        if (selectedCountry != null && selectedProvince == null) {
            Country country = new Country(selectedCountry);
            this.cities = locationBsn.findCities(country, false);
        } else if (selectedProvince != null) {
            Province province = new Province(selectedProvince);
            this.cities = locationBsn.findCities(province, false);
        }
        return this.cities;
    }

    public List<String> findCitiesStartingWith(String initials) {
        List<City> cits = locationBsn.findCitiesStartingWith(initials);
        List<String> citiesStartingWith = new ArrayList<String>();
        for (City city : cits) {
            citiesStartingWith.add(city.getName());
        }
        return citiesStartingWith;
    }

    public String getCityNotListed() {
        return cityNotListed;
    }

    /**
     * @return an instance of City not registered yet, according to the
     * parameters informed by the user.
     */
    public City getNotListedCity() {
        City newCity = null;
        if (this.cityNotListed != null && !this.cityNotListed.isEmpty()) {
            newCity = new City(null, this.cityNotListed);
            newCity.setCountry(getCountry());
            newCity.setProvince(getProvince());
            newCity.setValid(false);
        }
        return newCity;
    }

    public void setCityNotListed(String cityNotListed) {
        this.cityNotListed = cityNotListed;
    }

    public Country getCountry() {
        if (this.selectedCountry != null) {
            return locationBsn.findCountry(this.selectedCountry);
        } else {
            return null;
        }
    }

    public String getSelectedCountry() {
        return selectedCountry;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
        this.selectedProvince = null;
        this.selectedCity = null;
    }

    public Province getProvince() {
        if (this.selectedProvince != null && !this.selectedProvince.isEmpty()) {
            return locationBsn.findProvince(this.selectedProvince);
        } else {
            return null;
        }
    }

    public String getSelectedProvince() {
        return selectedProvince;
    }

    public void setSelectedProvince(String selectedProvince) {
        this.selectedProvince = selectedProvince;
        this.selectedCity = null;
    }

    public City getCity() {
        if (this.selectedCity != null && !this.selectedCity.isEmpty()) {
            return locationBsn.findCity(this.selectedCity);
        } else {
            return null;
        }
    }

    public String getSelectedCity() {
        return selectedCity;
    }

    public void setSelectedCity(String selectedCity) {
        this.selectedCity = selectedCity;
    }

    public void initialize() {
        this.countries = null;
        this.provinces = null;
        this.cities = null;

        this.selectedCountry = null;
        this.selectedProvince = null;
        this.selectedCity = null;

        this.initialized = true;

        logger.info("LocationBean initialized for a new use.");
    }

    public boolean isInitialized() {
        return this.initialized;
    }
}