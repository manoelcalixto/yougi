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
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.event.entity.Event;
import org.cejug.event.entity.EventSession;
import org.cejug.knowledge.business.TopicBsn;
import org.cejug.util.EntitySupport;

/**
 *
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class EventSessionBsn {

    @PersistenceContext
    private EntityManager em;
    
    @EJB
    private TopicBsn topicBsn;
    
    public EventSession findEventSession(String id) {
        if (id != null)
            return em.find(EventSession.class, id);
        return null;
    }
    
    public List<EventSession> findEventSessions(Event event) {
        return em.createQuery("select es from EventSession es where es.event = :event order by es.sessionDate, es.startTime asc")
                 .setParameter("event", event)
                 .getResultList();
    }
    
    public void save(EventSession eventSession) {
        if (eventSession != null && eventSession.getId() == null || eventSession.getId().isEmpty()) {
            eventSession.setId(EntitySupport.generateEntityId());
            em.persist(eventSession);
        } else {
            em.merge(eventSession);
        }
        
        topicBsn.consolidateTopics(eventSession.getTopics());
    }

    public void remove(String id) {
        EventSession eventSession = em.find(EventSession.class, id);
        if (eventSession != null) {
            em.remove(eventSession);
        }
    }
}