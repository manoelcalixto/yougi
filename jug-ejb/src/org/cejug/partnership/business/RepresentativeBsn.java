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
package org.cejug.partnership.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.cejug.entity.UserAccount;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;
import org.cejug.util.EntitySupport;

/**
 * Manages partners of the user group.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class RepresentativeBsn {
	
    @PersistenceContext
    private EntityManager em;

    public Representative findRepresentative(String id) {
        if(id != null)
            return em.find(Representative.class, id);
        else
            return null;
    }
    
    public Representative findRepresentative(UserAccount person) {
    	try {
    		return (Representative) em.createQuery("select r from Representative r where r.person = :person")
    								  .setParameter("person", person)
                                      .getSingleResult();
    	}
    	catch(NoResultException nre) {
    		return null;
    	}
    }
    
    @SuppressWarnings("unchecked")
	public List<Representative> findRepresentatives(Partner partner) {
    	return em.createQuery("select r from Representative r where r.partner = :partner order by r.person.firstName asc")
    	         .setParameter("partner", partner)
    	         .getResultList();
    }
    
    public void save(Representative representative) {
    	if(representative.getId() == null || representative.getId().isEmpty()) {
    		representative.setId(EntitySupport.generateEntityId());
            em.persist(representative);
        }
        else {
            em.merge(representative);
        }
    }

    public void remove(String id) {
        Representative representative = em.find(Representative.class, id);
        if(representative != null)
            em.remove(representative);
    }
}