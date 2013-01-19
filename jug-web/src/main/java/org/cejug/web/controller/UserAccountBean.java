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

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.Authentication;
import org.cejug.entity.City;
import org.cejug.entity.DeactivationType;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundleHelper;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class UserAccountBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    @ManagedProperty(value="#{locationBean}")
    private LocationBean locationBean;

    private String userId;
    private UserAccount userAccount;

    private String password;
    private String passwordConfirmation;

    public UserAccountBean() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * That is not a entity property and it will not be saved in the database.
     * It is used only to check if the user properly typed his password.
     */
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public Boolean getNoAccount() {
        return userAccountBsn.thereIsNoAccount();
    }

    public boolean isConfirmed() {
        if(userAccount.getConfirmationCode() == null || userAccount.getConfirmationCode().isEmpty())
            return true;
        return false;
    }

    public void validateUserId(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
        String usrId = (String) value;
        if(-1 == usrId.indexOf("@")) {
            throw new ValidatorException(new FacesMessage("Invalid email address."));
        }
    }

    @PostConstruct
    public void load() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String username = request.getRemoteUser();
        if(username != null) {
            this.userAccount = userAccountBsn.findUserAccountByUsername(username);

            if(this.userAccount.getCountry() != null) {
                locationBean.setSelectedCountry(this.userAccount.getCountry().getAcronym());
            }
            else {
                locationBean.setSelectedCountry(null);
            }

            if(this.userAccount.getProvince() != null) {
                locationBean.setSelectedProvince(this.userAccount.getProvince().getId());
            }
            else {
                locationBean.setSelectedProvince(null);
            }

            if(this.userAccount.getCity() != null) {
                locationBean.setSelectedCity(this.userAccount.getCity().getId());
            }
            else {
                locationBean.setSelectedCity(null);
            }
        }
        else {
            this.userAccount = new UserAccount();
        }
    }

    public String register() {
        FacesContext context = FacesContext.getCurrentInstance();
        ResourceBundleHelper bundle = new ResourceBundleHelper();

        if(!userAccount.isEmailConfirmed()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,bundle.getMessage("errorCode0003"),""));
            context.validationFailed();
        }

        if(userAccountBsn.existingAccount(this.userAccount.getUnverifiedEmail())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,bundle.getMessage("errorCode0004"),""));
            context.validationFailed();
        }

        if(!isPasswordConfirmed()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,bundle.getMessage("errorCode0005"),""));
            context.validationFailed();
        }

        boolean isFirstUser = userAccountBsn.thereIsNoAccount();

        if(!isFirstUser && this.locationBean.getCity() == null && (this.locationBean.getCityNotListed() == null || this.locationBean.getCityNotListed().isEmpty())) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getMessage("errorCode0006"),""));
            context.validationFailed();
        }

        if(!isFirstUser && !isPrivacyValid(userAccount)) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getMessage("errorCode0007"),""));
            context.validationFailed();
        }

        if(context.isValidationFailed()) {
            return "registration";
        }

        this.userAccount.setCountry(this.locationBean.getCountry());
    	this.userAccount.setProvince(this.locationBean.getProvince());
    	this.userAccount.setCity(this.locationBean.getCity());

        City newCity = locationBean.getNotListedCity();

        Authentication authentication = new Authentication();
        try {
            authentication.setUserAccount(this.userAccount);
            authentication.setUsername(userAccount.getUnverifiedEmail());
            authentication.setPassword(this.password);
            userAccountBsn.register(userAccount, authentication, newCity);
        }
        catch(Exception e) {
            context.addMessage(userId, new FacesMessage(e.getCause().getMessage()));
            return "registration";
        }

        if(isFirstUser) {
            context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoSuccessfulRegistration"), ""));
            return "login";
        }
        else {
            context.addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoRegistrationConfirmationRequest"), ""));
            return "registration_confirmation";
        }
    }

    /**
     * Compares the informed password with its respective confirmation.
     * @return true if the password matches with its confirmation.
     */
    private boolean isPasswordConfirmed() {
        return this.password.equals(passwordConfirmation);
    }

    public String savePersonalData() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBsn.findUserAccount(userAccount.getId());

            existingUserAccount.setCountry(this.locationBean.getCountry());
            existingUserAccount.setProvince(this.locationBean.getProvince());
            existingUserAccount.setCity(this.locationBean.getCity());

            existingUserAccount.setFirstName(userAccount.getFirstName());
            existingUserAccount.setLastName(userAccount.getLastName());
            existingUserAccount.setGender(userAccount.getGender());
            existingUserAccount.setBirthDate(userAccount.getBirthDate());
            userAccountBsn.save(existingUserAccount);

            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().remove("locationBean");
        }
        return "profile?faces-redirect=true";
    }

    public String savePrivacy() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBsn.findUserAccount(userAccount.getId());
            existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
            existingUserAccount.setMailingList(userAccount.getMailingList());
            existingUserAccount.setNews(userAccount.getNews());
            existingUserAccount.setGeneralOffer(userAccount.getGeneralOffer());
            existingUserAccount.setJobOffer(userAccount.getJobOffer());
            existingUserAccount.setEvent(userAccount.getEvent());
            existingUserAccount.setSponsor(userAccount.getSponsor());
            existingUserAccount.setSpeaker(userAccount.getSpeaker());

            if(!isPrivacyValid(existingUserAccount)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selecione pelo menos uma das opções de privacidade."));
                return "privacy";
            }

            userAccountBsn.save(existingUserAccount);
        }
        return "profile?faces-redirect=true";
    }

    public String deactivateMembership() {
        userAccountBsn.deactivateMembership(userAccount, DeactivationType.OWNWILL);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        try {
            request.logout();
            session.invalidate();
        }
        catch(ServletException se) {}

        return "/index?faces-redirect=true";
    }

    /** Check whether at least one of the privacy options was checked. */
    private boolean isPrivacyValid(UserAccount userAccount) {
        if(userAccount.getPublicProfile() ||
             userAccount.getMailingList() ||
             userAccount.getEvent() ||
             userAccount.getNews() ||
             userAccount.getGeneralOffer() ||
             userAccount.getJobOffer() ||
             userAccount.getSponsor() ||
             userAccount.getSpeaker()) { return true; }
        return false;
    }
}