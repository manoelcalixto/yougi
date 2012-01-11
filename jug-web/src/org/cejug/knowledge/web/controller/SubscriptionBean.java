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
package org.cejug.knowledge.web.controller;

import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.cejug.knowledge.business.MailingListBsn;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.knowledge.entity.MailingListSubscription;

/**
 *
 * @author Hildeberto Mendonca
 */
@ManagedBean
@SessionScoped
public class SubscriptionBean {

    static final Logger logger = Logger.getLogger("org.cejug.knowledge.web.controller.SubscriptionBean");
    
    @EJB
    private MailingListBsn mailingListBsn;
    
    private List<MailingListSubscription> subscriptions;
        
    public SubscriptionBean() {
        logger.info("A new SubscriptionBean was stored in the application session.");
    }
    
    public void load(MailingList mailingList) {
        if(mailingList != null) {
            this.subscriptions = mailingListBsn.findMailingListSubscriptions(mailingList);
        }
    }
    
    public void searchByEmail(MailingList mailingList, String email) {
        this.subscriptions = mailingListBsn.findMailingListSubscriptions(mailingList, email);
    }
    
    public List<MailingListSubscription> getSubscriptions() {
        return this.subscriptions;
    }
    
    public static void removeFromSession() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("subscriptionBean");
        logger.info("The existing SubscriptionBean was removed from the session.");
    }
}