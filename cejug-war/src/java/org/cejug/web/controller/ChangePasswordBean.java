package org.cejug.web.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;

@ManagedBean
@RequestScoped
public class ChangePasswordBean {

    @EJB
    private UserAccountBsn userAccountBsn;

    @ManagedProperty(value="#{param.cc}")
    private String confirmationCode;

    @ManagedProperty(value="#{applicationPropertiesBean}")
    private ApplicationPropertiesBean applicationPropertiesBean;

    private String currentPassword;
    private String username;
    private String password;
    private String confirmPassword;
    private Boolean invalid;

    public ChangePasswordBean() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public ApplicationPropertiesBean getApplicationPropertiesBean() {
        return applicationPropertiesBean;
    }

    public void setApplicationPropertiesBean(ApplicationPropertiesBean applicationPropertiesBean) {
        this.applicationPropertiesBean = applicationPropertiesBean;
    }

    public Boolean getInvalid() {
        return invalid;
    }

    public void setInvalid(Boolean invalid) {
        this.invalid = invalid;
    }

    @PostConstruct
    public void load() {
        if(confirmationCode != null && !confirmationCode.isEmpty()) {
            UserAccount userAccount = userAccountBsn.findUserAccountByConfirmationCode(confirmationCode);
            if(userAccount != null)
                this.username = userAccount.getUsername();
            else
                invalid = true;
        }
    }

    public String requestPasswordChange() {
        try {
            String serverAddress = applicationPropertiesBean.getUrl();
            userAccountBsn.requestConfirmationPasswordChange(username, serverAddress);
        }
        catch(EJBException ee) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(ee.getCausedByException().getMessage()));
            return "request_password_change";
        }
        return "change_password";
    }

    public String changeForgottenPassword() {
        UserAccount userAccount = userAccountBsn.findUserAccountByConfirmationCode(confirmationCode);

        if(userAccount == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The confirmation code does not match."));
            return "change_password";
        }
        
        userAccount.setPassword(password);
        userAccount.setConfirmPassword(confirmPassword);

        if(!userAccount.isPasswordConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The password confirmation does not match."));
            return "change_password";
        }

        userAccountBsn.changePassword(userAccount);
        return "login?faces-redirect=true";
    }

    public String changePassword() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        username = request.getRemoteUser();
        UserAccount userAccount = userAccountBsn.findUserAccountByUsername(username);
        if(!userAccountBsn.passwordMatches(userAccount, currentPassword)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The current password does not match."));
            return "change_password";
        }

        userAccount.setPassword(password);
        userAccount.setConfirmPassword(confirmPassword);

        if(!userAccount.isPasswordConfirmed()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("The password confirmation does not match."));
            return "change_password";
        }

        userAccountBsn.changePassword(userAccount);
        return "profile?faces-redirect=true";
    }
}