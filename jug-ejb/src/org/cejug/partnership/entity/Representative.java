package org.cejug.partnership.entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.cejug.entity.UserAccount;

public class Representative implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	
	@ManyToOne
	@JoinColumn(name="person")
	private UserAccount person;
	
	@ManyToOne
	@JoinColumn(name="partner")
	private Partner partner;
	
	private String phone;
	
	private String position;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public UserAccount getPerson() {
		return person;
	}

	public void setPerson(UserAccount person) {
		this.person = person;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}
