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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.cejug.event.business.AttendeeBsn;
import org.cejug.event.business.EventBsn;
import org.cejug.event.entity.Attendee;
import org.cejug.event.entity.Event;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@SessionScoped
public class AttendeeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private EventBsn eventBsn;

    @EJB
    private AttendeeBsn attendeeBsn;

    private Event event;

    private List<Attendee> attendees;

    private Attendee[] selectedAttendees;

    private Long numberPeopleAttending;

    private Long numberPeopleAttended;

    public AttendeeBean() {
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public List<Attendee> getAttendees() {
        return attendees;
    }

    public Attendee[] getSelectedAttendees() {
        return selectedAttendees;
    }

    public void setSelectedAttendees(Attendee[] selectedAttendees) {
        this.selectedAttendees = selectedAttendees;
    }

    public Long getNumberPeopleAttending() {
        return numberPeopleAttending;
    }

    public void setNumberPeopleAttending(Long numberPeopleAttending) {
        this.numberPeopleAttending = numberPeopleAttending;
    }

    public void setNumberPeopleAttended(Long numberPeopleAttended) {
        this.numberPeopleAttended = numberPeopleAttended;
    }

    public Long getNumberPeopleAttended() {
        return numberPeopleAttended;
    }

    public String load(String eventId) {
        this.event = eventBsn.findEvent(eventId);

        this.attendees = attendeeBsn.findAttendees(this.event);
        List<Attendee> confirmedAttendees = attendeeBsn.findConfirmedAttendees(event);
        if (confirmedAttendees != null) {
            this.selectedAttendees = new Attendee[confirmedAttendees.size()];
            int i = 0;
            for (Attendee atd : confirmedAttendees) {
                this.selectedAttendees[i++] = atd;
            }
        }
        
        this.numberPeopleAttending = attendeeBsn.findNumberPeopleAttending(this.event);
        this.numberPeopleAttended = attendeeBsn.findNumberPeopleAttended(this.event);
        
        return "attendees?faces-redirect=true";
    }

    public String confirmMembersAttended() {
        attendeeBsn.confirmMembersAttendance(this.event, this.selectedAttendees);
        removeSessionScoped();
        return "events?faces-redirect=true";
    }

    private void removeSessionScoped() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("partnerBean");
    }
}