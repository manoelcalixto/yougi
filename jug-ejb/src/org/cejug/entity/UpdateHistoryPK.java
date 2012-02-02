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
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Hildeberto Mendonca
 */
@Embeddable
public class UpdateHistoryPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "db_version")
    private String dbVersion;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "app_version")
    private String appVersion;

    public UpdateHistoryPK() {
    }

    public UpdateHistoryPK(String dbVersion, String appVersion) {
        this.dbVersion = dbVersion;
        this.appVersion = appVersion;
    }

    public String getDbVersion() {
        return dbVersion;
    }

    public void setDbVersion(String dbVersion) {
        this.dbVersion = dbVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (dbVersion != null ? dbVersion.hashCode() : 0);
        hash += (appVersion != null ? appVersion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UpdateHistoryPK)) {
            return false;
        }
        UpdateHistoryPK other = (UpdateHistoryPK) object;
        if ((this.dbVersion == null && other.dbVersion != null) || (this.dbVersion != null && !this.dbVersion.equals(other.dbVersion))) {
            return false;
        }
        if ((this.appVersion == null && other.appVersion != null) || (this.appVersion != null && !this.appVersion.equals(other.appVersion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder version = new StringBuilder();
        return version.append(appVersion).append("-").append(dbVersion).toString();
    }
}