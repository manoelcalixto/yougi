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
package org.cejug.web.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundleHelper;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class RegistrationConfirmationBean {

    @EJB
    private UserAccountBsn userAccountBsn;

    private UserAccount userAccount;

    @ManagedProperty(value="#{param.code}")
    private String code;
    
    private String informedCode;
    
    private Boolean validated;

    public RegistrationConfirmationBean() {
        this.userAccount = new UserAccount();
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the code informed manually by the user.
     */
    public String getInformedCode() {
        return informedCode;
    }

    public void setInformedCode(String informedCode) {
        this.informedCode = informedCode;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the whether the registration was validated.
     */
    public Boolean getValidated() {
        return validated;
    }

    public void setValidated(Boolean validated) {
        this.validated = validated;
    }
    
    /**
     * Validates the registration when the confirmation code comes 
     * through querystring.
     */
    @PostConstruct
    public void load() {
        if(this.code != null && !this.code.isEmpty()) {
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundleHelper bundle = new ResourceBundleHelper();
            this.userAccount = userAccountBsn.confirmUser(this.code);
            if(this.userAccount != null)
                this.validated = Boolean.TRUE;
            else {
                this.validated = Boolean.FALSE;
                context.addMessage(this.informedCode, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getMessage("warnCode0003"), ""));
            }
        }
    }
    
    /**
     * Validates the registration when the confirmation code is 
     * manually informed.
     */
    public String confirmUser() {
        if(this.informedCode != null && !this.informedCode.isEmpty()) {
            this.userAccount = userAccountBsn.confirmUser(this.informedCode);
            if(this.userAccount != null)
                this.validated = Boolean.TRUE;
            else
                this.validated = Boolean.FALSE;
            
            FacesContext context = FacesContext.getCurrentInstance();
            ResourceBundleHelper bundle = new ResourceBundleHelper();
            if(!this.validated)
                context.addMessage(this.informedCode, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getMessage("warnCode0003"), ""));
        }
        return "registration_confirmation";
    }
}