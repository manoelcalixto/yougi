package org.cejug.entity;

import java.io.UnsupportedEncodingException;
import java.util.List;
import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Adapts a mime email message to the application domain, considering a
 * UserAccount as a usual recipient.
 * @author Hildeberto Mendonca
 */
public class EmailMessage extends Message {

    private UserAccount[] recipientsTo;
    private UserAccount[] recipientsCopyTo;
    private UserAccount[] recipientsBlindCopyTo;
    private String subject;

    public UserAccount[] getRecipientsTo() {
        return recipientsTo;
    }

    public UserAccount getRecipientTo() {
        if(recipientsTo != null)
            return recipientsTo[0];
        
        return null;
    }

    public void setRecipientsTo(List<UserAccount> recipientsTo) {
        if(recipientsTo == null)
            return;

        this.recipientsTo = new UserAccount[recipientsTo.size()];
        int i = 0;
        for(UserAccount recipient: recipientsTo) {
            this.recipientsTo[i++] = recipient;
        }
    }

    public void setRecipientTo(UserAccount recipientTo) {
        if(recipientsTo == null) {
            recipientsTo = new UserAccount[1];
        }
        recipientsTo[0] = recipientTo;
    }

    public UserAccount[] getRecipientsCopyTo() {
        return recipientsCopyTo;
    }

    public UserAccount getRecipientCopyTo() {
        if(recipientsCopyTo != null)
            return recipientsCopyTo[0];

        return null;
    }

    public void setRecipientsCopyTo(List<UserAccount> recipientsCopyTo) {
        if(recipientsCopyTo == null)
            return;

        this.recipientsCopyTo = new UserAccount[recipientsCopyTo.size()];
        int i = 0;
        for(UserAccount recipient: recipientsCopyTo) {
            this.recipientsCopyTo[i++] = recipient;
        }
    }

    public void setRecipientCopyTo(UserAccount recipientCopyTo) {
        if(recipientsCopyTo == null)
            recipientsCopyTo = new UserAccount[1];
        recipientsCopyTo[0] = recipientCopyTo;
    }

    public UserAccount[] getRecipientsBlindCopyTo() {
        return recipientsBlindCopyTo;
    }

    public UserAccount getRecipientBlindCopyTo() {
        if(recipientsBlindCopyTo != null)
            return recipientsBlindCopyTo[0];

        return null;
    }

    public void setRecipientsBlindCopyTo(List<UserAccount> recipientsBlindCopyTo) {
        if(recipientsBlindCopyTo == null)
            return;

        this.recipientsBlindCopyTo = new UserAccount[recipientsBlindCopyTo.size()];
        int i = 0;
        for(UserAccount recipient: recipientsBlindCopyTo) {
            this.recipientsBlindCopyTo[i++] = recipient;
        }
    }

    public void setRecipientBlindCopyTo(UserAccount recipientBlindCopyTo) {
        if(recipientsBlindCopyTo == null)
            recipientsBlindCopyTo = new UserAccount[1];
        this.recipientsBlindCopyTo[0] = recipientBlindCopyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public MimeMessage createMimeMessage(Session mailSession, UserAccount sender) {
        MimeMessage mimeMessage = createMimeMessage(mailSession);
        try {
            Address sndr = new InternetAddress(sender.getEmail(), sender.getFullName());
            mimeMessage.setSender(sndr);
        } catch (MessagingException me) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.",me);
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.", uee);
        }
        return mimeMessage;
    }

    public MimeMessage createMimeMessage(Session mailSession) {
        try {
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setSubject(this.getSubject(), "UTF-8");
            Address[] recipients;

            if(recipientsTo != null) {
                recipients = new Address[recipientsTo.length];
                for(int i = 0;i < recipientsTo.length;i++) {
                    recipients[i] = new InternetAddress(recipientsTo[i].getEmail(), recipientsTo[i].getFullName());
                }
                msg.setRecipients(RecipientType.TO, recipients);
            }

            if(recipientsCopyTo != null) {
                recipients = new Address[recipientsCopyTo.length];
                for(int i = 0;i < recipientsCopyTo.length;i++) {
                    recipients[i] = new InternetAddress(recipientsCopyTo[i].getEmail(), recipientsCopyTo[i].getFullName());
                }
                msg.setRecipients(RecipientType.CC, recipients);
            }

            if(recipientsBlindCopyTo != null) {
                recipients = new Address[recipientsBlindCopyTo.length];
                for(int i = 0;i < recipientsBlindCopyTo.length;i++) {
                    recipients[i] = new InternetAddress(recipientsBlindCopyTo[i].getEmail(), recipientsBlindCopyTo[i].getFullName());
                }
                msg.setRecipients(RecipientType.BCC, recipients);
            }

            msg.setText(getBody(), "UTF-8");
            msg.setHeader("Content-Type", "text/html;charset=UTF-8");

            return msg;
        } catch (MessagingException me) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.",me);
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.", uee);
        }
    }
}