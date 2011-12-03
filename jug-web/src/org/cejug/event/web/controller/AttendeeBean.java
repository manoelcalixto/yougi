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
        if (this.attendees == null) {
            this.attendees = attendeeBsn.findAttendees(this.event);
        }
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
        event = eventBsn.findEvent(eventId);

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