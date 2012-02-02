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
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "update_history")
public class UpdateHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    protected UpdateHistoryPK updateHistoryPK;

    @Basic(optional = false)
    @NotNull
    @Column(name = "date_release")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRelease;

    @Lob
    @Size(max = 65535)
    @Column(name = "db_release_notes")
    private String dbReleaseNotes;

    @Lob
    @Size(max = 65535)
    @Column(name = "app_release_notes")
    private String appReleaseNotes;

    public UpdateHistory() {
    }

    public UpdateHistory(UpdateHistoryPK updateHistoryPK) {
        this.updateHistoryPK = updateHistoryPK;
    }

    public UpdateHistory(UpdateHistoryPK updateHistoryPK, Date dateRelease) {
        this.updateHistoryPK = updateHistoryPK;
        this.dateRelease = dateRelease;
    }

    public UpdateHistory(String dbVersion, String appVersion) {
        this.updateHistoryPK = new UpdateHistoryPK(dbVersion, appVersion);
    }

    public UpdateHistoryPK getUpdateHistoryPK() {
        return updateHistoryPK;
    }

    public void setUpdateHistoryPK(UpdateHistoryPK updateHistoryPK) {
        this.updateHistoryPK = updateHistoryPK;
    }

    public Date getDateRelease() {
        return dateRelease;
    }

    public void setDateRelease(Date dateRelease) {
        this.dateRelease = dateRelease;
    }

    public String getDbReleaseNotes() {
        return dbReleaseNotes;
    }

    public void setDbReleaseNotes(String dbReleaseNotes) {
        this.dbReleaseNotes = dbReleaseNotes;
    }

    public String getAppReleaseNotes() {
        return appReleaseNotes;
    }

    public void setAppReleaseNotes(String appReleaseNotes) {
        this.appReleaseNotes = appReleaseNotes;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (updateHistoryPK != null ? updateHistoryPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UpdateHistory)) {
            return false;
        }
        UpdateHistory other = (UpdateHistory) object;
        if ((this.updateHistoryPK == null && other.updateHistoryPK != null) || (this.updateHistoryPK != null && !this.updateHistoryPK.equals(other.updateHistoryPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.updateHistoryPK.toString();
    }
}