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
import java.util.List;
import javax.persistence.*;
import org.cejug.entity.Identified;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Entity
@Table(name = "mailing_list_message")
public class MailingListMessage implements Serializable, Cloneable, Identified {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "mailing_list", nullable = false)
    private MailingList mailingList;
    
    private String subject;
    
    private String body;

    @ManyToOne
    @JoinColumn(name="sender")
    private MailingListSubscription sender;

    @Column(name = "date_received", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateReceived;
    
    @ManyToOne
    @JoinColumn(name = "reply_to")
    private MailingListMessage replyTo;
    
    @OneToMany(mappedBy = "replyTo")
    private List<MailingListMessage> repliesFrom;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "message_type")
    private MessageType messageType;
    
    private String topics;

    private Boolean published;

    public MailingListMessage() {
    }

    public MailingListMessage(String id) {
        this.id = id;
    }

    public MailingListMessage(String id, String subject, String body, Date dateReceived) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.dateReceived = dateReceived;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public MailingListSubscription getSender() {
        return sender;
    }

    public void setSender(MailingListSubscription sender) {
        this.sender = sender;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<MailingListMessage> getRepliesFrom() {
        return repliesFrom;
    }

    public void setRepliesFrom(List<MailingListMessage> repliesFrom) {
        this.repliesFrom = repliesFrom;
    }

    public MailingListMessage getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(MailingListMessage replyTo) {
        this.replyTo = replyTo;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof MailingListMessage)) {
            return false;
        }
        MailingListMessage other = (MailingListMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.subject;
    }
}