package org.cejug.event.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.cejug.event.business.EventBsn;
import org.cejug.event.entity.Event;

@ManagedBean
@RequestScoped
public class EventBean {

    @EJB
    private EventBsn eventBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private Event event;
    private List<Event> events;

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

    public List<Event> getEvents() {
    	if(events == null)
    		events = eventBsn.findEvents();
        return events;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.event = eventBsn.findEvent(id);
        }
        else {
            this.event = new Event();
        }
    }

    public String save() {
        eventBsn.save(this.event);
        return "events?faces-redirect=true";
    }

    public String remove() {
        eventBsn.remove(this.event.getId());
        return "events?faces-redirect=true";
    }
}