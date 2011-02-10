package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Hildeberto Mendonca
 */
@Embeddable
public class UserGroupId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String groupId;
    private String userId;

    public UserGroupId() {
    }

    public UserGroupId(String groupId, String userId) {
        this.groupId = groupId;
        this.userId = userId;
    }

    @Column(name = "group_id", nullable = false)
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Column(name = "user_id", nullable= false)
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupId != null ? groupId.hashCode() : 0);
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserGroupId)) {
            return false;
        }
        UserGroupId other = (UserGroupId) object;
        if ((this.groupId == null && other.groupId != null) || (this.groupId != null && !this.groupId.equals(other.groupId))) {
            return false;
        }
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "[groupId=" + groupId + ", userId=" + userId + "]";
    }
}