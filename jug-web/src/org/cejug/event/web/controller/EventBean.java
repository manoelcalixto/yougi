package org.cejug.event.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.cejug.event.business.EventBsn;
import org.cejug.event.entity.Event;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.entity.Partner;

@ManagedBean
@RequestScoped
public class EventBean {

    @EJB
    private EventBsn eventBsn;
    
    @EJB
    private PartnerBsn partnerBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private Event event;
    private List<Event> events;
    private List<Partner> venues;
    
    private String selectedVenue;

    public EventBean() {}

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

    public String getSelectedVenue() {
		return selectedVenue;
	}

	public void setSelectedVenue(String selectedVenue) {
		this.selectedVenue = selectedVenue;
	}

	public List<Event> getEvents() {
    	if(events == null)
    		events = eventBsn.findEvents();
        return events;
    }
    
    public List<Partner> getVenues() {
    	if(venues == null)
    		venues = partnerBsn.findPartners();
        return venues;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.event = eventBsn.findEvent(id);
            this.selectedVenue = this.event.getVenue().getId();
        }
        else {
            this.event = new Event();
        }
    }

    public String save() {
    	Partner venue = partnerBsn.findPartner(selectedVenue);
    	this.event.setVenue(venue);
        eventBsn.save(this.event);
        return "events_next?faces-redirect=true";
    }

    public String remove() {
        eventBsn.remove(this.event.getId());
        return "events_next?faces-redirect=true";
    }
}