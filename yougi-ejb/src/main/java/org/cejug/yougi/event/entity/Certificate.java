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
package org.cejug.yougi.event.entity;

import java.util.Date;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
public class Certificate {
    private String certificateFullname;
    
    private String certificateEvent;
    
    private String certificateVenue;
    
    private Date certificateDate;
    
    private String certificateCode;

    /**
     * @return the certificateFullname
     */
    public String getCertificateFullname() {
        return certificateFullname;
    }

    /**
     * @param certificateFullname the certificateFullname to set
     */
    public void setCertificateFullname(String certificateFullname) {
        this.certificateFullname = certificateFullname;
    }

    /**
     * @return the certificateEvent
     */
    public String getCertificateEvent() {
        return certificateEvent;
    }

    /**
     * @param certificateEvent the certificateEvent to set
     */
    public void setCertificateEvent(String certificateEvent) {
        this.certificateEvent = certificateEvent;
    }

    /**
     * @return the certificateVenue
     */
    public String getCertificateVenue() {
        return certificateVenue;
    }

    /**
     * @param certificateVenue the certificateVenue to set
     */
    public void setCertificateVenue(String certificateVenue) {
        this.certificateVenue = certificateVenue;
    }

    /**
     * @return the certificateDate
     */
    public Date getCertificateDate() {
        return certificateDate;
    }

    /**
     * @param certificateDate the certificateDate to set
     */
    public void setCertificateDate(Date certificateDate) {
        this.certificateDate = certificateDate;
    }

    /**
     * @return the certificateCode
     */
    public String getCertificateCode() {
        return certificateCode;
    }

    /**
     * @param certificateCode the certificateCode to set
     */
    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }
}
