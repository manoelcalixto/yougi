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
package org.cejug.yougi.business;

import org.cejug.yougi.entity.MessageHistory;
import org.cejug.yougi.entity.UserAccount;
import java.util.*;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.yougi.util.EntitySupport;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class MessageHistoryBean {
    
    @PersistenceContext
    private EntityManager em;
    
    static final Logger logger = Logger.getLogger(MessageHistoryBean.class.getName());
    
    public MessageHistory findHistoricalMessage(String id) {
        if(id != null) {
            return em.find(MessageHistory.class, id);
        }
        else {
            return null;
        }
    }
    
    public List<MessageHistory> findHistoricalMessageByRecipient(UserAccount recipient) {
    	List<MessageHistory> messages = em.createQuery("select hm from MessageHistory hm where hm.recipient = :userAccount order by hm.dateSent desc")
                                          .setParameter("userAccount", recipient)
                                          .getResultList();
        return messages;
    }
    
    public MessageHistory saveHistoricalMessage(MessageHistory message) {
    	if(EntitySupport.INSTANCE.isIdNotValid(message)) {
            message.setId(EntitySupport.INSTANCE.generateEntityId());
            em.persist(message);
            return message;
        }
        else {
            return em.merge(message);
        }
    }

    public void removeHistoricalMessage(String id) {
        MessageHistory messageHistory = findHistoricalMessage(id);
        if(messageHistory != null) {
            em.remove(messageHistory);
        }
    }
}