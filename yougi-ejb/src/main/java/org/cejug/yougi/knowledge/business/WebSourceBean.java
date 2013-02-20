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
package org.cejug.yougi.knowledge.business;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.entity.UserAccount;
import org.cejug.yougi.knowledge.entity.WebSource;
import org.cejug.yougi.util.EntitySupport;

/**
 * Business logic dealing with web sources entities.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class WebSourceBean {

    @PersistenceContext
    private EntityManager em;

    public WebSource findWebSource(String id) {
        return em.find(WebSource.class, id);
    }

    public WebSource findWebSourceByProvider(UserAccount provider) {
        try {
            return (WebSource) em.createQuery("select ws from WebSource ws where ws.provider = :provider order by ws.title asc")
                                 .setParameter("provider", provider)
                                 .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public void save(WebSource webSource) {
        if(EntitySupport.INSTANCE.isIdNotValid(webSource)) {
            webSource.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(webSource);
        }
        else {
            em.merge(webSource);
        }
    }

    public void remove(String id) {
        WebSource webSource = em.find(WebSource.class, id);
        if(webSource != null) {
            em.remove(webSource);
        }
    }
}