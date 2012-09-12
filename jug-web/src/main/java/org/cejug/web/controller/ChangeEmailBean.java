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
import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class ChangeEmailBean {

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    @ManagedProperty(value="#{id}")
    private String id;
    
    @ManagedProperty(value="#{param.cc}")
    private String confirmationCode;
    
    private UserAccount userAccount;
    private String currentEmail;
    private String newEmail;
    private String newEmailConfirmation;

    public ChangeEmailBean() {
        userAccount = new UserAccount();
    }

    /**
     * @return the user account's id.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    /**
     * @return the userAccount that wants to change the password.
     */
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the currentEmail that will be changed.
     */
    public String getCurrentEmail() {
        return currentEmail;
    }

    public void setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
    }

    /**
     * @return the newEmail that will replace the currentEmail.
     */
    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    /**
     * @return the newEmailConfirmation to minimize the risk of typos in the address.
     */
    public String getNewEmailConfirmation() {
        return newEmailConfirmation;
    }

    public void setNewEmailConfirmation(String newEmailConfirmation) {
        this.newEmailConfirmation = newEmailConfirmation;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.userAccount = userAccountBsn.findUserAccountByConfirmationCode(id);
        }
        else if(confirmationCode != null && !confirmationCode.isEmpty()) {
            this.userAccount = userAccountBsn.findUserAccountByConfirmationCode(confirmationCode);
        }
    }
    
    /**
     * Compares the informed email with its respective confirmation.
     * @return true if the email matches with its confirmation.
     */
    private boolean isEmailConfirmed() {
        return newEmail.equals(newEmailConfirmation);
    }

    /**
     * It changes the user's email.
     * @return returns the next step in the navigation flow.
     */
    public String changeEmail() {
        
        // If password doesn't match its confirmation.
        if(!isEmailConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The email confirmation does not match."));
            return "change_password";
        }

        userAccountBsn.changeEmail(userAccount, this.newEmail);
        return "profile?faces-redirect=true";
    }
    
    public String confirmEmailChange() {
        this.userAccount.resetConfirmationCode();
        userAccountBsn.save(this.userAccount);
        return "index?faces-redirect=true";
    }
}