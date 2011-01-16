package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.cejug.util.EntitySupport;

/**
 * Represents one or more contacts of members, which could be home, work,
 * school, etc.
 * @author Hildeberto Mendonca (me@hildeberto.com)
 */
@Entity
@Table(name = "contact")
public class Contact implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private UserAccount user;
    private ContactType location;
    private String website;
    private String postalCode;
    private String email;
    private Province province;
    private City city;
    private Country country;
    private String twitter;
    private Boolean main;

    public Contact() {
    }

    public Contact(String id) {
        this.id = id;
    }

    public Contact(UserAccount userAccount) {
        this.user = userAccount;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JoinColumn(name = "user")
    @ManyToOne
    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    @Enumerated(EnumType.STRING)
    public ContactType getLocation() {
        return location;
    }

    public void setLocation(ContactType location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Column(name = "postal_code")
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JoinColumn(name = "province")
    @ManyToOne(fetch = FetchType.LAZY)
    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    @JoinColumn(name = "city")
    @ManyToOne(fetch = FetchType.LAZY)
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        if(city != null && (city.getId() == null || city.getId().isEmpty()))
            city.setId(EntitySupport.generateEntityId());

        this.city = city;
    }

    @JoinColumn(name = "country")
    @ManyToOne(fetch = FetchType.LAZY)
    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
        setLocation(ContactType.MAIN);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Contact)) {
            return false;
        }
        Contact other = (Contact) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return location.toString();
    }
}