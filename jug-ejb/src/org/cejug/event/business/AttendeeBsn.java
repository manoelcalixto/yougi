package org.cejug.event.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

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
	
	public Long findNumberPeopleAttending(Event event) {
		return (Long)em.createQuery("select count(a) from Attendee a where a.event = :event")
       		 .setParameter("event", event)
             .getSingleResult();
	}
	
	public Long findNumberPeopleAttended(Event event) {
		return (Long)em.createQuery("select count(a) from Attendee a where a.event = :event and a.attended = :attended")
       		 .setParameter("event", event)
       		 .setParameter("attended", true)
             .getSingleResult();
	}

	public Boolean isAttending(Event event, UserAccount person) {
    	try {
	    	Attendee attendee = (Attendee)em.createQuery("select a from Attendee a where a.attendee = :person and a.event = :event")
	    			 .setParameter("person", person)
	    			 .setParameter("event", event)
	                 .getSingleResult();
	    	
	    	if(attendee != null)
	    		return true;
	    	else
	    		return false;
    	}
    	catch(NoResultException nre) {
    		return false;
    	}
    }
	    
    @SuppressWarnings("unchecked")
	public List<Attendee> findAttendees(Event event) {
    	return em.createQuery("select a from Attendee a where a.event = :event order by a.attendee.firstName asc")
        		 .setParameter("event", event)
                 .getResultList();
    }
    
    @SuppressWarnings("unchecked")
	public List<Attendee> findConfirmedAttendees(Event event) {
    	return em.createQuery("select a from Attendee a where a.event = :event and a.attended = :attended order by a.attendee.firstName asc")
        		 .setParameter("event", event)
        		 .setParameter("attended", true)
                 .getResultList();
    }

    public void save(Attendee attendee) {
    	attendee.setId(EntitySupport.generateEntityId());
        em.persist(attendee);
    }

    public void remove(String id) {
        Attendee attendee = em.find(Attendee.class, id);
        if(attendee != null)
            em.remove(attendee);
    }
    
    public void confirmMembersAttendance(Event event, Attendee[] confirmedAttendees) {
        if(confirmedAttendees == null)
            confirmedAttendees = new Attendee[0];
        
        List<Attendee> attendees = findAttendees(event);
        boolean confirmed;
        for(Attendee attendee: attendees) {
            confirmed = false;
            for(Attendee confirmedAttendee: confirmedAttendees) {
                if(attendee.equals(confirmedAttendee)) {
                    attendee.setAttended(true);
                    em.merge(attendee);
                    confirmed = true;
                    break;
                }
            }

            if(!confirmed) {
                attendee.setAttended(false);
                em.merge(attendee);
            }
        }
    }
}