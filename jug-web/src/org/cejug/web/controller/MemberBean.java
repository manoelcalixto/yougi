package org.cejug.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
import org.cejug.entity.Contact;
import org.cejug.entity.UserAccount;
import org.cejug.knowledge.business.MailingListBsn;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.knowledge.entity.MailingListSubscription;
import org.cejug.web.report.CommunicationPrivacyRange;
import org.cejug.web.report.MembershipGrowthRange;

@ManagedBean
@SessionScoped
public class MemberBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private MailingListBsn mailingListBsn;
    
    @ManagedProperty(value="#{locationBean}")
    private LocationBean locationBean;

    private List<UserAccount> userAccounts;
    private List<MailingList> mailingLists;

    private String userId;
    private UserAccount userAccount;
    private Contact contact;
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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
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

    public List<UserAccount> getRecentUserAccounts() {
        return userAccountBsn.findRegisteredUsersSince(getLastSevenDays());
    }

    public List<UserAccount> getDeactivatedUserAccounts() {
        List<UserAccount> deactivatedUsers = userAccountBsn.findDeactivatedUserAccounts();
        return deactivatedUsers;
    }

    public String findUserAccountByEmail() {
        List<UserAccount> uas = new ArrayList<UserAccount>(1);
        UserAccount ua = userAccountBsn.findUserAccountByEmail(this.emailCriteria);
        if(ua != null) {
            uas.add(ua);
        }
        this.userAccounts = uas;
        this.firstLetterCriteria = null;
        return "users?faces-redirect=true";
    }

    public String findUserAccountByFirstLetter(String firstLetterCriteria) {
        this.firstLetterCriteria = firstLetterCriteria;
        this.userAccounts = userAccountBsn.findUserAccountsStartingWith(this.firstLetterCriteria);
        this.emailCriteria = null;

        return "users?faces-redirect=true";
    }

    public List<CommunicationPrivacyRange> getCommunicationPrivacyRanges() {
        return CommunicationPrivacyRange.generateSeries(userAccountBsn.findUserAccounts());
    }

    public List<MembershipGrowthRange> getMembershipGrowthRanges() {
        return MembershipGrowthRange.generateSeries(userAccountBsn.findUserAccountsOrderedByRegistration());
    }

    public Date getLastSevenDays() {
        Calendar sevenDaysAgo = Calendar.getInstance();
        sevenDaysAgo.add(Calendar.DAY_OF_YEAR, -7);
        return sevenDaysAgo.getTime();
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
        this.userAccounts = getRecentUserAccounts();
        this.mailingLists = mailingListBsn.findMailingLists();
    }

    public String load(String userId) {
        this.userId = userId;
        this.userAccount = userAccountBsn.findUserAccount(this.userId);
        this.contact = this.userAccount.getMainContact();
        if(this.contact == null) {
            this.contact = new Contact(this.userAccount);
        }
        
        locationBean.initialize();
    	
        if(this.contact.getCountry() != null)
        	locationBean.setSelectedCountry(this.contact.getCountry().getAcronym());
        
        if(this.contact.getProvince() != null)
        	locationBean.setSelectedProvince(this.contact.getProvince().getId());
        
        if(this.contact.getCity() != null)
        	locationBean.setSelectedCity(this.contact.getCity().getId());

        List<MailingListSubscription> mailingListSubscriptions = mailingListBsn.findMailingListSubscriptions(this.userAccount);
        if(mailingListSubscriptions != null) {
            this.selectedMailingLists = new MailingList[mailingListSubscriptions.size()];
            int i = 0;
            for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
                this.selectedMailingLists[i++] = mailingListSubscription.getMailingList();
            }
        }

        return "user?faces-redirect=true";
    }

    public String save() {
        UserAccount existingUserAccount = userAccountBsn.findUserAccount(userAccount.getId());

    	this.contact.setCountry(this.locationBean.getCountry());
    	this.contact.setProvince(this.locationBean.getProvince());
    	this.contact.setCity(this.locationBean.getCity());
    	        
        existingUserAccount.setMainContact(this.contact);
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

        List<MailingList> mailingListsToSubscribe = new ArrayList<MailingList>();
        mailingListsToSubscribe.addAll(Arrays.asList(this.selectedMailingLists));
        mailingListBsn.subscribe(mailingListsToSubscribe, existingUserAccount);
        
        userAccountBsn.save(existingUserAccount);
        
        //FacesContext context = FacesContext.getCurrentInstance();
        //context.getExternalContext().getSessionMap().remove("locationBean");
        
        return "users?faces-redirect=true";
    }

    public String deactivateMembership() {
        userAccountBsn.deactivateMembership(userAccount);
        return "users?faces-redirect=true";
    }

    public String confirm() {
        try {
            userAccountBsn.confirmUser(userAccount.getConfirmationCode());
        } catch(IllegalArgumentException iae) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(iae.getMessage()));
            return "user";
        }
        removeSessionScoped();
        return "users?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("memberBean");
    }
}