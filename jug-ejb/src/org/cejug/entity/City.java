package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * City is the smallest geographic region where a JUG can operate. A JUG can
 * cover one or more cities. During the registration, a new member can add
 * his/her own city if it is not listed in the select field. However, cities
 * added this way should pass through a validation process before being
 * considered as a city covered by the JUG.
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "city")
public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;
    private String name;
    private Boolean valid;
    
    @JoinColumn(name = "country")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Country country;
    
    @JoinColumn(name = "province")
    @ManyToOne(fetch = FetchType.LAZY)
    private Province province;
    
    private String latitude;
    private String longitude;

    public City() {
    }

    public City(String id) {
        this.id = id;
    }

    public City(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
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

    public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof City)) {
            return false;
        }
        City other = (City) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}