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
package org.cejug.event.web.controller;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.cejug.event.business.AttendeeBsn;
import org.cejug.event.business.EventBsn;
import org.cejug.event.entity.Certificate;
import org.cejug.web.util.ResourceBundleHelper;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class CertificateBean {

    static final Logger logger = Logger.getLogger("org.cejug.event.web.controller.EventBean");

    @EJB
    private EventBsn eventBsn;

    @EJB
    private AttendeeBsn attendeeBsn;

    private Certificate certificate;

    public CertificateBean() {
        certificate = new Certificate();
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public String verifyAuthenticity() {
        boolean verified = attendeeBsn.verifyAuthenticityCertificate(this.certificate);
        ResourceBundleHelper bundle = new ResourceBundleHelper();
        if(verified) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getMessage("infoCode0001"), ""));
        }
        else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, bundle.getMessage("warnCode0001"), bundle.getMessage("warnCode0002")));
        }
        return "certificate_validation";
    }
}