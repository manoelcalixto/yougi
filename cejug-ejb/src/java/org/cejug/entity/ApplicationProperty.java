package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="application_property")
public class ApplicationProperty implements Serializable {
    private static final long serialVersionUID = 1L;

    private String propertyKey;
    private String propertyValue;

    public ApplicationProperty() {}

    public ApplicationProperty(String propertyKey, String propertyValue) {
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
    }

    @Id
    @Column(name="property_key", nullable=false)
    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    @Column(name="property_value")
    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ApplicationProperty other = (ApplicationProperty) obj;
        if ((this.propertyKey == null) ? (other.propertyKey != null) : !this.propertyKey.equals(other.propertyKey)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.propertyKey != null ? this.propertyKey.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return propertyKey +" = "+ propertyValue;
    }
}