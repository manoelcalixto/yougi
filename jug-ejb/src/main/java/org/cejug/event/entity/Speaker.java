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
package org.cejug.event.entity;

import java.io.Serializable;
import javax.persistence.*;
import org.cejug.entity.UserAccount;

/**
 * Person with knowledge and experience to give a speech in an event, respecting
 * the scope of subjects in the domain explored by the user group.
 * 
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Entity
@Table(name = "speaker")
public class Speaker implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "event", nullable=false)
    private Event event;
    
    @ManyToOne
    @JoinColumn(name = "session", nullable=false)
    private EventSession session;
    
    @ManyToOne
    @JoinColumn(name = "user_account", nullable=false)
    private UserAccount userAccount;

    @Column(name = "short_cv")
    private String shortCv;   

    public Speaker() {
    }

    public Speaker(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getShortCv() {
        return shortCv;
    }

    public void setShortCv(String shortCv) {
        this.shortCv = shortCv;
    }

    /**
     * @return The event that the speaker is giving a speech.
     */
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * @return The session in which the speaker is scheduled to speak.
     */
    public EventSession getSession() {
        return session;
    }

    public void setSession(EventSession session) {
        this.session = session;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Speaker)) {
            return false;
        }
        Speaker other = (Speaker) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.userAccount.getFullName();
    }
}