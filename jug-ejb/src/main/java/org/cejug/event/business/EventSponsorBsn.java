
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
import javax.persistence.PersistenceContext;
import org.cejug.event.entity.Event;
import org.cejug.event.entity.EventSponsor;
import org.cejug.partnership.entity.Partner;
import org.cejug.util.EntitySupport;

/**
 * This class implements the business logic of events' sponsors.
 *
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class EventSponsorBsn {

    @PersistenceContext
    private EntityManager em;

    public EventSponsor findEventSponsor(String id) {
        if (id != null) {
            return em.find(EventSponsor.class, id);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<EventSponsor> findEventSponsors(Event event) {
        return em.createQuery("select es from EventSponsor es where es.event = :event order by es.partner.name asc").setParameter("event", event).getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<EventSponsor> findSponsorEvents(Partner sponsor) {
        return em.createQuery("select es from EventSponsor es where es.partner = :sponsor order by es.event.name asc").setParameter("sponsor", sponsor).getResultList();
    }

    public void save(EventSponsor eventSponsor) {
        if (eventSponsor.getId() == null || eventSponsor.getId().isEmpty()) {
            eventSponsor.setId(EntitySupport.generateEntityId());
            em.persist(eventSponsor);
        } else {
            em.merge(eventSponsor);
        }
    }

    public void remove(String id) {
        EventSponsor eventSponsor = em.find(EventSponsor.class, id);
        if (eventSponsor != null) {
            em.remove(eventSponsor);
        }
    }
}