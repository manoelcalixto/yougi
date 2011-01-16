package org.cejug.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import org.cejug.business.LocationBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.City;
import org.cejug.entity.Contact;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.entity.UserAccount;
import org.cejug.web.report.CommunicationPrivacyRange;
import org.cejug.web.report.MembershipGrowthRange;

@ManagedBean
@SessionScoped
public class MemberBean implements Serializable {

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private LocationBsn locationBsn;

    private String userId;
    private UserAccount userAccount;
    private Contact contact;
    private String firstLetterCriteria;
    
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

    public List<SelectItem> getCountries() {
        List<Country> countries = locationBsn.findCountries();
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        SelectItem selectItem = new SelectItem("", "Select...");
        selectItems.add(selectItem);
        for(Country country: countries) {
            selectItem = new SelectItem(country.getAcronym(), country.getName());
            selectItems.add(selectItem);
        }
        return selectItems;
    }

    public SelectItem[] getProvinces() {
        List<Province> provinces = locationBsn.findProvinces(contact.getCountry());
        SelectItem[] selectItems = new SelectItem[provinces.size() + 1];
        SelectItem selectItem = new SelectItem("", "Select...");
        int i = 0;
        selectItems[i++] = selectItem;
        for(Province province: provinces) {
            selectItem = new SelectItem(province.getId(), province.getName());
            selectItems[i++] = selectItem;
        }
        return selectItems;
    }

    public SelectItem[] getCities() {
        List<City> cities;
        if(contact.getProvince() != null)
            cities = locationBsn.findCities(contact.getProvince(), false);
        else
            cities = locationBsn.findCities(contact.getCountry(), false);
        SelectItem[] selectItems = new SelectItem[cities.size() + 1];
        SelectItem selectItem = new SelectItem("", "Select...");
        int i = 0;
        selectItems[i++] = selectItem;
        for(City city: cities) {
            selectItem = new SelectItem(city.getId(), city.getName());
            selectItems[i++] = selectItem;
        }
        return selectItems;
    }

    public List<UserAccount> getUserAccounts() {
        if(firstLetterCriteria != null && !firstLetterCriteria.isEmpty())
            return userAccountBsn.findUserAccountsStartingWith(firstLetterCriteria);
        return getRecentUserAccounts();
    }

    public List<UserAccount> getRecentUserAccounts() {
        return userAccountBsn.findRegisteredUsersSince(getLastSevenDays());
    }

    public List<Date> getScheduledAccountMaintenances() {
        return userAccountBsn.getScheduledAccountMaintenances();
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

    public String getSelectedCountry() {
        if(contact == null || contact.getCountry() == null)
            return null;

        return contact.getCountry().getAcronym();
    }

    public void setSelectedCountry(String acronym) {
        if(acronym == null || acronym.isEmpty())
            return;

        if(this.contact.getCountry() == null || !this.contact.getCountry().getAcronym().equals(acronym)) {
            this.contact.setCountry(locationBsn.findCountry(acronym));
            this.contact.setProvince(null);
            this.contact.setCity(null);
        }
    }

    public String getSelectedProvince() {
        if(this.contact == null || this.contact.getProvince() == null)
            return null;

        return contact.getProvince().getId();
    }

    public void setSelectedProvince(String id) {
        if(id == null || id.isEmpty()) {
            this.contact.setProvince(null);
            return;
        }

        if(this.contact.getProvince() == null || !this.contact.getProvince().getId().equals(id)) {
            this.contact.setProvince(locationBsn.findProvince(id));
            this.contact.setCity(null);
        }
    }

    public String getSelectedCity() {
        if(this.contact == null || this.contact.getCity() == null)
            return null;

        return contact.getCity().getId();
    }

    public void setSelectedCity(String id) {
        if(id == null || id.isEmpty())
            return;

        if(this.contact.getCity() == null || !this.contact.getCity().getId().equals(id))
            this.contact.setCity(locationBsn.findCity(id));
    }

    public String getFirstLetterCriteria() {
        return firstLetterCriteria;
    }

    public String setFirstLetterCriteria(String firstLetterCriteria) {
        this.firstLetterCriteria = firstLetterCriteria;
        return "users?faces-redirect=true";
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

    public String load(String userId) {
        this.userId = userId;
        this.userAccount = userAccountBsn.findUserAccount(this.userId);
        this.contact = this.userAccount.getMainContact();
        if(this.contact == null) {
            this.contact = new Contact(this.userAccount);
        }
        return "user?faces-redirect=true";
    }

    public String save() {
        UserAccount existingUserAccount = userAccountBsn.findUserAccount(userAccount.getId());

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

        userAccountBsn.save(existingUserAccount);
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

    public String scheduleAccountMaintenance() {
        userAccountBsn.scheduleAccountMaintenance();
        return "users?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("memberBean");
    }
}