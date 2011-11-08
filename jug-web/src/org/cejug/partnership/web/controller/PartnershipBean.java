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
import org.cejug.entity.Properties;
import org.cejug.entity.UserAccount;
import org.cejug.partnership.business.PartnerBsn;
import org.cejug.partnership.business.RepresentativeBsn;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;
import org.cejug.web.util.WebTextUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

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
    
    @ManagedProperty(value="#{param.id}")
    private String id;

    private Representative representative;
    
    private StreamedContent logoImage;

    public PartnershipBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    	if(representative != null) {
    		String description = this.representative.getPartner().getDescription();
    		return WebTextUtils.convertLineBreakToHTMLParagraph(description);
    	}
    	return null;
    }
    
    public boolean getRepresentativeExists() {
    	if(this.representative.getId() != null) {
    		return true;
    	}
    	return false;
    }
        
    @PostConstruct
    public void load() {
    	HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String username = request.getRemoteUser();
        UserAccount person = userAccountBsn.findUserAccountByUsername(username);
        this.representative = representativeBsn.findRepresentative(person);
        
        if(this.representative == null) {
        	this.representative = new Representative();
        	this.representative.setPerson(person);
        	Partner newPartner = new Partner();
        	this.representative.setPartner(newPartner);
        }
        
        loadLogoImage();
    }

    public String save() {
    	partnerBsn.save(this.representative.getPartner());
        representativeBsn.save(this.representative);
        
        return "profile?faces-redirect=true&tab=2";
    }

    public String remove() {
        representativeBsn.remove(representative.getId());
        return "profile?faces-redirect=true";
    }
    

    public void loadLogoImage() {
    	try {
			String logoPath = this.representative.getPartner().getLogo();
			
			if(logoPath != null) {
				InputStream in = new FileInputStream(new File(logoPath));
				logger.log(Level.INFO, "JUG-0002: Loading logo file {0}", new String[]{logoPath});
				logoImage = new DefaultStreamedContent(in, "image/jpeg");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void handleFileUpload(FileUploadEvent event) {
		UploadedFile uploadedFile = event.getFile();
		logger.log(Level.INFO, "JUG-0001: File {0} of type {1} temporarely uploaded to {2}", new String[]{uploadedFile.getFileName(), uploadedFile.getContentType(),System.getProperty("java.io.tmpdir")});
		try {
			/* Loads the representative related to the logged user. */
			HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
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
			int read = 0;
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
		}
		catch(IOException ioe) {
			logger.log(Level.INFO, ioe.getMessage(), ioe);
		}
		catch(Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
        FacesMessage msg = new FacesMessage("Succesful", uploadedFile.getSize() +" bytes of the file "+ uploadedFile.getFileName() + " are uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}