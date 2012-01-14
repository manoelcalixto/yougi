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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.knowledge.business.MailingListBsn;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.knowledge.entity.MailingListSubscription;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class MailingListBean {

    @EJB
    private MailingListBsn mailingListBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;
    
    private MailingList mailingList;
    
    private List<MailingList> mailingLists;
    private List<MailingListSubscription> subscriptions;
    
    private String emailCriteria;
    
    public MailingListBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmailCriteria() {
        return emailCriteria;
    }

    public void setEmailCriteria(String emailCriteria) {
        this.emailCriteria = emailCriteria;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public List<MailingList> getMailingLists() {
        if(this.mailingLists == null) {
            this.mailingLists = mailingListBsn.findMailingLists();
        }
        return this.mailingLists;
    }
    
    public List<MailingListSubscription> getSubscriptions() {
        return this.subscriptions;
    }
    
    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.mailingList = mailingListBsn.findMailingList(id);
        }
        else {
            this.mailingList = new MailingList();
        }
    }
    
    public String searchByEmail() {
        if(this.emailCriteria != null) {
            this.subscriptions = mailingListBsn.findMailingListSubscriptions(this.mailingList, this.emailCriteria);
        }
        return "subscriptions";
    }

    public String save() {
        mailingListBsn.save(this.mailingList);
        return "mailing_lists?faces-redirect=true";
    }

    public String remove() {
        mailingListBsn.remove(this.mailingList.getId());
        return "mailing_lists?faces-redirect=true";
    }
}