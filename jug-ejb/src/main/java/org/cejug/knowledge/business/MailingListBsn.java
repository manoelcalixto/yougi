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
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.util.EntitySupport;

/**
 * Implements the business logic related to the management of mailing lists.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class MailingListBsn {

    @PersistenceContext
    private EntityManager em;

    static final Logger logger = Logger.getLogger("org.cejug.knowledge.business.MailingListBsn");

    public MailingList findMailingList(String id) {
        return em.find(MailingList.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<MailingList> findMailingLists() {
        return em.createQuery("select ml from MailingList ml order by ml.name asc").getResultList();
    }

    public MailingList findMailingListByEmail(String email) {
        try {
            return (MailingList) em.createQuery("select ml from MailingList ml where ml.email = :email")
                                   .setParameter("email", email)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    /** Save the mailing list in the database. */
    public void save(MailingList mailingList) {
        if(mailingList.getId() == null || mailingList.getId().isEmpty()) {
            mailingList.setId(EntitySupport.generateEntityId());
            em.persist(mailingList);
        }
        else {
            em.merge(mailingList);
        }
    }

    public void remove(String id) {
        MailingList mailingList = em.find(MailingList.class, id);
        if(mailingList != null)
            em.remove(mailingList);
    }
}