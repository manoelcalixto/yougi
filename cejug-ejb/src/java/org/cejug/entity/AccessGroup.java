package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Represents a group of users.
 * @author Hildeberto Mendonca (me@hildeberto.com)
 */
@Entity
@Table(name="access_group")
public class AccessGroup implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private Boolean userDefault = false;

    public AccessGroup() {}

    public AccessGroup(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Id
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="user_default")
    public Boolean getUserDefault() {
        return userDefault;
    }

    public void setUserDefault(Boolean userDefault) {
        this.userDefault = userDefault;
    }

    @Transient
    public boolean getDefault() {
        if(userDefault == null)
            return false;
        else
            return userDefault.booleanValue();
    }

    @Override
    public String toString() {
        return name;
    }
}