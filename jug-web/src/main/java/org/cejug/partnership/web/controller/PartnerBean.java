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
package org.cejug.partnership.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.AccessGroupBsn;
import org.cejug.business.UserGroupBsn;
import org.cejug.entity.*;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.business.RepresentativeBsn;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;
import org.cejug.web.controller.LocationBean;
import org.primefaces.model.DualListModel;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class PartnerBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @EJB
    private PartnerBsn partnerBsn;
    
    @EJB
    private AccessGroupBsn accessGroupBsn;
    
    @EJB
    private UserGroupBsn userGroupBsn;
    
    @EJB
    private RepresentativeBsn representativeBsn;
    
    @ManagedProperty(value = "#{param.id}")
    private String id;
    
    @ManagedProperty(value = "#{locationBean}")
    private LocationBean locationBean;
    
    private Partner partner;
    private List<Partner> partners;
    private List<Representative> representatives;
    
    // List of users from the group of partners, which are candidates for 
    // representative.
    private DualListModel<UserAccount> candidates;

    public PartnerBean() {
    }

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
        if (partners == null) {
            this.partners = partnerBsn.findPartners();
        }
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
    
    public DualListModel<UserAccount> getCandidates() {
        return candidates;
    }

    public void setCandidates(DualListModel<UserAccount> candidates) {
        this.candidates = candidates;
    }

    @PostConstruct
    public void load() {
    	
        if (this.id != null && !this.id.isEmpty()) {
            this.partner = partnerBsn.findPartner(id);

            locationBean.initialize();

            if (this.partner.getCountry() != null) {
                locationBean.setSelectedCountry(this.partner.getCountry().getAcronym());
            }

            if (this.partner.getProvince() != null) {
                locationBean.setSelectedProvince(this.partner.getProvince().getId());
            }

            if (this.partner.getCity() != null) {
                locationBean.setSelectedCity(this.partner.getCity().getId());
            }
            
            AccessGroup accessGroup = accessGroupBsn.findAccessGroupByName("partners"); 
        	List<UserAccount> usersGroup = userGroupBsn.findUsersGroup(accessGroup);
            List<UserAccount> reps = new ArrayList<UserAccount>();
            reps.addAll(representativeBsn.findRepresentativePersons(this.partner));
            usersGroup.removeAll(reps);
            this.candidates = new DualListModel<UserAccount>(usersGroup, reps);
        } else {
            this.partner = new Partner();
            
            AccessGroup accessGroup = accessGroupBsn.findAccessGroupByName("partners"); 
        	List<UserAccount> usersGroup = userGroupBsn.findUsersGroup(accessGroup);
            List<UserAccount> reps = new ArrayList<UserAccount>();
            this.candidates = new DualListModel<UserAccount>(usersGroup, reps);
        }
    }

    @SuppressWarnings("rawtypes")
    public String save() {
        Country country = this.locationBean.getCountry();
        if (country != null) {
            this.partner.setCountry(country);
        }

        Province province = this.locationBean.getProvince();
        if (province != null) {
            this.partner.setProvince(province);
        }

        City city = this.locationBean.getCity();
        if (city != null) {
            this.partner.setCity(city);
        }
        
        List<UserAccount> reps = new ArrayList<UserAccount>();
        List selectedCandidates = this.candidates.getTarget();
        UserAccount userAccount;
        for(int i = 0;i < selectedCandidates.size();i++) {
            userAccount = new UserAccount(((UserAccount)selectedCandidates.get(i)).getId());
            reps.add(userAccount);
        }

        representativeBsn.save(this.partner, reps);
        
        return "partners?faces-redirect=true";
    }

    public String remove() {
        partnerBsn.remove(this.partner.getId());
        return "partners?faces-redirect=true";
    }
}