package org.cejug.partnership.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.cejug.business.LocationBsn;
import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.business.RepresentativeBsn;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;

@ManagedBean
@SessionScoped
public class PartnerBean implements Serializable {
    
	private static final long serialVersionUID = 1L;

	@EJB
    private PartnerBsn partnerBsn;
    
    @EJB
    private RepresentativeBsn representativeBsn;
    
    @EJB
    private LocationBsn locationBsn;
    
    private Partner partner;
    private List<Partner> partners;
    private List<Representative> representatives;
    private List<Country> countries;
    private List<Province> provinces;
    private List<City> cities;
    
    private String selectedCountry;
    private String selectedProvince;
    private String selectedCity;
    
    public PartnerBean() {}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public List<Partner> getPartners() {
    	return partners;
    }
    
    public List<Representative> getRepresentatives() {
    	return representatives;
    }
    
    public List<Country> getCountries() {
    	if(this.countries == null)
    		this.countries = locationBsn.findCountries();
        return this.countries;
    }

    public List<Province> getProvinces() {
    	if(this.selectedCountry != null && !this.selectedCountry.isEmpty()) {
    		Country country = new Country(selectedCountry);
            this.provinces = locationBsn.findProvinces(country);
    	}
    	return this.provinces;
    }

    public List<City> getCities() {
    	if(selectedProvince != null) {
    		Province province = new Province(selectedProvince);
        	this.cities = locationBsn.findCities(province, false);
    	}
        else if(selectedCountry != null) {
        	Country country = new Country(selectedCountry); 
        	this.cities = locationBsn.findCities(country, false);
        }
        return this.cities;
    }
        
    public String getSelectedCountry() {
		return selectedCountry;
	}

	public void setSelectedCountry(String selectedCountry) {
		this.selectedCountry = selectedCountry;
	}

	public String getSelectedProvince() {
		return selectedProvince;
	}

	public void setSelectedProvince(String selectedProvince) {
		this.selectedProvince = selectedProvince;
	}

	public String getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(String selectedCity) {
		this.selectedCity = selectedCity;
	}

	@PostConstruct
	public void load() {
		this.partners = partnerBsn.findPartners();
	}
	
    public String load(String id) {
        this.partner = partnerBsn.findPartner(id);
        
        if(this.partner.getCountry() != null)
        	this.selectedCountry = this.partner.getCountry().getAcronym();
        
        if(this.partner.getProvince() != null)
        	this.selectedProvince = this.partner.getProvince().getId();
        
        if(this.partner.getCity() != null)
        	this.selectedCity = this.partner.getCity().getId();
        
        this.representatives = representativeBsn.findRepresentatives(this.partner);
        return "partner?faces-redirect=true";
    }
    
    public String save() {
    	if(selectedCountry != null && !selectedCountry.isEmpty()) {
    		Country country = new Country(selectedCountry);
    		this.partner.setCountry(country);
    	}
    	
    	if(selectedProvince != null && !selectedProvince.isEmpty()) {
    		Province province = new Province(selectedProvince);
    		this.partner.setProvince(province);
    	}
    	
    	if(selectedCity != null && !selectedCity.isEmpty()) {
    		City city = new City(selectedCity);
        	this.partner.setCity(city);
    	}
    	    	
        partnerBsn.save(this.partner);
        removeSessionScoped();
        return "partners?faces-redirect=true";
    }

    public String remove() {
        partnerBsn.remove(this.partner.getId());
        removeSessionScoped();
        return "partners?faces-redirect=true";
    }
    
    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("partnerBean");
    }
}