package org.cejug.partnership.web.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.partnership.business.RepresentativeBsn;
import org.cejug.partnership.entity.Representative;

@ManagedBean
@RequestScoped
public class RepresentativeBean {

    @EJB
    private RepresentativeBsn representativeBsn;
    
    @EJB
    private UserAccountBsn userAccountBsn;
    
    @ManagedProperty(value="#{param.id}")
    private String id;

    private Representative representative;

    public RepresentativeBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.representative = representativeBsn.findRepresentative(id);
        }
        else {
        	HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String username = request.getRemoteUser();
            UserAccount person = userAccountBsn.findUserAccountByUsername(username);
            if(person != null)
            	this.representative = representativeBsn.findRepresentative(person);
        }
    }

    public String save() {
        representativeBsn.save(this.representative);
        return "profile?faces-redirect=true";
    }

    public String remove() {
        representativeBsn.remove(representative.getId());
        return "profile?faces-redirect=true";
    }
}