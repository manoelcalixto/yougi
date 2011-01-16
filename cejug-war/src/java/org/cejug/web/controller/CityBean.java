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

@ManagedBean
@SessionScoped
public class CityBean implements Serializable {

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