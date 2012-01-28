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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.event.business.AttendeeBsn;
import org.cejug.event.entity.Event;
import org.cejug.knowledge.business.MailingListBsn;
import org.cejug.knowledge.business.SubscriptionBsn;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.knowledge.entity.MailingListSubscription;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@SessionScoped
public class MemberBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private MailingListBsn mailingListBsn;
    
    @EJB
    private SubscriptionBsn subscriptionBsn;

    @EJB
    private AttendeeBsn attendeeBsn;

    @ManagedProperty(value = "#{locationBean}")
    private LocationBean locationBean;

    private List<UserAccount> userAccounts;

    private List<MailingList> mailingLists;

    private List<Event> attendedEvents;

    private String userId;

    private UserAccount userAccount;

    private String emailCriteria;

    private String firstLetterCriteria;

    private MailingList[] selectedMailingLists;

    public MemberBean() {
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

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public List<UserAccount> getUserAccounts() {
        return this.userAccounts;
    }

    public List<MailingList> getMailingLists() {
        return mailingLists;
    }

    public void setMailingLists(List<MailingList> mailingLists) {
        this.mailingLists = mailingLists;
    }

    public List<Event> getAttendedEvents() {
        return this.attendedEvents;
    }

    public List<UserAccount> getDeactivatedUserAccounts() {
        List<UserAccount> deactivatedUsers = userAccountBsn.findDeactivatedUserAccounts();
        return deactivatedUsers;
    }

    public String findUserAccountByEmail() {
        if (this.emailCriteria == null || this.emailCriteria.isEmpty()) {
            this.userAccounts = userAccountBsn.findNotVerifiedUsers();
        } else {
            List<UserAccount> uas = new ArrayList<UserAccount>(1);
            UserAccount ua = userAccountBsn.findUserAccountByEmail(this.emailCriteria);
            if (ua != null) {
                uas.add(ua);
            }
            this.userAccounts = uas;
        }
        this.firstLetterCriteria = null;
        return "users?faces-redirect=true";
    }

    public String findUserAccountByFirstLetter(String firstLetterCriteria) {
        if (firstLetterCriteria == null || firstLetterCriteria.isEmpty()) {
            this.userAccounts = userAccountBsn.findNotVerifiedUsers();
        } else {
            this.firstLetterCriteria = firstLetterCriteria;
            this.userAccounts = userAccountBsn.findUserAccountsStartingWith(this.firstLetterCriteria);
            this.emailCriteria = null;
        }

        return "users?faces-redirect=true";
    }

    public MailingList[] getSelectedMailingLists() {
        return selectedMailingLists;
    }

    public void setSelectedMailingLists(MailingList[] selectedMailingLists) {
        this.selectedMailingLists = selectedMailingLists;
    }

    public String getEmailCriteria() {
        return emailCriteria;
    }

    public void setEmailCriteria(String emailCriteria) {
        this.emailCriteria = emailCriteria;
    }

    public String getFirstLetterCriteria() {
        return firstLetterCriteria;
    }

    public void setFirstLetterCriteria(String firstLetterCriteria) {
        this.firstLetterCriteria = firstLetterCriteria;
    }

    public boolean isConfirmed() {
        if (userAccount.getConfirmationCode() == null || userAccount.getConfirmationCode().isEmpty()) {
            return true;
        }
        return false;
    }

    public void validateUserId(FacesContext context, UIComponent toValidate, Object value) throws ValidatorException {
        String usrId = (String) value;
        if (-1 == usrId.indexOf("@")) {
            throw new ValidatorException(new FacesMessage("Invalid email address."));
        }
    }

    @PostConstruct
    public void load() {
        this.userAccounts = userAccountBsn.findNotVerifiedUsers();
    }

    public String load(String userId) {
        this.userId = userId;
        this.userAccount = userAccountBsn.findUserAccount(this.userId);
        this.mailingLists = mailingListBsn.findMailingLists();
        this.attendedEvents = attendeeBsn.findAttendeedEvents(this.userAccount);

        locationBean.initialize();

        if (this.userAccount.getCountry() != null) {
            locationBean.setSelectedCountry(this.userAccount.getCountry().getAcronym());
        }

        if (this.userAccount.getProvince() != null) {
            locationBean.setSelectedProvince(this.userAccount.getProvince().getId());
        }

        if (this.userAccount.getCity() != null) {
            locationBean.setSelectedCity(this.userAccount.getCity().getId());
        }

        List<MailingListSubscription> mailingListSubscriptions = subscriptionBsn.findMailingListSubscriptions(this.userAccount);
        if (mailingListSubscriptions != null) {
            this.selectedMailingLists = new MailingList[mailingListSubscriptions.size()];
            int i = 0;
            for (MailingListSubscription mailingListSubscription : mailingListSubscriptions) {
                this.selectedMailingLists[i++] = mailingListSubscription.getMailingList();
            }
        }

        return "user?faces-redirect=true";
    }

    public String save() {
        save(null);
        return "users?faces-redirect=true";
    }

    private void save(Boolean verified) {
        UserAccount existingUserAccount = userAccountBsn.findUserAccount(userAccount.getId());

        existingUserAccount.setCountry(this.locationBean.getCountry());
        existingUserAccount.setProvince(this.locationBean.getProvince());
        existingUserAccount.setCity(this.locationBean.getCity());

        existingUserAccount.setFirstName(userAccount.getFirstName());
        existingUserAccount.setLastName(userAccount.getLastName());
        existingUserAccount.setGender(userAccount.getGender());
        existingUserAccount.setBirthDate(userAccount.getBirthDate());

        existingUserAccount.setPublicProfile(userAccount.getPublicProfile());
        existingUserAccount.setMailingList(userAccount.getMailingList());
        existingUserAccount.setNews(userAccount.getNews());
        existingUserAccount.setGeneralOffer(userAccount.getGeneralOffer());
        existingUserAccount.setJobOffer(userAccount.getJobOffer());
        existingUserAccount.setEvent(userAccount.getEvent());
        existingUserAccount.setSponsor(userAccount.getSponsor());
        existingUserAccount.setSpeaker(userAccount.getSpeaker());

        if (verified != null) {
            existingUserAccount.setVerified(verified);
        }

        List<MailingList> mailingListsToSubscribe = new ArrayList<MailingList>();
        mailingListsToSubscribe.addAll(Arrays.asList(this.selectedMailingLists));
        subscriptionBsn.subscribe(mailingListsToSubscribe, existingUserAccount);

        userAccountBsn.save(existingUserAccount);
    }

    public String deactivateMembership() {
        userAccountBsn.deactivateMembership(userAccount);
        return "users?faces-redirect=true";
    }

    public String confirm() {
        try {
            userAccountBsn.confirmUser(userAccount.getConfirmationCode());
        } catch (IllegalArgumentException iae) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(iae.getMessage()));
            return "user";
        }
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    public String checkUserAsVerified() {
        save(Boolean.TRUE);
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("memberBean");
    }
}