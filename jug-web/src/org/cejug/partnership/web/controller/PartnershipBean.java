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
package org.cejug.partnership.web.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.City;
import org.cejug.entity.Country;
import org.cejug.entity.Properties;
import org.cejug.entity.Province;
import org.cejug.entity.UserAccount;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.business.RepresentativeBsn;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;
import org.cejug.web.controller.LocationBean;
import org.cejug.web.util.WebTextUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 * @author Hildeberto Mendonca
 */
@ManagedBean
@RequestScoped
public class PartnershipBean {

    static final Logger logger = Logger.getLogger("org.cejug.partnership.web.controller.PartnershipBean");
    
    @EJB
    private RepresentativeBsn representativeBsn;
    
    @EJB
    private UserAccountBsn userAccountBsn;
    
    @EJB
    private PartnerBsn partnerBsn;
    
    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;
    
    @ManagedProperty(value = "#{locationBean}")
    private LocationBean locationBean;
    
    private Representative representative;
    
    private StreamedContent logoImage;

    public PartnershipBean() {
    }

    public Representative getRepresentative() {
        return representative;
    }

    public void setRepresentative(Representative representative) {
        this.representative = representative;
    }

    public StreamedContent getLogoImage() {
        return logoImage;
    }

    public void setLogoImage(StreamedContent logoImage) {
        this.logoImage = logoImage;
    }

    public String getFormattedPartnerDescription() {
        if (representative != null) {
            String description = this.representative.getPartner().getDescription();
            return WebTextUtils.convertLineBreakToHTMLParagraph(description);
        }
        return null;
    }
    
    public String getFormattedPartnerAddress() {
        Partner partner = this.representative.getPartner();
        return WebTextUtils.printAddress(partner.getAddress(), partner.getCountry(), partner.getProvince(), partner.getCity(), partner.getPostalCode());
    }

    public boolean getRepresentativeExists() {
        if (this.representative.getId() != null) {
            return true;
        }
        return false;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }

    public void setLocationBean(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    @PostConstruct
    public void load() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String username = request.getRemoteUser();
        UserAccount person = userAccountBsn.findUserAccountByUsername(username);
        this.representative = representativeBsn.findRepresentative(person);

        if (this.representative == null) {
            this.representative = new Representative();
            this.representative.setPerson(person);
            Partner newPartner = new Partner();
            this.representative.setPartner(newPartner);
        } else if (!locationBean.isInitialized()) {
            locationBean.initialize();

            if (this.representative.getPartner().getCountry() != null) {
                locationBean.setSelectedCountry(this.representative.getPartner().getCountry().getAcronym());
            }

            if (this.representative.getPartner().getProvince() != null) {
                locationBean.setSelectedProvince(this.representative.getPartner().getProvince().getId());
            }

            if (this.representative.getPartner().getCity() != null) {
                locationBean.setSelectedCity(this.representative.getPartner().getCity().getId());
            }
        }

        loadLogoImage();
    }

    public String save() {
        Country country = this.locationBean.getCountry();
        if (country != null) {
            this.representative.getPartner().setCountry(country);
        }

        Province province = this.locationBean.getProvince();
        if (province != null) {
            this.representative.getPartner().setProvince(province);
        }

        City city = this.locationBean.getCity();
        if (city != null) {
            this.representative.getPartner().setCity(city);
        }

        partnerBsn.save(this.representative.getPartner());
        representativeBsn.save(this.representative);

        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().remove("locationBean");

        return "profile?faces-redirect=true&tab=2";
    }

    public String remove() {
        representativeBsn.remove(representative.getId());
        return "profile?faces-redirect=true";
    }

    private void loadLogoImage() {
        try {
            String logoPath = this.representative.getPartner().getLogo();

            if (logoPath != null) {
                InputStream in = new FileInputStream(new File(logoPath));
                logger.log(Level.INFO, "JUG-0002: Loading logo file {0}", new String[]{logoPath});
                logoImage = new DefaultStreamedContent(in, "image/jpeg");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void handleLogoFileUpload(FileUploadEvent event) {
        UploadedFile uploadedFile = event.getFile();
        logger.log(Level.INFO, "JUG-0001: File {0} of type {1} temporarely uploaded to {2}", new String[]{uploadedFile.getFileName(), uploadedFile.getContentType(), System.getProperty("java.io.tmpdir")});
        try {
            /* Loads the representative related to the logged user. */
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String username = request.getRemoteUser();
            UserAccount person = userAccountBsn.findUserAccountByUsername(username);
            this.representative = representativeBsn.findRepresentative(person);

            /* Write the inputStream to a FileOutputStream */
            InputStream in = uploadedFile.getInputstream();
            ApplicationProperty applicationProperty = applicationPropertyBsn.findApplicationProperty(Properties.FILE_REPOSITORY_PATH);
            String fileExtension = uploadedFile.getFileName();
            fileExtension = fileExtension.substring(fileExtension.indexOf("."));
            StringBuilder filePath = new StringBuilder();
            filePath.append(applicationProperty.getPropertyValue());
            filePath.append("/");
            filePath.append(this.representative.getPartner().getId());
            filePath.append(fileExtension);
            OutputStream out = new FileOutputStream(new File(filePath.toString()));
            int read;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            /* If nothing goes wrong while saving the file,
             * then updates the database with the file location. */
            this.representative.getPartner().setLogo(filePath.toString());
            partnerBsn.save(this.representative.getPartner());

            loadLogoImage();
        } catch (IOException ioe) {
            logger.log(Level.INFO, ioe.getMessage(), ioe);
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage(), e);
        }
        FacesMessage msg = new FacesMessage("Succesful", uploadedFile.getSize() + " bytes of the file " + uploadedFile.getFileName() + " are uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
    
    public String removeLogoImage() {
        try {
            String logoPath = this.representative.getPartner().getLogo();

            if (logoPath != null) {
                File logo = new File(logoPath);
                
                logo.delete();
                InputStream in = new FileInputStream(new File(logoPath));
                logger.log(Level.INFO, "JUG-0002: Loading logo file {0}", new String[]{logoPath});
                logoImage = new DefaultStreamedContent(in, "image/jpeg");
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return "profile?faces-redirect=true&tab=2";
    }
}