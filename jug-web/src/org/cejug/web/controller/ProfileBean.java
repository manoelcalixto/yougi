package org.cejug.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.Properties;
import org.cejug.entity.UserAccount;
import org.cejug.util.MD5Util;

@ManagedBean
@RequestScoped
public class ProfileBean {

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private ApplicationPropertyBsn appPropertyBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private UserAccount userAccount;

    public ProfileBean() {}

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getPhotoURL() {
        String urlStr = "http://www.gravatar.com/avatar/" + MD5Util.md5Hex(this.userAccount.getEmail()) + ".jpg";
        if(userAccount.getPhoto() == null) {
            try {
                ApplicationProperty applicationProperty = appPropertyBsn.findApplicationProperty(Properties.FILE_REPOSITORY_PATH);
                String fileRepositoryPath = applicationProperty.getPropertyValue();

                if(!fileRepositoryPath.isEmpty()) {
                    String profilePicturesFolder = "members/pictures/";
                    File folder = new File(fileRepositoryPath + profilePicturesFolder);

                    boolean folderExists = folder.exists();

                    if(!folderExists)
                        folderExists = folder.mkdirs();

                    if(folderExists) {
                        String profilePicturePath = profilePicturesFolder + userAccount.getId() + ".jpg";
                        FileOutputStream fos = new FileOutputStream(fileRepositoryPath + profilePicturePath);

                        URL url = new URL(urlStr);
                        InputStream is = url.openStream();

                        byte[] buffer = new byte[100000];
                        int bytesRead = 0;
                        while((bytesRead = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, bytesRead);
                            buffer = new byte[100000];
                        }
                        fos.close();
                        is.close();
                        userAccountBsn.updateProfilePicture(userAccount, profilePicturePath);
                    }
                }
            }
            catch(MalformedURLException mue) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("MalformedURLException."));
            }
            catch(IOException ioe) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("IOException."));
            }
        }
        return urlStr;
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.userAccount = userAccountBsn.findUserAccount(this.id);
        }
        else {
            this.userAccount = new UserAccount();
        }
    }
}