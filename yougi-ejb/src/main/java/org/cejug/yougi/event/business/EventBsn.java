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
package org.cejug.yougi.event.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.cejug.yougi.event.entity.Event;
import org.cejug.yougi.util.EntitySupport;

/**
 * Manages events organized by the user group.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class EventBsn {
	
    @PersistenceContext
    private EntityManager em;

    public Event findEvent(String id) {
        if(id != null) {
            return em.find(Event.class, id);
        }
        else {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Event> findEvents() {
    	List<Event> events = em.createQuery("select e from Event e order by e.endDate desc")
        		       .getResultList();
        return events;
    }
    
    @SuppressWarnings("unchecked")
    public List<Event> findCommingEvents() {
    	Calendar today = Calendar.getInstance();
        List<Event> events = em.createQuery("select e from Event e where e.endDate >= :today order by e.endDate desc")
        		       .setParameter("today", today.getTime())
                               .getResultList();
        return events;
    }
    
    public void consolidateEventPeriod(Event event, Date date, Date startDate, Date endDate) {
        if(event == null) {
            return;
        }
        
        Query query = em.createQuery("");
    }

    public void save(Event event) {
    	if(EntitySupport.INSTANCE.isIdNotValid(event)) {
            event.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(event);
        }
        else {
            em.merge(event);
        }
    }

    public void remove(String id) {
        Event event = findEvent(id);
        if(event != null) {
            em.remove(event);
        }
    }
}