package org.cejug.knowledge.business;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import org.cejug.knowledge.entity.MailingListMessage;

/**
 *
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class MailingListBsn {

    @Resource(name = "mail/jug")
    private Session mailSession;

    static final Logger logger = Logger.getLogger("org.cejug.knowledge.business.MailingListBsn");

    @Schedules({ @Schedule(hour="*", minute="*/1") })
    public void downloadMailingListMessages() {
        try {
            Store store = mailSession.getStore("imap");
            //store.connect("imap.usi4biz.com", 143, "community@usi4biz.com", "aveleb0920");
            store.connect();
            Folder inboxFolder = store.getDefaultFolder();
            if(inboxFolder == null)
                inboxFolder = store.getFolder("INBOX");
            inboxFolder.open(Folder.READ_ONLY);

            Message[] messages = inboxFolder.getMessages();
            Message message;
            MailingListMessage mailingListMessage;
            logger.log(Level.INFO, "messages: {0}", messages.length);
            for(int i = 0,n = messages.length;i < n; i++) {
                message = messages[i];
                //mailingListMessage = new MailingListMessage();
                //mailingListMessage.setSubject(message.getSubject());
                //mailingListMessage.setSender(null);
                logger.log(Level.INFO, "subject: {0}", message.getSubject());
            }

        } catch (NoSuchProviderException ex) {
            logger.log(Level.SEVERE, "Email provider not available", ex);
        } catch (MessagingException ex) {
            logger.log(Level.SEVERE, "Failure to connect to the server", ex);
        }
    }
}