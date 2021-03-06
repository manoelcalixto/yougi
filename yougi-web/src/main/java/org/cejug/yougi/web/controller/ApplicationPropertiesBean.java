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
package org.cejug.yougi.web.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.cejug.yougi.business.ApplicationPropertyBsn;
import org.cejug.yougi.business.LanguageBsn;
import org.cejug.yougi.business.LocationBsn;
import org.cejug.yougi.entity.Language;
import org.cejug.yougi.entity.Properties;
import org.cejug.yougi.util.ResourceBundleHelper;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class ApplicationPropertiesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;
    
    @EJB
    private LocationBsn locationBsn;
    
    @EJB
    private LanguageBsn languageBsn;

    private Map<String, String> applicationProperties;

    private Boolean sendEmails;
    
    private List<Language> languages;
        
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
    
    public List<String> getTimeZones() {
        return locationBsn.getTimeZones();
    }
    
    public List<Language> getLanguages() {
        if(this.languages == null)
            this.languages = languageBsn.findLanguages();
        return this.languages;
    }

    public String save() {
        this.applicationProperties.put(Properties.SEND_EMAILS.getKey(), sendEmails.toString());
        applicationPropertyBsn.save(this.applicationProperties);

        ResourceBundleHelper bundle = new ResourceBundleHelper();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoPropertiesSaved"), ""));

        return "properties";
    }

    @PostConstruct
    public void load() {
        applicationProperties = applicationPropertyBsn.findApplicationProperties();

        if(applicationProperties.get(Properties.URL.getKey()).toString().equals("")) {
            applicationProperties.put(Properties.URL.getKey(), getUrl());
        }

        if(applicationProperties.get(Properties.SEND_EMAILS.getKey()).equals("true")) {
            sendEmails = true;
        }
    }

    private String getUrl() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String serverAddress = serverName + (serverPort != 80 ? ":" + serverPort : "") + (contextPath.equals("") ? "" : contextPath);
        return serverAddress;
    }
}