package org.cejug.event.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.cejug.business.MessengerBsn;
import org.cejug.entity.UserAccount;
import org.cejug.event.entity.Attendee;
import org.cejug.event.entity.Event;
import org.cejug.util.EntitySupport;

/**
 * Manages attendees of events organized by the user group.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class AttendeeBsn {
	
    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private MessengerBsn messengerBsn;

    public Attendee findAttendee(String id) {
        if(id != null)
            return em.find(Attendee.class, id);
        else
            return null;
    }
    
	public Attendee findAttendee(Event event, UserAccount person) {
    	try {
	    	return (Attendee)em.createQuery("select a from Attendee a where a.attendee = :person and a.event = :event")
	    			 .setParameter("person", person)
	    			 .setParameter("event", event)
	                 .getSingleResult();
    	}
    	catch(NoResultException nre) {
    		return null;
    	}
    }
	
	public Integer findNumberPeopleAttending(Event event) {
		return (Integer)em.createQuery("select count(a) from Attendee a where a.event = :event")
       		 .setParameter("event", event)
             .getSingleResult();
	}
	    
    @SuppressWarnings("unchecked")
	public List<Attendee> findAttendees(Event event) {
    	return em.createQuery("select a from Attendee a where a.event = :event order by a.attendee.attendee.firstName asc")
        		 .setParameter("event", event)
                 .getResultList();
    }

    public void save(Attendee attendee) {
    	attendee.setId(EntitySupport.generateEntityId());
        em.persist(attendee);
        messengerBsn.sendConfirmationEventAttendance(attendee.getAttendee(), attendee.getEvent());
    }

    public void remove(String id) {
        Attendee attendee = em.find(Attendee.class, id);
        if(attendee != null)
            em.remove(attendee);
    }
}