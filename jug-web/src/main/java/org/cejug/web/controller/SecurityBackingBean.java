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

import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundleHelper;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class SecurityBackingBean {
    @EJB
    private UserAccountBsn userAccountBsn;

    @ManagedProperty(value="#{sessionScope}")
    private Map<String, Object> sessionMap;

    public UserAccount getSignedUser() {
        return (UserAccount) sessionMap.get("signedUser");
    }

    public void setSignedUser(UserAccount signedUser) {
        sessionMap.remove("signedUser");
        if(null != signedUser) {
            sessionMap.put("signedUser", signedUser);
        }
    }

    public boolean isUserSignedIn() {
        return sessionMap.containsKey("signedUser");
    }

    public String login() {
        if(userAccountBsn.noAccount()) {
            ResourceBundleHelper bundle = new ResourceBundleHelper();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoFirstUser"), ""));
            return "registration";
        }
        else
            return "login?faces-redirect=true";
    }

    /**
     * Performe the logout of the user by removing the user from the session and
     * distroying the session.
     * @return The next step in the navigation flow.
     */
    public String logout() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        try {
            request.logout();
            session.invalidate();
        }
        catch(ServletException se) {
            return "/index?faces-redirect=true";
        }
        return "/index?faces-redirect=true";
    }

    public Boolean getIsUserLeader() {
        Boolean result = false;
        FacesContext context = FacesContext.getCurrentInstance();
        Object request = context.getExternalContext().getRequest();
        if(request instanceof HttpServletRequest) {
            result = ((HttpServletRequest)request).isUserInRole("leader");
        }
        return result;
    }
    
    public Boolean getIsUserPartner() {
        Boolean result = false;
        FacesContext context = FacesContext.getCurrentInstance();
        Object request = context.getExternalContext().getRequest();
        if(request instanceof HttpServletRequest) {
            result = ((HttpServletRequest)request).isUserInRole("partner");
        }
        return result;
    }

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}