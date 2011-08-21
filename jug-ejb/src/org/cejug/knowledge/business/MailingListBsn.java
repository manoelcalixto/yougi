package org.cejug.knowledge.business;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.business.ApplicationPropertyBsn;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.Properties;
import org.cejug.entity.UserAccount;
import org.cejug.knowledge.entity.MailingList;
import org.cejug.knowledge.entity.MailingListMessage;
import org.cejug.knowledge.entity.MailingListSubscription;
import org.cejug.util.EntitySupport;

/**
 * Implements the business logic related to the management of mailing lists.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class MailingListBsn {

    @Resource(name = "mail/jug")
    private Session mailSession;

    @PersistenceContext
    private EntityManager em;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;

    static final Logger logger = Logger.getLogger("org.cejug.knowledge.business.MailingListBsn");

    public MailingList findMailingList(String id) {
        return em.find(MailingList.class, id);
    }

    public List<MailingList> findMailingLists() {
        return em.createQuery("select ml from MailingList ml order by ml.name asc").getResultList();
    }

    public MailingList findMailingListByEmail(String email) {
        try {
            return (MailingList) em.createQuery("select ml from MailingList ml where ml.email = :email")
                                   .setParameter("email", email)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public MailingListSubscription findMailingListSubscription(MailingList mailingList, UserAccount userAccount) {
        try {
            return (MailingListSubscription) em.createQuery("select mls from MailingListSubscription mls where mls.mailingList = :mailingList and mls.emailAddress = :email and mls.unsubscriptionDate is null")
                                   .setParameter("mailingList", mailingList)
                                   .setParameter("email", userAccount.getEmail())
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public List<MailingListSubscription> findMailingListSubscriptions(UserAccount userAccount) {
        return em.createQuery("select mls from MailingListSubscription mls where mls.emailAddress = :email and mls.unsubscriptionDate is null")
                 .setParameter("email", userAccount.getEmail())
                 .getResultList();
    }

    /** Subscribes the user in several mailing lists. */
    public void subscribe(List<MailingList> mailingLists, UserAccount userAccount) {
        if(mailingLists.isEmpty()) {
            unsubscribeAll(userAccount);
            userAccount.setMailingList(Boolean.FALSE);
            return;
        }
        else
            userAccount.setMailingList(Boolean.TRUE);

        List<MailingListSubscription> mailingListSubscriptions = findMailingListSubscriptions(userAccount);
        boolean found;
        // Check if the user is already registered in the informed mailing lists.
        for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
            found = false;
            for(MailingList mailingList: mailingLists) {
                // If true, the user is already registered. No action needed.
                if(mailingListSubscription.getMailingList().equals(mailingList)) {
                    mailingLists.remove(mailingList);
                    found = true;
                    break;
                }
            }
            // If one of the existing registrations was not found in the informed mailing lists,
            // then the user is unsubscribed.
            if(!found) {
                unsubscribe(mailingListSubscription.getMailingList(), userAccount);
            }
        }
        // If there is any remaining mailing lists in the list, the user is registered to them.
        for(MailingList mailingList: mailingLists) {
            subscribe(mailingList, userAccount);
        }
    }

    /** Subscribes the user in the informed mailing list. */
    public void subscribe(MailingList mailingList, UserAccount userAccount) {
        Calendar today = Calendar.getInstance();

        // Reactivates an ancient subscription or creates a new one.
        MailingListSubscription mailingListSubscription = new MailingListSubscription();
        mailingListSubscription.setId(EntitySupport.generateEntityId());
        mailingListSubscription.setMailingList(mailingList);
        mailingListSubscription.setUserAccount(userAccount);
        mailingListSubscription.setEmailAddress(userAccount.getEmail());
        mailingListSubscription.setSubscriptionDate(today.getTime());
        em.persist(mailingListSubscription);
    }

    /** Unsubscribes the user from all mailing lists. */
    public void unsubscribeAll(UserAccount userAccount) {
        List<MailingListSubscription> mailingListSubscriptions = findMailingListSubscriptions(userAccount);
        if(mailingListSubscriptions != null) {
            for(MailingListSubscription mailingListSubscription: mailingListSubscriptions) {
                Calendar today = Calendar.getInstance();
                mailingListSubscription.setUnsubscriptionDate(today.getTime());
                em.merge(mailingListSubscription);
            }
        }
    }

    /** Unsubscribes the user from the informed mailing list. */
    public void unsubscribe(MailingList mailingList, UserAccount userAccount) {
        MailingListSubscription mailingListSubscription = findMailingListSubscription(mailingList, userAccount);
        if(mailingListSubscription != null) {
            Calendar today = Calendar.getInstance();
            mailingListSubscription.setUnsubscriptionDate(today.getTime());
        }
    }

    public void save(MailingList mailingList) {
        if(mailingList.getId() == null || mailingList.getId().isEmpty()) {
            mailingList.setId(EntitySupport.generateEntityId());
            em.persist(mailingList);
        }
        else {
            em.merge(mailingList);
        }
    }

    public void remove(String id) {
        MailingList mailingList = em.find(MailingList.class, id);
        if(mailingList != null)
            em.remove(mailingList);
    }

    @Schedule(hour="*/1",persistent=false) // Production
    //@Schedule(hour="*",minute="*/2",persistent=false) // Development
    public void retrieveMailingListMessages() {

        try {
            logger.log(Level.INFO, "Start retrieving of emails...");
            ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.EMAIL_SERVER_TYPE);
            Store store = mailSession.getStore(appProp.getPropertyValue());
            store.connect();
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] message = folder.getMessages();
            MailingListMessage mailingListMessage;
            String address;
            List<MailingList> mailingLists;

            for (int i = 0, n = message.length; i < n; i++) {
                /* Stores in the database only those messages that were sent to
                 * a registered mailing list. */
                mailingLists = figureOutMailingLists(message[i].getAllRecipients());
                if(mailingLists == null || mailingLists.isEmpty())
                    continue;

                mailingListMessage = new MailingListMessage();
                mailingListMessage.setId(EntitySupport.generateEntityId());
                mailingListMessage.setSubject(message[i].getSubject());

                /* Get the email address of the 'from' field and set the sender. */
                address = message[i].getFrom()[0].toString();
                if(address.indexOf("<") >= 0)
                    address = address.substring(address.indexOf("<") + 1, address.indexOf(">"));
                address = address.toLowerCase();
                mailingListMessage.setSender(address);

                /* This part tries to get the full content of the message to
                 * store in the database. For that, a simple OutputStream
                 * implementation writes the whole content of the message in a
                 * string, which defines the attribute body of the
                 * mailingListMessage object. */
                OutputStream output = new OutputStream() {
                    private StringBuilder string = new StringBuilder();

                    @Override
                    public void write(int b) throws IOException {
                        this.string.append((char) b );
                    }

                    @Override
                    public String toString() {
                        return this.string.toString();
                    }
                };
                message[i].writeTo(output);
                mailingListMessage.setBody(output.toString());

                mailingListMessage.setWhenReceived(message[i].getReceivedDate());

                mailingListMessage.setMailingList(mailingLists.get(0));
                em.persist(mailingListMessage);
                // Once persisted, the message is flagged to be deleted.
                message[i].setFlag(Flags.Flag.DELETED, true);
                logger.log(Level.INFO, "Message -{0}- sent by -{1}- saved.", new Object[]{mailingListMessage.getSubject(),mailingListMessage.getSender()});

                for(int j = 1;j < mailingLists.size();j++) {
                    mailingListMessage = (MailingListMessage)mailingListMessage.clone();
                    mailingListMessage.setMailingList(mailingLists.get(j));
                    em.persist(mailingListMessage);
                }
            }
            // if true, all messages flagged to be deleted will actually be deleted.
            folder.close(true);
            store.close();
            logger.log(Level.INFO, "Email retrieval ended.");
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (MessagingException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    /** Check the recipients to detect for which mailing lists the message was sent. */
    private List<MailingList> figureOutMailingLists(Address[] extendedListAddresses) {
        String listAddress;
        List<MailingList> mailingLists = new ArrayList<MailingList>();
        MailingList mailingList;
        for(int i = 0;i < extendedListAddresses.length;i++) {
            listAddress = extendedListAddresses[i].toString();
            if(listAddress.indexOf("<") >= 0)
                listAddress = listAddress.substring(listAddress.indexOf("<") + 1, listAddress.indexOf(">"));
            listAddress = listAddress.toLowerCase();
            mailingList = findMailingListByEmail(listAddress);
            if(mailingList != null)
                mailingLists.add(mailingList);
        }
        return mailingLists;
    }
}