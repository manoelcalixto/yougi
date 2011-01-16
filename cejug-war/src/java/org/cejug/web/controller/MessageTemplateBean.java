package org.cejug.web.controller;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.MessageTemplateBsn;
import org.cejug.entity.MessageTemplate;

@ManagedBean
@RequestScoped
public class MessageTemplateBean {

    @EJB
    private MessageTemplateBsn messageTemplateBsn;

    @ManagedProperty(value="#{param.id}")
    private String id;

    private MessageTemplate messageTemplate;

    public MessageTemplateBean() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MessageTemplate getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(MessageTemplate messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public List<MessageTemplate> getMessageTemplates() {
        return messageTemplateBsn.findMessageTemplates();
    }

    @PostConstruct
    public void load() {
        if(id != null && !id.isEmpty()) {
            this.messageTemplate = messageTemplateBsn.findMessageTemplate(id);
        }
        else {
            this.messageTemplate = new MessageTemplate();
        }
    }

    public String save() {
        messageTemplateBsn.save(this.messageTemplate);
        return "message_templates?faces-redirect=true";
    }

    public String remove() {
        if(messageTemplate != null)
            messageTemplateBsn.remove(messageTemplate.getId());
        return "message_templates?faces-redirect=true";
    }

    public Boolean getExistent() {
        if(messageTemplate.getId() != null && !messageTemplate.getId().isEmpty())
            return true;
        else
            return false;
    }
}