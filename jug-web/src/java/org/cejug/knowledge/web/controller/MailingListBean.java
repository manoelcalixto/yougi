package org.cejug.knowledge.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.knowledge.business.MailingListBsn;
import org.cejug.knowledge.entity.MailingList;

@ManagedBean
@RequestScoped
public class MailingListBean {

    @EJB
    private MailingListBsn mailingListBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private MailingList mailingList;

    public MailingListBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MailingList getMailingList() {
        return mailingList;
    }

    public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
    }

    public List<MailingList> getMailingLists() {
        return mailingListBsn.findMailingLists();
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.mailingList = mailingListBsn.findMailingList(id);
        }
        else {
            this.mailingList = new MailingList();
        }
    }

    public String save() {
        mailingListBsn.save(this.mailingList);
        return "mailing_lists?faces-redirect=true";
    }

    public String remove() {
        mailingListBsn.remove(this.mailingList.getId());
        return "mailing_lists?faces-redirect=true";
    }
}