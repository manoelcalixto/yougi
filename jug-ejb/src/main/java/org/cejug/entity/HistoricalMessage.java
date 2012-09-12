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
package org.cejug.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.*;

/**
 *
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "historical_message")
public class HistoricalMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    private String id;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false)
    private String body;
    
    @ManyToOne
    @JoinColumn(name="recipient", nullable=false)
    private UserAccount recipient;

    @Column(name = "date_sent", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSent;
    
    @Column(name="message_sent")
    private Boolean messageSent;

    public HistoricalMessage () {}
    
    public HistoricalMessage(EmailMessage emailMessage) {
        this.setSubject(emailMessage.getSubject());
        this.setBody(emailMessage.getBody());
        this.setRecipient(emailMessage.getRecipient());
    }
        
    /** 
     * @return A list of historical messages, one for each recipient of the message.
     */
    public static List<HistoricalMessage> createHistoricMessages(EmailMessage emailMessage) {
        List<HistoricalMessage> historicMessages = new ArrayList<HistoricalMessage>();
        HistoricalMessage historicMessage;
        
        for(UserAccount userAccount: emailMessage.getRecipients()) {
            historicMessage = new HistoricalMessage();
            historicMessage.setRecipient(userAccount);
            historicMessage.setSubject(emailMessage.getSubject());
            historicMessage.setBody(emailMessage.getBody());
            historicMessages.add(historicMessage);
        }
        
        return historicMessages;
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

    /**
     * @return the recipient
     */
    public UserAccount getRecipient() {
        return recipient;
    }

    /**
     * @param recipient the user account for whom the message was sent.
     */
    public void setRecipient(UserAccount recipient) {
        this.recipient = recipient;
    }
    
    public Date getDateSent() {
        return dateSent;
    }

    public void setDateSent(Date dateSent) {
        this.dateSent = dateSent;
    }

    /**
     * @return true if the message was sent to the user by email. False if it was
     * not possible to send the message.
     */
    public Boolean getMessageSent() {
        return messageSent;
    }

    /**
     * @param messageSent set true if it was possible to send the message, or
     * false if some exception ocurred.
     */
    public void setMessageSent(Boolean messageSent) {
        this.messageSent = messageSent;
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
        if (!(object instanceof HistoricalMessage)) {
            return false;
        }
        HistoricalMessage other = (HistoricalMessage) object;
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