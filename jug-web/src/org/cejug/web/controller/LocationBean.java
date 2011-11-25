package org.cejug.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.cejug.business.LocationBsn;
import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;

/** 
 * This class is used to manage the update of the fields country, province and
 * city, based on the selection of the user. When the user selects the country,
 * its provinces and cities are listed in the respective fields. When the user
 * selects a province, its cities are listed in the respective field. This class
 * should be used every time at least 2 of the location fields are presented to
 * the user.
 * */
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
    	if(this.countries == null)
    		this.countries = locationBsn.findCountries();
        return this.countries;
    }

    public List<Province> getProvinces() {
    	if(this.selectedCountry != null) {
    		Country country = new Country(selectedCountry);
            this.provinces = locationBsn.findProvinces(country);
            return this.provinces;
    	}
    	else
    		return null;
    }

    public List<City> getCities() {
    	if(selectedCountry != null && selectedProvince == null) {
        	Country country = new Country(selectedCountry); 
        	this.cities = locationBsn.findCities(country, false);
        }
    	else if(selectedProvince != null) {
    		Province province = new Province(selectedProvince);
        	this.cities = locationBsn.findCities(province, false);
    	}
        return this.cities;
    }
    
    public List<String> getCitiesStartingWith(String initials) {
        List<City> cities = locationBsn.findCitiesStartingWith(initials);
        List<String> citiesStartingWith = new ArrayList<String>();
        for(City city:cities) {
            citiesStartingWith.add(city.getName());
        }
        return citiesStartingWith;
    }
    
    public String getCityNotListed() {
        return cityNotListed;
    }
    
    public City getNotListedCity() {
	    City newCity = null;
	    if(this.cityNotListed != null && !this.cityNotListed.isEmpty()) {
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
    	if(this.selectedCountry != null) {
    		return new Country(this.selectedCountry);
    	}
    	else
    		return null;
    }

    public String getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
		this.selectedProvince = null;
		this.selectedCity = null;
		logger.info("selectedCountry setted: "+ this.selectedCountry);
	}
	
	public Province getProvince() {
    	if(this.selectedProvince != null && !this.selectedProvince.isEmpty()) {
    		return new Province(this.selectedProvince);
    	}
    	else
    		return null;
    }

	public String getSelectedProvince() {
		return selectedProvince;
	}

	public void setSelectedProvince(String selectedProvince) {
		this.selectedProvince = selectedProvince;
		this.selectedCity = null;
		logger.info("selectedProvince setted: "+ this.selectedProvince);
	}

	public City getCity() {
    	if(this.selectedCity != null && !this.selectedCity.isEmpty()) {
    		return new City(this.selectedCity);
    	}
    	else
    		return null;
    }
	
	public String getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(String selectedCity) {
		this.selectedCity = selectedCity;
		logger.info("selectedCity setted: "+ this.selectedCity);
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