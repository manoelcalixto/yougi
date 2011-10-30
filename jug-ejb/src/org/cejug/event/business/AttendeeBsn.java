package org.cejug.event.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cejug.event.entity.Attendee;
import org.cejug.event.entity.Event;

/**
 * Manages partners of the user group.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class AttendeeBsn {
	
    @PersistenceContext
    private EntityManager em;

    public Attendee findAttendee(String id) {
        if(id != null)
            return em.find(Attendee.class, id);
        else
            return null;
    }
    
    @SuppressWarnings("unchecked")
	public List<Attendee> findAttendees(Event event) {
    	return em.createQuery("select a from Attendee a where a.event = :event order by a.attendee.attendee.firstName asc")
        		 .setParameter("event", event)
                 .getResultList();
    }

    public void save(Attendee attendee) {
        Attendee existing = em.find(Attendee.class, attendee.getId());
        if(existing == null)
            em.persist(attendee);
        else
            em.merge(attendee);
    }

    public void remove(String id) {
        Attendee attendee = em.find(Attendee.class, id);
        if(attendee != null)
            em.remove(attendee);
    }
}