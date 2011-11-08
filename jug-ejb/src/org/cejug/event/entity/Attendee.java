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
    private Date dateRegistration;
	
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

	public Date getDateRegistration() {
		return dateRegistration;
	}

	public void setDateRegistration(Date dateRegistration) {
		this.dateRegistration = dateRegistration;
	}

	public Boolean getAttended() {
		return attended;
	}

	public void setAttended(Boolean attended) {
		this.attended = attended;
	}
}