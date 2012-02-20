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

import java.util.Locale;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.cejug.business.LanguageBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.Language;
import org.cejug.entity.UserAccount;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@SessionScoped
public class UserProfileBean {

    @EJB
    private LanguageBsn languageBsn;
    
    @EJB
    private UserAccountBsn userAccountBsn;

    private Language language;
    private UserAccount userAccount;

    static final Logger logger = Logger.getLogger("org.cejug.web.controller.UserProfileBean");

    public UserProfileBean() {
        this.language = new Language();
    }

    public String getLanguage() {
        if (this.language != null) {
            return this.language.getAcronym();
        } else {
            this.language = new Language();
            return this.language.getAcronym();
        }
    }

    public String changeLanguage(String acronym) {
        this.language = languageBsn.findLanguage(acronym);
        FacesContext fc = FacesContext.getCurrentInstance();
        Locale locale = new Locale(language.getAcronym());
        fc.getViewRoot().setLocale(locale);
        return "index?faces-redirect=true";
    }
    
    /** 
     * In the first invocation, it loads the user account in the session, using
     * the authenticated user to search for the corresponding user in the
     * database. Subsequent invocations return the user account in the session.
     */
    public UserAccount getUserAccount() {
    	if(userAccount == null) {
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletRequest request = (HttpServletRequest)fc.getExternalContext().getRequest();
            String username = request.getRemoteUser();
            this.userAccount = userAccountBsn.findUserAccountByUsername(username);
    	}
    	return userAccount;
    }
}