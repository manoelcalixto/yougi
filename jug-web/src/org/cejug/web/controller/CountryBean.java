package org.cejug.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.LocationBsn;
import org.cejug.entity.Country;

@ManagedBean
@RequestScoped
public class CountryBean {

    @EJB
    private LocationBsn locationBsn;

    @ManagedProperty(value="#{param.acronym}")
    private String acronym;

    private Country country;

    public CountryBean() {}

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public List<Country> getCountries() {
        return locationBsn.findCountries();
    }

    @PostConstruct
    public void load() {
        if(acronym != null && !acronym.isEmpty()) {
            this.country = locationBsn.findCountry(acronym);
        }
        else {
            this.country = new Country();
        }
    }

    public String save() {
        locationBsn.saveCountry(this.country);
        return "countries?faces-redirect=true";
    }

    public String remove() {
        locationBsn.removeCountry(country.getAcronym());
        return "countries?faces-redirect=true";
    }
}