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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import org.cejug.entity.UserAccount;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.knowledge.entity.MailingListSubscription;
import org.cejug.util.EntitySupport;

/**
 * Implements the business logic related to the management of mailing lists.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class SubscriptionBsn {

    @PersistenceContext
    private EntityManager em;

    static final Logger logger = Logger.getLogger("org.cejug.knowledge.business.SubscriptionBsn");

    public MailingListSubscription findMailingListSubscription(String id) {
        return em.find(MailingListSubscription.class, id);
    }
    
    /** Check if a subscription with the informed email is subscribed. */
    public boolean isSubscribed(String email) {
        try {
            em.createQuery("select mls from MailingListSubscription mls where mls.emailAddress = :email and mls.unsubscriptionDate is null")
                    .setParameter("email", email)
                    .getSingleResult();
            return true;
        }
        catch(NoResultException nre) {
            return false;
        }
        catch(NonUniqueResultException nure) {
            return true;
        }
    }
    
    public MailingListSubscription findMailingListSubscription(MailingList mailingList, UserAccount userAccount) {
        try {
            return (MailingListSubscription) em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList and mls.emailAddress = :email and mls.unsubscriptionDate is null")
                                   .setParameter("mailingList", mailingList)
                                   .setParameter("email", userAccount.getEmail())
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    /**
     * Returns a list of subscriptions in different mailing lists in which the
     * informed user is subscribed.
     */
    @SuppressWarnings("unchecked")
    public List<MailingListSubscription> findMailingListSubscriptions(UserAccount userAccount) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.emailAddress = :email and mls.unsubscriptionDate is null")
                 .setParameter("email", userAccount.getEmail())
                 .getResultList();
    }
    
    /** 
     * Returns a list of subscriptions associated with the informed mailing list.
     */
    @SuppressWarnings("unchecked")
    public List<MailingListSubscription> findMailingListSubscriptions(MailingList mailingList) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList order by mls.subscriptionDate desc")
                 .setParameter("mailingList", mailingList)
                 .getResultList();
    }
    
    /** 
     * Returns a subscription with the informed email and associated with the 
     * informed mailing list.
     */
    @SuppressWarnings("unchecked")
    public List<MailingListSubscription> findMailingListSubscriptions(MailingList mailingList, String email) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList and mls.emailAddress = :email order by mls.subscriptionDate desc")
                 .setParameter("mailingList", mailingList)
                 .setParameter("email", email)
                 .getResultList();
    }

    /** Subscribes the user in several mailing lists. */
    public void subscribe(List<MailingList> mailingLists, UserAccount userAccount) {
        // Nothing to do if the user is not informed.
        if(userAccount == null)
            return;
        
        // If the user account is informed and the mailing list is empty, this is
        // the case in which the user must not be associated with a mailing list,
        // thus unsubscribed from all existing lists.
        if(mailingLists.isEmpty()) {
            unsubscribeAll(userAccount);
            userAccount.setMailingList(Boolean.FALSE);
            return;
        }
        
        userAccount.setMailingList(Boolean.TRUE);

        // Check if the user is already registered in the informed mailing lists.
        List<MailingListSubscription> mailingListSubscriptions = findMailingListSubscriptions(userAccount);
        boolean found;
        for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
            found = false;
            for(MailingList mailingList: mailingLists) {
                // If true, the user is already registered. No action needed.
                if(mailingListSubscription.getMailingList().equals(mailingList)) {
                    mailingLists.remove(mailingList);
                    found = true;
                    break;
                }
            }
            // If one of the existing registrations was not found in the informed mailing lists,
            // then the user is unsubscribed.
            if(!found) {
                unsubscribe(mailingListSubscription.getMailingList(), userAccount);
            }
        }
        
        // If there is any remaining mailing lists in the list, the user is registered to them.
        Calendar today = Calendar.getInstance();
        for(MailingList mailingList: mailingLists) {
            subscribe(mailingList, userAccount, today.getTime());
        }
    }

    /** Subscribes the user in the informed mailing list. */
    public void subscribe(MailingList mailingList, UserAccount userAccount, Date when) {
        if(mailingList == null || userAccount == null)
            return;
        
        // Reactivates an ancient subscription or creates a new one.
        MailingListSubscription mailingListSubscription = new MailingListSubscription();
        mailingListSubscription.setId(EntitySupport.generateEntityId());
        mailingListSubscription.setMailingList(mailingList);
        mailingListSubscription.setUserAccount(userAccount);
        mailingListSubscription.setEmailAddress(userAccount.getEmail());
        mailingListSubscription.setSubscriptionDate(when);
        em.persist(mailingListSubscription);
    }

    /** Unsubscribes the user from all mailing lists. */
    public void unsubscribeAll(UserAccount userAccount) {
        List<MailingListSubscription> mailingListSubscriptions = findMailingListSubscriptions(userAccount);
        if(mailingListSubscriptions != null) {
            for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
                Calendar today = Calendar.getInstance();
                mailingListSubscription.setUnsubscriptionDate(today.getTime());
                em.merge(mailingListSubscription);
            }
        }
    }

    /** Unsubscribes the user from the informed mailing list. */
    public void unsubscribe(MailingList mailingList, UserAccount userAccount) {
        MailingListSubscription mailingListSubscription = findMailingListSubscription(mailingList, userAccount);
        if(mailingListSubscription != null) {
            Calendar today = Calendar.getInstance();
            mailingListSubscription.setUnsubscriptionDate(today.getTime());
        }
    }
    
    /** 
     * When the subscription is not associated to a user, but it must be 
     * unsubscribed from the mailing list.
     */
    public void unsubscribe(MailingListSubscription subscription) {
        MailingListSubscription mailingListSubscription = findMailingListSubscription(subscription.getId());
        if(mailingListSubscription == null)
            return;
        
        mailingListSubscription.setUnsubscriptionDate(subscription.getUnsubscriptionDate());
    }
    
    /**
     * Save the mailing list subscription in the database.
     */
    public void save(MailingListSubscription subscription) {
        if(subscription.getId() == null || subscription.getId().isEmpty()) {
            subscription.setId(EntitySupport.generateEntityId());
            em.persist(subscription);
        }
        else {
            em.merge(subscription);
        }
    }
}