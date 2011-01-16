package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Message template with variables to be fulfilled with object attributes.
 * @author Hildeberto Mendonca (hildeberto@cejug.org)
 */
@Entity
@Table(name="message_template")
public class MessageTemplate implements Serializable {

    private String id;
    private String title;
    private String body;

    public MessageTemplate() {}

    public MessageTemplate(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(nullable=false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(nullable=false)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Transient
    public String getTruncatedBody() {
        if (this.body.length() < 200) {
            return this.body;
        } else {
            return this.body.substring(0, 200);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessageTemplate other = (MessageTemplate) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return this.title;
    }
}