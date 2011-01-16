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