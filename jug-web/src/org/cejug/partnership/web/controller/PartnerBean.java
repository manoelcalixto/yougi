package org.cejug.partnership.web.controller;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;
import org.cejug.web.controller.LocationBean;

@ManagedBean
@RequestScoped
public class PartnerBean implements Serializable {
    
	private static final long serialVersionUID = 1L;

	@EJB
    private PartnerBsn partnerBsn;
        
    @ManagedProperty(value="#{param.id}")
    private String id;
    
    @ManagedProperty(value="#{locationBean}")
    private LocationBean locationBean;
    
    private Partner partner;
    private List<Partner> partners;
    private List<Representative> representatives;
    
    public PartnerBean() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public List<Partner> getPartners() {
		if(partners == null)
			this.partners = partnerBsn.findPartners();
    	return partners;
    }
    
    public List<Representative> getRepresentatives() {
    	return representatives;
    }
    
	public LocationBean getLocationBean() {
		return locationBean;
	}

	public void setLocationBean(LocationBean locationBean) {
		this.locationBean = locationBean;
	}

	@PostConstruct
	public void load() {
		if(this.id != null && !this.id.isEmpty()) {
			this.partner = partnerBsn.findPartner(id);
	        
			locationBean.initialize();
			
	        if(this.partner.getCountry() != null)
	        	locationBean.setSelectedCountry(this.partner.getCountry().getAcronym());
	        
	        if(this.partner.getProvince() != null)
	        	locationBean.setSelectedProvince(this.partner.getProvince().getId());
	        
	        if(this.partner.getCity() != null)
	        	locationBean.setSelectedCity(this.partner.getCity().getId());
		}
		else
			this.partner = new Partner();
    }
    
    public String save() {
    	Country country = this.locationBean.getCountry();
    	if(country != null) {
    		this.partner.setCountry(country);
    	}
    	
    	Province province = this.locationBean.getProvince();
    	if(province != null) {
    		this.partner.setProvince(province);
    	}
    	
    	City city = this.locationBean.getCity();
    	if(city != null) {
    		this.partner.setCity(city);
    	}
    	    	
        partnerBsn.save(this.partner);
        return "partners?faces-redirect=true";
    }

    public String remove() {
        partnerBsn.remove(this.partner.getId());
        return "partners?faces-redirect=true";
    }
}