package org.cejug.business;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.City;
import org.cejug.entity.Contact;
import org.cejug.entity.ContactType;
import org.cejug.entity.EmailMessage;
import org.cejug.entity.MessageTemplate;
import org.cejug.entity.UserAccount;
import org.cejug.entity.UserGroup;
import org.cejug.util.Base64Encoder;
import org.cejug.util.EntitySupport;

@Stateless
@LocalBean
public class UserAccountBsn {

    @Resource(name = "mail/cejug")
    private Session mailSession;

    @Resource
    private TimerService timer;

    @EJB
    private AccessGroupBsn accessGroupBsn;

    @EJB
    private UserGroupBsn userGroupBsn;

    @EJB
    private LocationBsn locationBsn;

    @EJB
    private MessageTemplateBsn messageTemplateBsn;
    
    @PersistenceContext
    private EntityManager em;

    public boolean existingAccount(UserAccount userAccount) {
        UserAccount existing = findUserAccountByUsername(userAccount.getUsername());
        return existing != null;
    }

    /** Returns true is there is no account registered in the database. */
    public boolean noAccount() {
        Long totalUserAccounts = (Long)em.createQuery("select count(u) from UserAccount u").getSingleResult();
        if(totalUserAccounts == 0)
            return true;
        else
            return false;
    }

    public UserAccount findUserAccount(String id) {
        return em.find(UserAccount.class, id);
    }

    public UserAccount findUserAccountByUsername(String username) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.username = :username")
                                   .setParameter("username", username)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public UserAccount findUserAccountByConfirmationCode(String confirmationCode) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :confirmationCode")
                                   .setParameter("confirmationCode", confirmationCode)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }

    public List<UserAccount> findUserAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated order by ua.firstName")
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    public List<UserAccount> findUserAccountsOrderedByRegistration() {
        return em.createQuery("select ua from UserAccount ua order by ua.registrationDate")
                 .getResultList();
    }

    public List<UserAccount> findRegisteredUsersSince(Date date) {
        return em.createQuery("select ua from UserAccount ua where ua.registrationDate >= :date and ua.deactivated = :deactivated order by ua.registrationDate desc")
                 .setParameter("date", date)
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    public List<UserAccount> findUserAccountsStartingWith(String firstLetter) {
        return em.createQuery("select ua from UserAccount ua where ua.firstName like '"+ firstLetter +"%' and ua.deactivated = :deactivated order by ua.firstName")
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    public List<UserAccount> findInhabitantsFrom(City city) {
        return em.createQuery("select c.user from Contact c where c.city = :city and c.user.deactivated = :deactivated order by c.user.firstName")
                .setParameter("city", city)
                .setParameter("deactivated", Boolean.FALSE)
                .getResultList();
    }

    public void register(UserAccount userAccount, Contact mainContact, City newCity, String serverAddress) {
        boolean noAccount = noAccount();

        mainContact.setLocation(ContactType.MAIN);
        mainContact.setId(EntitySupport.generateEntityId());
        
        if(newCity != null) {
            newCity.setId(EntitySupport.generateEntityId());
            locationBsn.saveCity(newCity);
            mainContact.setCity(newCity);
        }
        userAccount.setMainContact(mainContact);
        userAccount.setConfirmationCode(generateConfirmationCode());
        userAccount.setId(EntitySupport.generateEntityId());
        em.persist(userAccount);

        if(noAccount) {
            AccessGroup adminGroup = accessGroupBsn.findAdministrativeGroup();
            UserGroup userGroup = new UserGroup(adminGroup, userAccount);
            userGroupBsn.add(userGroup);
        }
        else {
            AccessGroup defaultGroup = accessGroupBsn.findUserDefaultGroup();
            UserGroup userGroup = new UserGroup(defaultGroup, userAccount);
            userGroupBsn.add(userGroup);
        }
        
        sendEmailConfirmationRequest(userAccount, serverAddress);
    }

    public void save(UserAccount userAccount) {
        userAccount.setLastUpdate(Calendar.getInstance().getTime());
        em.merge(userAccount);
    }

    private void sendEmailConfirmationRequest(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("E3F122DCC87D42248872878412B34CEE");
        Map values = new HashMap();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(messageTemplate.getTitle());
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        emailMessage.setHeader(serverAddress);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.",me);
        }
    }

    public void confirmUser(String confirmationCode) {
        try {
            UserAccount userAccount = (UserAccount)em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :code")
                                                     .setParameter("code", confirmationCode)
                                                     .getSingleResult();
            userAccount.setConfirmationCode("");
            userAccount.setRegistrationDate(Calendar.getInstance().getTime());
            userAccount.setPassword(encryptPassword(userAccount.getPassword()));
            sendWelcomeMessage(userAccount);
            sendNewMemberAlertMessage(userAccount);
        }
        catch(NoResultException nre) {
            throw new IllegalArgumentException("Confirmation code "+ confirmationCode +" does not match any existing pendent account.", nre.getCause());
        }
    }

    private void sendWelcomeMessage(UserAccount userAccount) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("47DEE5C2E0E14F8BA4605F3126FBFAF4");
        Map values = new HashMap();
        values.put("userAccount.firstName", userAccount.getFirstName());
        
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(messageTemplate.getTitle());
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            throw new RuntimeException("Error when sending the deactivation reason to user "+ userAccount.getUsername(),me);
        }
    }

    private void sendNewMemberAlertMessage(UserAccount newMember) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("0D6F96382D91454F8155A720F3326F1B");
        Map values = new HashMap();
        values.put("newMember.fullName", newMember.getFullName());
        values.put("newMember.registrationDate", newMember.getRegistrationDate());
        
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(messageTemplate.getTitle());
        
        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
        emailMessage.setRecipientsTo(leaders);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            throw new RuntimeException("Error when sending alert to administrators about the registration of "+ newMember.getUsername(),me);
        }
    }

    public void deactivateMembership(UserAccount userAccount) {
        UserAccount existingUserAccount = findUserAccount(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());

        save(existingUserAccount);
        userGroupBsn.removeUserFromAllGroups(userAccount);
        if(!existingUserAccount.getDeactivationReason().trim().isEmpty()) {
            sendDeactivationReason(existingUserAccount);
        }
    }

    private void sendDeactivationReason(UserAccount userAccount) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("03BD6F3ACE4C48BD8660411FC8673DB4");
        Map values = new HashMap();
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.deactivationReason", userAccount.getConfirmationCode());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(messageTemplate.getTitle());
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            throw new RuntimeException("Error when sending the deactivation reason to user "+ userAccount.getUsername(),me);
        }
    }

    private String encryptPassword(String string) {
        MessageDigest md = null;
        byte stringBytes[] = null;
        try {
            md = MessageDigest.getInstance("MD5");
            stringBytes = string.getBytes("UTF8");
        }
        catch(NoSuchAlgorithmException nsae) {
            throw new SecurityException("The Requested encoding algorithm was not found in this execution platform.", nsae);
        }
        catch(UnsupportedEncodingException uee) {
            throw new SecurityException("UTF8 is not supported in this execution platform.", uee);
        }
         
        byte stringCriptBytes[] = md.digest(stringBytes);
        char[] encoded = Base64Encoder.encode(stringCriptBytes);
        return String.valueOf(encoded);
    }

    private String generateConfirmationCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "");
    }

    public void requestConfirmationPasswordChange(String username, String serverAddress) {
        UserAccount userAccount = findUserAccountByUsername(username);
        if(userAccount != null) {
            userAccount.setConfirmationCode(generateConfirmationCode());
            sendConfirmationCode(userAccount, serverAddress);
        }
        else
            throw new PersistenceException("Usuário inexistente.");
    }

    private void sendConfirmationCode(UserAccount userAccount, String serverAddress) {
        MessageTemplate messageTemplate = messageTemplateBsn.findMessageTemplate("67BE6BEBE45945D29109A8D6CD878344");
        Map values = new HashMap();
        values.put("serverAddress", serverAddress);
        values.put("userAccount.firstName", userAccount.getFirstName());
        values.put("userAccount.confirmationCode", userAccount.getConfirmationCode());
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(messageTemplate.getTitle());
        emailMessage.setRecipientTo(userAccount);
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);
        emailMessage.setHeader(serverAddress);
        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.",me);
        }
    }

    public Boolean passwordMatches(UserAccount userAccount, String passwordToCheck) {
        if(userAccount.getPassword().equals(encryptPassword(passwordToCheck)))
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    public void changePassword(UserAccount userAccount) {
        userAccount.setPassword(encryptPassword(userAccount.getPassword()));
        userAccount.setConfirmationCode("");
        save(userAccount);
    }

    public void scheduleAccountMaintenance() {
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_YEAR, 1);
        today.set(Calendar.HOUR_OF_DAY, 6);
        today.set(Calendar.MINUTE, 0);
        long oneDay = 1000 * 60 * 60 * 24 * 1;

        Collection<Timer> timers = timer.getTimers();
        if(timers.isEmpty())
            timer.createTimer(today.getTime(), oneDay, "");
    }

    public List<Date> getScheduledAccountMaintenances() {
        Collection<Timer> timers = timer.getTimers();
        List<Date> schedules = new ArrayList<Date>();
        for(Timer tmr: timers) {
            schedules.add(tmr.getNextTimeout());
        }
        return schedules;
    }

    @Timeout
    public void removeNonConfirmedAccounts(Timer timer) {
        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.DAY_OF_YEAR, -2);

        em.createQuery("delete from UserAccount ua where ua.registrationDate <= :twoDaysAgo and ua.confirmationCode != ''")
                 .setParameter("twoDaysAgo", twoDaysAgo)
                 .executeUpdate();
    }

    public void remove(String userId) {
        UserAccount userAccount = em.find(UserAccount.class, userId);
        em.remove(userAccount);
    }
}