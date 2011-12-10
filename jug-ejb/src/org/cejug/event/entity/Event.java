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
package org.cejug.event.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;

import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Province;
import org.cejug.partnership.entity.Partner;

/**
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name="event")
public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	private String name;
	
	@ManyToOne
	@JoinColumn(name="venue")
	private Partner venue;
	
	@Column(name="start_date")
	@Temporal(javax.persistence.TemporalType.DATE)
    private Date startDate;
	
	@Column(name="start_time")
	@Temporal(javax.persistence.TemporalType.TIME)
	private Date startTime;
	
	@Column(name="end_date")
	@Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;
	
	@Column(name="end_time")
	@Temporal(javax.persistence.TemporalType.TIME)
	private Date endTime;
	
    private String description;
    
    @Column(name="short_description")
    private String shortDescription;
    
    private String address;

    @ManyToOne
    @JoinColumn(name="country")
    private Country country;
    
    @ManyToOne
    @JoinColumn(name="province")
    private Province province;
    
    @ManyToOne
    @JoinColumn(name="city")
    private City city;
    
    private String latitude;
    
    private String longitude;  
    
    private Boolean external;
    
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
	
	public Partner getVenue() {
		return venue;
	}
	
	public void setVenue(Partner venue) {
		this.venue = venue;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Country getCountry() {
		return country;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public Province getProvince() {
		return province;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public City getCity() {
		return city;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setExternal(Boolean external) {
		this.external = external;
	}

	public Boolean getExternal() {
		return external;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}