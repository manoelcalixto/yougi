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
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "mailing_list_message")
public class MailingListMessage implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String subject;
    private String body;

    @Column(name="sender")
    private String sender;

    @Column(name = "when_received", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date whenReceived;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "answer_score")
    private Integer answerScore;

    private Boolean published;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mailinglistMessage")
    private List<TopicMailinglistMessage> topics;

    @OneToMany(mappedBy = "replyTo")
    private List<MailingListMessage> repliesFrom;

    @ManyToOne
    @JoinColumn(name = "reply_to")
    private MailingListMessage replyTo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mailing_list", nullable = false)
    private MailingList mailingList;

    public MailingListMessage() {
    }

    public MailingListMessage(String id) {
        this.id = id;
    }

    public MailingListMessage(String id, String subject, String body, Date whenReceived) {
        this.id = id;
        this.subject = subject;
        this.body = body;
        this.whenReceived = whenReceived;
    }

    public String getId() {
        return id;
    }

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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getWhenReceived() {
        return whenReceived;
    }

    public void setWhenReceived(Date whenReceived) {
        this.whenReceived = whenReceived;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Integer getAnswerScore() {
        return answerScore;
    }

    public void setAnswerScore(Integer answerScore) {
        this.answerScore = answerScore;
    }

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<TopicMailinglistMessage> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicMailinglistMessage> topics) {
        this.topics = topics;
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
        // TODO: Warning - this method won't work in the case the id fields are not set
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
    public Object clone() {
        MailingListMessage mailingListMessage = new MailingListMessage();
        mailingListMessage.setId(this.id);
        mailingListMessage.setSubject(this.subject);
        mailingListMessage.setBody(this.body);
        mailingListMessage.setWhenReceived(this.whenReceived);
        mailingListMessage.setAnswerScore(this.answerScore);
        mailingListMessage.setMailingList(this.mailingList);
        mailingListMessage.setMessageType(this.messageType);
        mailingListMessage.setPublished(this.published);
        mailingListMessage.setRepliesFrom(this.repliesFrom);
        mailingListMessage.setReplyTo(this.replyTo);
        mailingListMessage.setSender(this.sender);
        mailingListMessage.setTopics(this.topics);
        return mailingListMessage;
    }

    @Override
    public String toString() {
        return this.subject;
    }
}