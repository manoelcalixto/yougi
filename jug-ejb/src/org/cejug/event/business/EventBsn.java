package org.cejug.event.business;

import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cejug.event.entity.Event;

/**
 * Manages partners of the user group.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class EventBsn {
	
    @PersistenceContext
    private EntityManager em;

    public Event findEvent(String id) {
        if(id != null)
            return em.find(Event.class, id);
        else
            return null;
    }
    
    public List<Event> findEvents() {
    	Calendar today = Calendar.getInstance();
        return em.createQuery("select e from Event e where e.endDate >= :today order by e.endDate desc")
        		 .setParameter("today", today.getTime())
                 .getResultList();
    }

    public void save(Event event) {
        Event existing = em.find(Event.class, event.getId());
        if(existing == null)
            em.persist(event);
        else
            em.merge(event);
    }

    public void remove(String id) {
        Event event = em.find(Event.class, id);
        if(event != null)
            em.remove(event);
    }
}