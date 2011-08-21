package org.cejug.knowledge.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "topic_mailinglist_message")
public class TopicMailinglistMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "mailinglist_message", nullable = false)
    private MailingListMessage mailinglistMessage;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "topic", nullable = false)
    private Topic topic;

    public TopicMailinglistMessage() {
    }

    public TopicMailinglistMessage(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MailingListMessage getMailinglistMessage() {
        return mailinglistMessage;
    }

    public void setMailinglistMessage(MailingListMessage mailinglistMessage) {
        this.mailinglistMessage = mailinglistMessage;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
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
        if (!(object instanceof TopicMailinglistMessage)) {
            return false;
        }
        TopicMailinglistMessage other = (TopicMailinglistMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.topic.getName() + " - " + this.mailinglistMessage.getSubject();
    }
}