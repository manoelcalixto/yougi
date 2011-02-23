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
import org.cejug.entity.Properties;

@ManagedBean
@SessionScoped
public class ApplicationPropertiesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    private Map<String, String> applicationProperties;

    private Boolean sendEmails;

    public Map<String, String> getApplicationProperties() {
        return applicationProperties;
    }

    public void setApplicationProperties(Map<String, String> applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public Boolean getSendEmails() {
        return sendEmails;
    }

    public void setSendEmails(Boolean sendEmails) {
        this.sendEmails = sendEmails;
    }

    public String save() {
        this.applicationProperties.put(Properties.SEND_EMAILS.getKey(), sendEmails.toString());
        applicationPropertyBsn.save(this.applicationProperties);
        return "properties?faces-redirect=true";
    }

    @PostConstruct
    public void load() {
        applicationProperties = applicationPropertyBsn.findApplicationProperties();

        if(applicationProperties.get(Properties.URL.getKey()).toString().equals("")) {
            applicationProperties.put(Properties.URL.getKey(), getUrl());
        }
    }

    public String getUrl() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String serverAddress = serverName + (serverPort != 80 ? ":" + serverPort : "") + (contextPath.equals("") ? "" : contextPath);
        return serverAddress;
    }
}