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
package org.cejug.knowledge.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import org.cejug.entity.UserAccount;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Entity
@Table(name = "mailing_list_subscription")
public class MailingListSubscription implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name="mailing_list", nullable=false)
    private MailingList mailingList;

    @ManyToOne
    @JoinColumn(name="user_account")
    private UserAccount userAccount;

    @Column(name = "email_address", nullable=false)
    private String emailAddress;

    @Column(name = "subscription_date")
    @Temporal(TemporalType.DATE)
    private Date subscriptionDate;

    @Column(name = "unsubscription_date")
    @Temporal(TemporalType.DATE)
    private Date unsubscriptionDate;

    public MailingListSubscription() {
    }

    public MailingListSubscription(String id) {
        this.id = id;
    }

    public MailingListSubscription(String id, String emailAddress) {
        this.id = id;
        this.emailAddress = emailAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public Date getUnsubscriptionDate() {
        return unsubscriptionDate;
    }

    public void setUnsubscriptionDate(Date unsubscriptionDate) {
        this.unsubscriptionDate = unsubscriptionDate;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MailingListSubscription)) {
            return false;
        }
        MailingListSubscription other = (MailingListSubscription) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.emailAddress;
    }
}