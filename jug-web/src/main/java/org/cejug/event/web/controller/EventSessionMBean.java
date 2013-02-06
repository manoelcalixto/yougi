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
import org.cejug.event.business.EventBsn;
import org.cejug.event.business.EventSessionBsn;
import org.cejug.event.entity.Event;
import org.cejug.event.entity.EventSession;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class EventSessionMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private EventSessionBsn eventSessionBsn;

    @EJB
    private EventBsn eventBsn;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    @ManagedProperty(value = "#{param.eventId}")
    private String eventId;

    private Event event;

    private EventSession eventSession;

    private List<Event> events;
    
    private List<EventSession> eventSessions;

    private String selectedEvent;

    public EventSessionMBean() {
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

    public EventSession getEventSession() {
        return eventSession;
    }

    public void setEventSession(EventSession eventSession) {
        this.eventSession = eventSession;
    }

    public List<EventSession> getEventSessions() {
        if (this.eventSessions == null) {
            this.eventSessions = eventSessionBsn.findEventSessions(this.event);
        }
        return this.eventSessions;
    }

    public String getSelectedEvent() {
        return this.selectedEvent;
    }

    public void setSelectedEvent(String selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public List<Event> getEvents() {
        if (this.events == null) {
            this.events = eventBsn.findEvents();
        }
        return this.events;
    }

    @PostConstruct
    public void load() {
        if (this.eventId != null && !this.eventId.isEmpty()) {
            this.event = eventBsn.findEvent(eventId);
            this.selectedEvent = this.event.getId();
        }

        if (this.id != null && !this.id.isEmpty()) {
            this.eventSession = eventSessionBsn.findEventSession(id);
            this.selectedEvent = this.eventSession.getEvent().getId();
        } else {
            this.eventSession = new EventSession();
        }
    }

    public String save() {
        Event evt = eventBsn.findEvent(selectedEvent);
        this.eventSession.setEvent(evt);
        
        eventSessionBsn.save(this.eventSession);
        return "sessions?faces-redirect=true&eventId=" + evt.getId();
    }

    public String remove() {
        eventSessionBsn.remove(this.eventSession.getId());
        return "sessions?faces-redirect=true&eventId=" + this.event.getId();
    }
}