package org.cejug.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.cejug.util.TextUtils;

/**
 * Represents the user account of jug members.
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name="user_account")
public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    
    private String email;
    
    private String username;
    
    @Column(nullable=false)
    private String password;

    @Column(name="first_name", nullable=false)
    private String firstName;

    @Column(name="last_name", nullable=false)
    private String lastName;

    @Column(nullable=false)
    private Integer gender;
    
    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name="birth_date",nullable=false)
    private Date birthDate;

    @Column(name="confirmation_code")
    private String confirmationCode;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="registration_date")
    private Date registrationDate;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="last_update")
    private Date lastUpdate;
    
    private Boolean deactivated = false;
    
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="deactivation_date")
    private Date deactivationDate;
    
    @Column(name="deactivation_reason")
    private String deactivationReason;

    @Enumerated(EnumType.ORDINAL)
    @Column(name="deactivation_type")
    private DeactivationType deactivationType;

    private String website;
    
    private String twitter;
    
    @ManyToOne
    @JoinColumn(name="country")
    private Country country;
    
    @ManyToOne
    @JoinColumn(name="province")
    private Province province;
    
    @ManyToOne
    @JoinColumn(name="city")
    private City city;
    
    @Column(name="postal_code")
    private String postalCode;
    
    @Column(name = "public_profile")
    private Boolean publicProfile;
    
    @Column(name = "mailing_list")
    private Boolean mailingList;
    
    private Boolean news;
    
    @Column(name="general_offer")
    private Boolean generalOffer;

    @Column(name = "job_offer")
    private Boolean jobOffer;
    
    private Boolean event;
    
    private Boolean sponsor;
    
    private Boolean speaker;
    
    private Boolean verified;

    @Transient
    private String confirmPassword;
    
    @Transient
    private String confirmEmail;
    
    public UserAccount() {}

    public UserAccount(String id) {
        this.id = id;
    }

    public UserAccount(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean isPasswordConfirmed() {
        return confirmPassword.equals(password);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        firstName = TextUtils.capitalizeFirstCharWords(firstName);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        lastName = TextUtils.capitalizeFirstCharWords(lastName);
        this.lastName = lastName;
    }

    public String getFullName() {
        StringBuilder str = new StringBuilder();
        str.append(firstName);
        str.append(" ");
        str.append(lastName);
        return str.toString();
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public int getAge() {
        if(this.birthDate != null) {
            Date today = Calendar.getInstance().getTime();
            return (int)(((((today.getTime() - birthDate.getTime()) / 1000) / 60) / 60) / 24) / 365;
        }
        return 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email.toLowerCase();
        this.email = email;
        this.setUsername(email);
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getDeactivated() {
        if(deactivated != null)
            return deactivated;
        else
            return false;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public Date getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Date deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public DeactivationType getDeactivationType() {
        return deactivationType;
    }

    public void setDeactivationType(DeactivationType deactivationType) {
        this.deactivationType = deactivationType;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Boolean getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Boolean speaker) {
        this.speaker = speaker;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public Boolean getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(Boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    public Boolean getMailingList() {
        return mailingList;
    }

    public void setMailingList(Boolean mailingList) {
        this.mailingList = mailingList;
    }

    public Boolean getNews() {
        return news;
    }

    public void setNews(Boolean news) {
        this.news = news;
    }

    public Boolean getGeneralOffer() {
        return generalOffer;
    }

    public void setGeneralOffer(Boolean generalOffer) {
        this.generalOffer = generalOffer;
    }

    public Boolean getJobOffer() {
        return jobOffer;
    }

    public void setJobOffer(Boolean jobOffer) {
        this.jobOffer = jobOffer;
    }

    public Boolean getEvent() {
        return event;
    }

    public void setEvent(Boolean event) {
        this.event = event;
    }

    public Boolean getSponsor() {
        return sponsor;
    }

    public void setSponsor(Boolean sponsor) {
        this.sponsor = sponsor;
    }

    public String getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        confirmEmail = confirmEmail.toLowerCase();
        this.confirmEmail = confirmEmail;
    }

    public Boolean isEmailConfirmed() {
        return confirmEmail.equals(email);
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public boolean getConfirmed() {
        if(confirmationCode != null)
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserAccount other = (UserAccount) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}