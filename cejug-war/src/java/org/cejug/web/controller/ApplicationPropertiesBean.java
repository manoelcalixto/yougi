package org.cejug.web.controller;

import java.io.Serializable;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.cejug.business.ApplicationPropertyBsn;

@ManagedBean
@SessionScoped
public class ApplicationPropertiesBean implements Serializable {

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    private Map applicationProperties;

    public Map getApplicationProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(Map applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public String save() {
        applicationPropertyBsn.save(applicationProperties);
        return "properties?faces-redirect=true";
    }

    @PostConstruct
    public void load() {
        applicationProperties = applicationPropertyBsn.findApplicationProperties();
    }

    public String getUrl() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String serverAddress = serverName + (serverPort != 80?":"+ serverPort:"") + (contextPath.equals("")?"":contextPath);
        return serverAddress;
    }
}