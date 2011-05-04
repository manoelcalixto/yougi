package org.cejug.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.business.LocationBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.City;
import org.cejug.entity.Contact;
import org.cejug.entity.Country;
import org.cejug.entity.Properties;
import org.cejug.entity.Province;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundle;

@ManagedBean
@SessionScoped
public class UserAccountBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private LocationBsn locationBsn;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    private String userId;
    private String cityNotListed;
    private UserAccount userAccount;
    private Contact contact;
    
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

    public List<String> getCitiesStartingWith(String initials) {
        List<City> cities = locationBsn.findCitiesStartingWith(initials);
        List<String> citiesStartingWith = new ArrayList<String>();
        for(City city:cities) {
            citiesStartingWith.add(city.getName());
        }
        return citiesStartingWith;
    }

    public String getSelectedCountry() {
        if(contact.getCountry() == null)
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
        if(this.contact.getProvince() == null)
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
        if(this.contact.getCity() == null)
            return null;

        return contact.getCity().getId();
    }

    public void setSelectedCity(String id) {
        if(id == null || id.isEmpty())
            return;

        if(this.contact.getCity() == null || !this.contact.getCity().getId().equals(id))
            this.contact.setCity(locationBsn.findCity(id));
    }

    public String getCityNotListed() {
        return cityNotListed;
    }

    public Boolean getNoAccount() {
        return userAccountBsn.noAccount();
    }

    public void setCityNotListed(String cityNotListed) {
        this.cityNotListed = cityNotListed;
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
            this.contact = this.userAccount.getMainContact();
        }
        else {
            this.userAccount = new UserAccount();
            this.contact = new Contact();
        }
    }

    public String register() {
        if(!userAccount.isEmailConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("A confirmação de email não está coincidindo com o email informado."));
            return "registration";
        }

        if(!userAccount.isPasswordConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("A confirmação de senha não está coincidindo com a senha informada."));
            return "registration";
        }

        if(userAccountBsn.existingAccount(userAccount)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Um membro com o endereço de email \""+ userAccount.getEmail() +"\" já está registrado."));
            return "registration";
        }

        if(!isPrivacyValid(userAccount)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selecione pelo menos uma das opções de privacidade."));
            return "registration";
        }
        
        boolean isFirstUser = userAccountBsn.noAccount();

        ApplicationProperty url = applicationPropertyBsn.findApplicationProperty(Properties.URL);
        String serverAddress = url.getPropertyValue();

        City newCity = null;
        if(this.cityNotListed != null && !this.cityNotListed.isEmpty() && contact.getCountry() != null) {
            newCity = new City(null, this.cityNotListed);
            newCity.setCountry(contact.getCountry());
            newCity.setProvince(contact.getProvince());
            newCity.setValid(false);
        }

        try {
            userAccount.setUsername(userAccount.getEmail());
            userAccountBsn.register(userAccount, contact, newCity, serverAddress);
        }
        catch(Exception e) {
            FacesContext.getCurrentInstance().addMessage(userId, new FacesMessage(e.getCause().getMessage()));
            return "registration";
        }

        removeSessionScoped();
        ResourceBundle bundle = new ResourceBundle();
        if(isFirstUser) {
            FacesContext.getCurrentInstance().addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoSuccessfulRegistration"), ""));
        }
        else
            FacesContext.getCurrentInstance().addMessage(userId, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoRegistrationConfirmationRequest"), ""));
        return "registration_confirmation";
    }

    public String savePersonalData() {
        if(userAccount != null) {
            UserAccount existingUserAccount = userAccountBsn.findUserAccount(userAccount.getId());
            existingUserAccount.setMainContact(contact);
            existingUserAccount.setFirstName(userAccount.getFirstName());
            existingUserAccount.setLastName(userAccount.getLastName());
            existingUserAccount.setGender(userAccount.getGender());
            existingUserAccount.setBirthDate(userAccount.getBirthDate());
            userAccountBsn.save(existingUserAccount);
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

            if(!isPrivacyValid(existingUserAccount)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Selecione pelo menos uma das opções de privacidade."));
                return "privacy";
            }

            userAccountBsn.save(existingUserAccount);
        }
        return "profile?faces-redirect=true";
    }

    public String deactivateMembership() {
        userAccountBsn.deactivateOwnMembership(userAccount);
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        try {
            request.logout();
            session.invalidate();
        }
        catch(ServletException se) {}
        
        return "/index?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("userAccountBean");
    }

    /** Check whether at least one of the privacy options was checked. */
    private boolean isPrivacyValid(UserAccount userAccount) {
        if(userAccount.getPublicProfile() || 
             userAccount.getMailingList() ||
             userAccount.getEvent() ||
             userAccount.getNews() ||
             userAccount.getGeneralOffer() ||
             userAccount.getJobOffer() ||
             userAccount.getSponsor()) {
            
            return true;
        }
        return false;
    }
}