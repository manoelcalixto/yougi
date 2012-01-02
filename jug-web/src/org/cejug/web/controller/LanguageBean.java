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
package org.cejug.web.controller;

import org.cejug.business.LanguageBsn;
import org.cejug.entity.Language;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class LanguageBean {

    @EJB
    private LanguageBsn languageBsn;

    private Language language;

    private List<Language> languages;
    
    @ManagedProperty(value="#{param.acronym}")
    private String acronym;

    public LanguageBean() {
        this.language = new Language();
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Language> getLanguages() {
        if(this.languages == null)
            this.languages = languageBsn.findLanguages();
        return this.languages;
    }
    
    @PostConstruct
    public void load() {
        if(this.acronym != null && !this.acronym.isEmpty()) {
            this.language = languageBsn.findLanguage(this.acronym);
        }
    }

    public String save() {
        languageBsn.save(this.language);
        return "languages?faces-redirect=true";
    }
}