package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents the allocation of users in groups.
 * @author Hildeberto Mendonca
 */
@Entity
@Table(name = "user_group")
public class UserGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected UserGroupId id;

    private UserAccount userAccount;
    private AccessGroup accessGroup;

    private String username;
    private String groupName;

    public UserGroup() {
    }

    public UserGroup(AccessGroup accessGroup, UserAccount userAccount) {
        this.accessGroup = accessGroup;
        this.userAccount = userAccount;
        this.id = new UserGroupId(this.accessGroup.getId(), this.userAccount.getId());
        this.username = this.userAccount.getUsername();
        this.groupName = this.accessGroup.getName();
    }

    @EmbeddedId
    public UserGroupId getId() {
        return this.id;
    }

    public void setId(UserGroupId id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @ManyToOne
    @JoinColumn(name="user_id", insertable = false, updatable = false)
    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;

        if(this.id == null)
            this.id = new UserGroupId();
        this.id.setUserId(userAccount.getId());
        this.username = userAccount.getUsername();
    }

    @ManyToOne
    @JoinColumn(name="group_id", insertable = false, updatable = false)
    public AccessGroup getAccessGroup() {
        return accessGroup;
    }

    public void setAccessGroup(AccessGroup accessGroup) {
        this.accessGroup = accessGroup;

        if(this.id == null)
            this.id = new UserGroupId();
        this.id.setGroupId(accessGroup.getId());
        this.groupName = accessGroup.getName();
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
        if (!(object instanceof UserGroup)) {
            return false;
        }
        UserGroup other = (UserGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return username;
    }
}