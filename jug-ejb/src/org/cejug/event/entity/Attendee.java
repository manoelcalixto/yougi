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

import org.cejug.entity.UserAccount;

@Entity
@Table(name="attendee")
public class Attendee implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	
	@ManyToOne
	@JoinColumn(name="event")
    private Event event;
	
	@ManyToOne
	@JoinColumn(name="attendee")
    private UserAccount attendee;
	
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name="registration_date")
    private Date registrationDate;
	
	private Boolean attended;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public UserAccount getAttendee() {
		return attendee;
	}

	public void setAttendee(UserAccount attendee) {
		this.attendee = attendee;
	}

	public Date getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	public Boolean getAttended() {
		return attended;
	}

	public void setAttended(Boolean attended) {
		this.attended = attended;
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
		if (!(obj instanceof Attendee))
			return false;
		Attendee other = (Attendee) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}