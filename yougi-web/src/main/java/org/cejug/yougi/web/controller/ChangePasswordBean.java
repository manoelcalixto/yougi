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
package org.cejug.yougi.web.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.cejug.yougi.business.ApplicationPropertyBsn;
import org.cejug.yougi.business.UserAccountBsn;
import org.cejug.yougi.entity.ApplicationProperty;
import org.cejug.yougi.entity.Authentication;
import org.cejug.yougi.entity.Properties;
import org.cejug.yougi.entity.UserAccount;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class ChangePasswordBean {

    @EJB
    private UserAccountBsn userAccountBsn;

    @ManagedProperty(value="#{param.cc}")
    private String confirmationCode;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    private String currentPassword;
    private String username;
    private String password;
    private String confirmPassword;
    private Boolean invalid;

    public ChangePasswordBean() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    @PostConstruct
    public void load() {
        if(confirmationCode != null && !confirmationCode.isEmpty()) {
            UserAccount userAccount = userAccountBsn.findUserAccountByConfirmationCode(confirmationCode);
            Authentication authentication = userAccountBsn.findAuthenticationUser(userAccount);
            if(userAccount != null)
                this.username = authentication.getUsername();
            else
                invalid = true;
        }
    }

    /**
     * @return returns the next step in the navigation flow.
     */
    public String requestPasswordChange() {
        try {
            ApplicationProperty url = applicationPropertyBsn.findApplicationProperty(Properties.URL);
            String serverAddress = url.getPropertyValue();
            userAccountBsn.requestConfirmationPasswordChange(username, serverAddress);
        }
        catch(EJBException ee) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ee.getCausedByException().getMessage()));
            return "request_password_change";
        }
        return "change_password";
    }
    
    /**
     * Compares the informed password with its respective confirmation.
     * @return true if the password matches with its confirmation.
     */
    private boolean isPasswordConfirmed() {
        return password.equals(confirmPassword);
    }

    /**
     * It changes the password in case the user has forgotten it. It checks whether
     * the confirmation code sent to the user's email is valid before proceeding
     * with the password change.
     * @return returns the next step in the navigation flow.
     */
    public String changeForgottenPassword() {
        UserAccount userAccount = userAccountBsn.findUserAccountByConfirmationCode(confirmationCode);
        
        if(userAccount == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The confirmation code does not match."));
            return "change_password";
        }
                
        if(!isPasswordConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The password confirmation does not match."));
            return "change_password";
        }

        userAccountBsn.changePassword(userAccount, this.password);
        return "login?faces-redirect=true";
    }

    /**
     * It changes the password in case the user still knows his(er) own password.
     * @return returns the next step in the navigation flow.
     */
    public String changePassword() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        username = request.getRemoteUser();
        UserAccount userAccount = userAccountBsn.findUserAccountByUsername(username);
        if(!userAccountBsn.passwordMatches(userAccount, currentPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The current password does not match."));
            return "change_password";
        }

        // If password doesn't match its confirmation.
        if(!isPasswordConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The password confirmation does not match."));
            return "change_password";
        }

        userAccountBsn.changePassword(userAccount, this.password);
        return "profile?faces-redirect=true";
    }
}