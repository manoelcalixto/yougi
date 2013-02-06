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
package org.cejug.event.web.controller;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.event.business.EventBsn;
import org.cejug.event.business.EventSessionBsn;
import org.cejug.event.business.SpeakerBsn;
import org.cejug.event.entity.Event;
import org.cejug.event.entity.EventSession;
import org.cejug.event.entity.Speaker;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class SpeakerMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private SpeakerBsn speakerBsn;

    @EJB
    private EventBsn eventBsn;
    
    @EJB
    private EventSessionBsn eventSessionBsn;
    
    @EJB
    private UserAccountBsn userAccountBsn;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    private Event event;
    
    private Speaker speaker;

    private List<Event> events;
    
    private List<EventSession> eventSessions;
    
    private List<UserAccount> userAccounts;
    
    private List<Speaker> speakers;

    private String selectedEvent;
    
    private String selectedEventSession;
    
    private String selectedUserAccount;

    public SpeakerMBean() {
        this.speaker = new Speaker();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    public List<Speaker> getSpeakers() {
        if (this.speakers == null) {
            this.speakers = speakerBsn.findSpeakers(this.event);
        }
        return this.speakers;
    }

    public String getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    /**
     * @return the selectedEventSession
     */
    public String getSelectedEventSession() {
        return selectedEventSession;
    }

    /**
     * @param selectedEventSession the selectedEventSession to set
     */
    public void setSelectedEventSession(String selectedEventSession) {
        this.selectedEventSession = selectedEventSession;
    }

    /**
     * @return the selectedUserAccount
     */
    public String getSelectedUserAccount() {
        return selectedUserAccount;
    }

    /**
     * @param selectedUserAccount the selectedUserAccount to set
     */
    public void setSelectedUserAccount(String selectedUserAccount) {
        this.selectedUserAccount = selectedUserAccount;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = eventBsn.findEvents();
        }
        return this.events;
    }
    
    /**
     * @return the eventSessions
     */
    public List<EventSession> getEventSessions() {
        return eventSessions;
    }

    /**
     * @return the userAccounts
     */
    public List<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    /**
     * @param userAccounts the userAccounts to set
     */
    public void setUserAccounts(List<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    /**
     * @param eventSessions the eventSessions to set
     */
    public void setEventSessions(List<EventSession> eventSessions) {
        this.eventSessions = eventSessions;
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.event = eventBsn.findEvent(eventId);
            this.speaker.setEvent(this.event);
            this.selectedEvent = this.event.getId();
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.speaker = speakerBsn.findSpeaker(id);
            this.selectedEvent = this.speaker.getEvent().getId();
            this.selectedEventSession = this.speaker.getSession().getId();
            this.selectedUserAccount = this.speaker.getUserAccount().getId();
        }
        
        this.events = eventBsn.findEvents();
        this.eventSessions = eventSessionBsn.findEventSessions(this.event);
        this.userAccounts = userAccountBsn.findUserAccounts();
    }

    public String save() {
        Event evt = eventBsn.findEvent(selectedEvent);
        this.speaker.setEvent(evt);
        
        EventSession evtSes = eventSessionBsn.findEventSession(selectedEventSession);
        this.speaker.setSession(evtSes);
        
        UserAccount usrAcc = userAccountBsn.findUserAccount(selectedUserAccount);
        this.speaker.setUserAccount(usrAcc);
        
        speakerBsn.save(this.speaker);
        return "speakers?faces-redirect=true&eventId=" + evt.getId();
    }

    public String remove() {
        speakerBsn.remove(this.speaker.getId());
        return "speakers?faces-redirect=true&eventId=" + this.event.getId();
    }
}