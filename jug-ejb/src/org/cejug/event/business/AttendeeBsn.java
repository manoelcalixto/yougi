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
package org.cejug.event.business;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.entity.UserAccount;
import org.cejug.event.entity.Attendee;
import org.cejug.event.entity.Certificate;
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
        if (id != null) {
            return em.find(Attendee.class, id);
        } else {
            return null;
        }
    }

    public Attendee findAttendee(Event event, UserAccount person) {
        try {
            return (Attendee) em.createQuery("select a from Attendee a where a.attendee = :person and a.event = :event").setParameter("person", person).setParameter("event", event).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Long findNumberPeopleAttending(Event event) {
        return (Long) em.createQuery("select count(a) from Attendee a where a.event = :event").setParameter("event", event).getSingleResult();
    }
    
    public Long findNumberPeopleAttended(Event event) {
        return (Long) em.createQuery("select count(a) from Attendee a where a.event = :event and a.attended = :attended").setParameter("event", event).setParameter("attended", true).getSingleResult();
    }
    
    public Boolean isAttending(Event event, UserAccount person) {
        try {
            Attendee attendee = (Attendee) em.createQuery("select a from Attendee a where a.attendee = :person and a.event = :event").setParameter("person", person).setParameter("event", event).getSingleResult();

            if (attendee != null) {
                return true;
            } else {
                return false;
            }
        } catch (NoResultException nre) {
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public List<Attendee> findAttendees(Event event) {
        return em.createQuery("select a from Attendee a where a.event = :event order by a.attendee.firstName asc").setParameter("event", event).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Attendee> findConfirmedAttendees(Event event) {
        return em.createQuery("select a from Attendee a where a.event = :event and a.attended = :attended order by a.attendee.firstName asc").setParameter("event", event).setParameter("attended", true).getResultList();
    }

    /**
     * Returns a list of events in which the presence of the user was confirmed.
     */
    @SuppressWarnings("unchecked")
    public List<Event> findAttendeedEvents(UserAccount userAccount) {
        return em.createQuery("select a.event from Attendee a where a.attendee = :attendee and a.attended = :attended order by a.event.startDate desc")
                 .setParameter("attendee", userAccount)
                 .setParameter("attended", true)
                 .getResultList();
    }

    public void save(Attendee attendee) {
        attendee.setId(EntitySupport.generateEntityId());
        em.persist(attendee);
    }

    public void remove(String id) {
        Attendee attendee = em.find(Attendee.class, id);
        if (attendee != null) {
            em.remove(attendee);
        }
    }

    /**
     * Confirm the attendance of a list of members in a event.
     */
    public void confirmMembersAttendance(Event event, Attendee[] confirmedAttendees) {
        // If the received list is empty then nobody attended the event.
        if (confirmedAttendees == null) {
            confirmedAttendees = new Attendee[0];
        }

        /* Compares the existing list of attendees with the list of confirmed 
         * attendees.*/
        List<Attendee> attendees = findAttendees(event);
        boolean confirmed;
        for (Attendee attendee : attendees) {
            // We initially assume that the member didn't attend.
            confirmed = false;
            
            /* Check whether the attendee is in the list of confirmed
             * attendees. If yes, then his(er) attendance is confirmed. */
            for (Attendee confirmedAttendee : confirmedAttendees) {
                if (attendee.equals(confirmedAttendee)) {
                    attendee.setAttended(true);
                    attendee.generateCertificateData();
                    em.merge(attendee);
                    confirmed = true;
                    break;
                }
            }

            /* If the attendee is not in the list of confirmed attendees then
             * (s)he is set as not attending. */
            if (!confirmed) {
                attendee.setAttended(false);
                attendee.resetCertificateCode();
                em.merge(attendee);
            }
        }
    }
    
    /**
     * @return true if the data of the certificate match exactly the record of 
     * the related attendee.
     */
    public Boolean verifyAuthenticityCertificate(Certificate certificate) {
        try {
            Attendee attendee = (Attendee) em.createQuery("select a from Attendee a where a.certificateCode = :certificateCode and a.certificateFullname = :certificateFullname and a.certificateEvent = :certificateEvent and a.certificateVenue = :certificateVenue")
                                            .setParameter("certificateCode", certificate.getCertificateCode())
                                            .setParameter("certificateFullname", certificate.getCertificateFullname())
                                            .setParameter("certificateEvent", certificate.getCertificateEvent())
                                            .setParameter("certificateVenue", certificate.getCertificateVenue())
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
}