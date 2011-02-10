package org.cejug.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Represents a country covered by the Java User Group.
 * @author Hildeberto Mendonca (me@hildeberto.com)
 */
@Entity
@Table(name = "country")
public class Country implements Serializable {
    private static final long serialVersionUID = 1L;

    private String acronym;
    private String name;
    
    public Country() {
    }

    public Country(String acronym) {
        this.acronym = acronym;
    }

    public Country(String acronym, String name) {
        this.acronym = acronym;
        this.name = name;
    }

    @Id
    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (acronym != null ? acronym.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Country)) {
            return false;
        }
        Country other = (Country) object;
        if ((this.acronym == null && other.acronym != null) || (this.acronym != null && !this.acronym.equals(other.acronym))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name;
    }
}