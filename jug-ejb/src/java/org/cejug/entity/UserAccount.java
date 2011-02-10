package org.cejug.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
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
@SecondaryTable(name="communication_privacy", pkJoinColumns=@PrimaryKeyJoinColumn(name="user"))
public class UserAccount implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String username;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private Integer gender;
    private Date birthDate;
    private String photo;
    private String email;
    private String confirmEmail;
    private String confirmationCode;
    private List<Contact> contacts;
    private Date registrationDate;
    private Date lastUpdate;
    private Boolean deactivated = false;
    private Date deactivationDate;
    private String deactivationReason;
    private DeactivationType deactivationType;

    // Privacy properties
    private Boolean publicProfile;
    private Boolean mailingList;
    private Boolean news;
    private Boolean generalOffer;
    private Boolean jobOffer;
    private Boolean event;

    public UserAccount() {}

    public UserAccount(String id) {
        this.id = id;
    }

    public UserAccount(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Id
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(nullable=false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Transient
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    @Transient
    public Boolean isPasswordConfirmed() {
        return confirmPassword.equals(password);
    }

    @Column(name="first_name", nullable=false)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        firstName = TextUtils.capitalizeFirstCharWords(firstName);
        this.firstName = firstName;
    }

    @Column(name="last_name", nullable=false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        lastName = TextUtils.capitalizeFirstCharWords(lastName);
        this.lastName = lastName;
    }

    @Transient
    public String getFullName() {
        StringBuilder str = new StringBuilder();
        str.append(firstName);
        str.append(" ");
        str.append(lastName);
        return str.toString();
    }

    @Column(nullable=false)
    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    @Temporal(javax.persistence.TemporalType.DATE)
    @Column(name="birth_date",nullable=false)
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    @Transient
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = email.toLowerCase();
        this.email = email;
        this.setUsername(email);
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="registration_date")
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="last_update")
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

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name="deactivation_date")
    public Date getDeactivationDate() {
        return deactivationDate;
    }

    public void setDeactivationDate(Date deactivationDate) {
        this.deactivationDate = deactivationDate;
    }

    @Column(name="deactivation_reason")
    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name="deactivation_type")
    public DeactivationType getDeactivationType() {
        return deactivationType;
    }

    public void setDeactivationType(DeactivationType deactivationType) {
        this.deactivationType = deactivationType;
    }

    @Column(table="communication_privacy", name = "public_profile")
    public Boolean getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(Boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    @Column(table="communication_privacy", name = "mailing_list")
    public Boolean getMailingList() {
        return mailingList;
    }

    public void setMailingList(Boolean mailingList) {
        this.mailingList = mailingList;
    }

    @Column(table="communication_privacy")
    public Boolean getNews() {
        return news;
    }

    public void setNews(Boolean news) {
        this.news = news;
    }

    @Column(table="communication_privacy", name="general_offer")
    public Boolean getGeneralOffer() {
        return generalOffer;
    }

    public void setGeneralOffer(Boolean generalOffer) {
        this.generalOffer = generalOffer;
    }

    @Column(table="communication_privacy", name = "job_offer")
    public Boolean getJobOffer() {
        return jobOffer;
    }

    public void setJobOffer(Boolean jobOffer) {
        this.jobOffer = jobOffer;
    }

    @Column(table="communication_privacy")
    public Boolean getEvent() {
        return event;
    }

    public void setEvent(Boolean event) {
        this.event = event;
    }

    @OneToMany(mappedBy="user", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact contact) {
        if(this.contacts == null)
            this.contacts = new ArrayList<Contact>(1);
        contact.setUser(this);
        contacts.add(contact);
    }

    @Transient
    public String getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        confirmEmail = confirmEmail.toLowerCase();
        this.confirmEmail = confirmEmail;
    }

    @Transient
    public Boolean isEmailConfirmed() {
        return confirmEmail.equals(email);
    }

    @Column(name="confirmation_code")
    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    @Transient
    public boolean getConfirmed() {
        if(confirmationCode != null)
            return false;
        else
            return true;
    }

    @Transient
    public Contact getMainContact() {
        for(Contact contact: contacts) {
            if(contact.getMain())
                return contact;
        }
        return null;
    }

    public void setMainContact(Contact mainContact) {
        mainContact.setMain(Boolean.TRUE);
        mainContact.setUser(this);

        if(contacts == null) {
            contacts = new ArrayList<Contact>(1);
        }

        if(contacts.isEmpty()) {
            contacts.add(mainContact);
            return;
        }

        for(Contact contact: contacts) {
            if(contact.getMain()) {
                contact.setMain(Boolean.FALSE);
                break;
            }
        }

        Boolean found = false;
        Contact contact;
        for(int i = 0;i < contacts.size();i++) {
            contact = contacts.get(i);
            if(contact.equals(mainContact)) {
                contacts.set(i, mainContact);
                found = Boolean.TRUE;
                break;
            }
        }

        if(!found) {
            contacts.add(mainContact);
        }
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