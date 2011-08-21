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
import org.cejug.web.util.ResourceBundle;

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
            ResourceBundle bundle = new ResourceBundle();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoFirstUser"), ""));
            return "registration";
        }
        else
            return "login?faces-redirect=true";
    }

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
        return "/login?faces-redirect=true";
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

    public Map<String, Object> getSessionMap() {
        return sessionMap;
    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}