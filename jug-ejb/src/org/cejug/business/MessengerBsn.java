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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import org.cejug.entity.*;
import org.cejug.event.entity.Event;
import org.cejug.util.TextUtils;

/**
 * Send email messages according to business needs.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class MessengerBsn {

    @Resource(name = "mail/jug")
    private Session mailSession;

    @EJB
    private MessageTemplateBsn messageTemplateBsn;
    
    static final Logger logger = Logger.getLogger("org.cejug.business.MessengerBsn");
    
    public void sendEmailConfirmationRequest(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("E3F122DCC87D42248872878412B34CEE");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        emailMessage.setHeader(serverAddress);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    public void sendWelcomeMessage(UserAccount userAccount) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("47DEE5C2E0E14F8BA4605F3126FBFAF4");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userAccount.firstName", userAccount.getFirstName());

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the deactivation reason to user "+ userAccount.getUsername(), me);
        }
    }

    public void sendNewMemberAlertMessage(UserAccount newMember, List<UserAccount> leaders) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("0D6F96382D91454F8155A720F3326F1B");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("newMember.fullName", newMember.getFullName());
        values.put("newMember.registrationDate", newMember.getRegistrationDate());

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientsTo(leaders);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending alert to administrators about the registration of "+ newMember.getUsername(), me);
        }
    }

    public void sendDeactivationReason(UserAccount userAccount) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.deactivationReason", userAccount.getDeactivationReason());

        MessageTemplate messageTemplate;
        if(userAccount.getDeactivationType() == DeactivationType.ADMINISTRATIVE) {
            messageTemplate = messageTemplateBsn.findMessageTemplate("03BD6F3ACE4C48BD8660411FC8673DB4");
        }
        else {
            messageTemplate = messageTemplateBsn.findMessageTemplate("IKWMAJSNDOE3F122DCC87D4224887287");
        }

        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the deactivation reason to user "+ userAccount.getUsername(), me);
        }
    }

    public void sendDeactivationAlertMessage(UserAccount userAccount, List<UserAccount> leaders) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userAccount.fullName", userAccount.getFullName());
        values.put("userAccount.deactivationReason", userAccount.getDeactivationReason());

        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("0D6F96382IKEJSUIWOK5A720F3326F1B");
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientsTo(leaders);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the deactivation reason from "+ userAccount.getUsername() +" to leaders.", me);
        }
    }

    public void sendConfirmationCode(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("67BE6BEBE45945D29109A8D6CD878344");
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        emailMessage.setHeader(serverAddress);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the mail confirmation. The registration was not finalized.", me);
        }
    }

    public void sendGroupAssignmentAlert(UserAccount userAccount, AccessGroup accessGroup) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("09JDIIE82O39IDIDOSJCHXUDJJXHCKP0");
        Map<String, Object> values = new HashMap<String, Object>();

        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("accessGroup.name", accessGroup.getName());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the group assignment alert to "+ userAccount.getFullName(), me);
        }
    }
    
    public void sendConfirmationEventAttendance(UserAccount userAccount, Event event, String dateFormat, String timeFormat, String timezone) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("event.name", event.getName());
        values.put("event.venue", event.getVenue().getName());
        values.put("event.startDate", TextUtils.getFormattedDate(event.getStartDate(), dateFormat));
        values.put("event.startTime", TextUtils.getFormattedTime(event.getStartTime(), timeFormat, timezone));
        values.put("event.endTime", TextUtils.getFormattedTime(event.getEndTime(), timeFormat, timezone));

        MessageTemplate messageTemplate;
        messageTemplate = messageTemplateBsn.findMessageTemplate("KJDIEJKHFHSDJDUWJHAJSNFNFJHDJSLE");
        
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            logger.log(Level.WARNING, "Error when sending the confirmation of event attendance to user "+ userAccount.getUsername(), me);
        }
    }
}