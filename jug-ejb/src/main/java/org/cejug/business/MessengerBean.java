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
package org.cejug.business;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.*;
import org.cejug.entity.Properties;
import org.cejug.event.entity.Event;
import org.cejug.util.TextUtils;

/**
 * Centralizes the posting of all email messages sent by the system and manage
 * the history of those messages. Each kind of message has a method, which can 
 * be involked from different parts of the business logic.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@Stateless
@LocalBean
public class MessengerBean {
    
    @PersistenceContext
    private EntityManager em;

    @Resource(name = "mail/jug")
    private Session mailSession;

    @EJB
    private MessageTemplateBsn messageTemplateBsn;
    
    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;
    
    @EJB
    private MessageHistoryBean messageHistoryBean;
    
    static final Logger logger = Logger.getLogger(MessengerBean.class.getName());
        
    public void sendEmailConfirmationRequest(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("E3F122DCC87D42248872878412B34CEE");
        Map<String, Object> values = new HashMap<>();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    public void sendWelcomeMessage(UserAccount userAccount) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("47DEE5C2E0E14F8BA4605F3126FBFAF4");
        Map<String, Object> values = new HashMap<>();
        values.put("userAccount.firstName", userAccount.getFirstName());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the deactivation reason to user "+ userAccount.getPostingEmail(), me);
        }
    }

    public void sendNewMemberAlertMessage(UserAccount userAccount, List<UserAccount> leaders) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("0D6F96382D91454F8155A720F3326F1B");
        Map<String, Object> values = new HashMap<>();
        values.put("userAccount.fullName", userAccount.getFullName());
        values.put("userAccount.registrationDate", userAccount.getRegistrationDate());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending alert to administrators about the registration of "+ userAccount.getPostingEmail(), me);
        }
    }

    public void sendDeactivationReason(UserAccount userAccount) {
        MessageTemplate messageTemplate;
        if(userAccount.getDeactivationType() == DeactivationType.ADMINISTRATIVE) {
            messageTemplate = messageTemplateBsn.findMessageTemplate("03BD6F3ACE4C48BD8660411FC8673DB4");
        }
        else {
            messageTemplate = messageTemplateBsn.findMessageTemplate("IKWMAJSNDOE3F122DCC87D4224887287");
        }
        em.detach(messageTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.deactivationReason", userAccount.getDeactivationReason());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);

        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the deactivation reason to user "+ userAccount.getPostingEmail(), me);
        }
    }

    public void sendDeactivationAlertMessage(UserAccount userAccount, List<UserAccount> leaders) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("0D6F96382IKEJSUIWOK5A720F3326F1B");
        Map<String, Object> values = new HashMap<>();
        values.put("userAccount.fullName", userAccount.getFullName());
        values.put("userAccount.deactivationReason", userAccount.getDeactivationReason());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the deactivation reason from "+ userAccount.getPostingEmail() +" to leaders.", me);
        }
    }

    public void sendConfirmationCode(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("67BE6BEBE45945D29109A8D6CD878344");
        Map<String, Object> values = new HashMap<>();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }
    
    /**
     * Sends a email to the user that requested to change his/her email address,
     * asking him/her to confirm the request by clicking on the informed link. If
     * the user successfully click on the link it means that his/her email address
     * is valid since he/she could receive the email message successfully.
     * @param userAccount the user who wants to change his/her email address.
     * @param serverAddress the URL of the server where the application is deployed.
     * it will be used to build the URL that the user will click to validate his/her
     * email address.
     */
    public void sendEmailVerificationRequest(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("KJZISKQBE45945D29109A8D6C92IZJ89");
        Map<String, Object> values = new HashMap<>();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.email", userAccount.getEmail());
        values.put("userAccount.unverifiedEmail", userAccount.getUnverifiedEmail());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    public void sendGroupAssignmentAlert(UserAccount userAccount, AccessGroup accessGroup) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("09JDIIE82O39IDIDOSJCHXUDJJXHCKP0");
        Map<String, Object> values = new HashMap<>();
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("accessGroup.name", accessGroup.getName());
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the group assignment alert to "+ userAccount.getFullName(), me);
        }
    }
    
    public void sendConfirmationEventAttendance(UserAccount userAccount, Event event, String dateFormat, String timeFormat, String timezone) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("KJDIEJKHFHSDJDUWJHAJSNFNFJHDJSLE");
        Map<String, Object> values = new HashMap<>();
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("event.name", event.getName());
        values.put("event.venue", event.getVenue().getName());
        values.put("event.startDate", TextUtils.getFormattedDate(event.getStartDate(), dateFormat));
        values.put("event.startTime", TextUtils.getFormattedTime(event.getStartTime(), timeFormat, timezone));
        values.put("event.endTime", TextUtils.getFormattedTime(event.getEndTime(), timeFormat, timezone));
        EmailMessage emailMessage = messageTemplate.replaceVariablesByValues(values);
        emailMessage.setRecipient(userAccount);
        
        try {
            sendEmailMessage(emailMessage);
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the confirmation of event attendance to user "+ userAccount.getPostingEmail(), me);
        }
    }
    
    /**
     * If the application is configured to send emails, it sends the email message
     * based on the message template. The message is saved in the history and an
     * attempt to send the message is done. If successful the historical message
     * is set as sent, otherwise the message is set as not sent and new attempts
     * will be carried out later until the message is successfully sent.
     * @param emailMessage The message to be sent.
     */
    public void sendEmailMessage(EmailMessage emailMessage) throws MessagingException {
        ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);
        if(!appProp.sendEmailsEnabled()) {
            return;
        }
        
        MessagingException messagingException = null;
        
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch (MessagingException me) {
            messagingException = me;
        }
        
        List<MessageHistory> messagesHistory = MessageHistory.createHistoricMessages(emailMessage);
        for(MessageHistory messageHistory: messagesHistory) {
            if(messagingException == null) {
                messageHistory.setMessageSent(Boolean.TRUE);
                messageHistory.setDateSent(Calendar.getInstance().getTime());
            }
            else {
                messageHistory.setMessageSent(Boolean.FALSE);
            }
            messageHistoryBean.saveHistoricalMessage(messageHistory);
        }
        
        if(messagingException != null) {
            throw messagingException;
        }
    }
}