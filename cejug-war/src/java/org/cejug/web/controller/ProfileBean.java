package org.cejug.web.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;

@ManagedBean
@RequestScoped
public class ProfileBean {

    @EJB
    private UserAccountBsn userAccountBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private UserAccount userAccount;

    public ProfileBean() {}

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.userAccount = userAccountBsn.findUserAccount(this.id);
        }
        else {
            this.userAccount = new UserAccount();
        }
    }
}