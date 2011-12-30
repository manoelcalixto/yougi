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
package org.cejug.knowledge.business;

import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cejug.knowledge.entity.Topic;

/**
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class TopicBsn {

    @PersistenceContext
    private EntityManager em;

    public Topic findTopic(String name) {
        return em.find(Topic.class, name);
    }
    
    @SuppressWarnings("unchecked")
    public List<Topic> findTopics() {
        return em.createQuery("select t from Topic t order by t.name asc").getResultList();
    }

    public void save(Topic topic) {
        Topic existing = em.find(Topic.class, topic.getName());
        if(existing == null)
            em.persist(topic);
        else
            em.merge(topic);
    }

    public void remove(String name) {
        Topic topic = em.find(Topic.class, name);
        if(topic != null)
            em.remove(topic);
    }
}