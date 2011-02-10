package org.cejug.business;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.City;
import org.cejug.entity.Contact;
import org.cejug.entity.ContactType;
import org.cejug.entity.DeactivationType;
import org.cejug.entity.UserAccount;
import org.cejug.entity.UserGroup;
import org.cejug.util.Base64Encoder;
import org.cejug.util.EntitySupport;

@Stateless
@LocalBean
public class UserAccountBsn {

    @Resource
    private TimerService timer;

    @EJB
    private AccessGroupBsn accessGroupBsn;

    @EJB
    private UserGroupBsn userGroupBsn;

    @EJB
    private LocationBsn locationBsn;

    @EJB
    private MessengerBsn messengerBsn;
    
    @PersistenceContext
    private EntityManager em;

    static final Logger logger = Logger.getLogger("org.cejug.business.UserAccountBsn");

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

    public UserAccount findUserAccountByEmail(String email) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.email = :email")
                                   .setParameter("email", email)
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

    @SuppressWarnings("unchecked")
    public List<UserAccount> findUserAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated order by ua.firstName")
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserAccount> findUserAccountsOrderedByRegistration() {
        return em.createQuery("select ua from UserAccount ua where ua.confirmationCode is null and ua.deactivated = :deactivated order by ua.registrationDate")
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserAccount> findRegisteredUsersSince(Date date) {
        return em.createQuery("select ua from UserAccount ua where ua.registrationDate >= :date and ua.deactivated = :deactivated order by ua.registrationDate desc")
                 .setParameter("date", date)
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserAccount> findUserAccountsStartingWith(String firstLetter) {
        return em.createQuery("select ua from UserAccount ua where ua.firstName like '"+ firstLetter +"%' and ua.deactivated = :deactivated order by ua.firstName")
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserAccount> findDeactivatedUserAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated order by ua.deactivationDate desc")
                 .setParameter("deactivated", Boolean.TRUE)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
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
        
        messengerBsn.sendEmailConfirmationRequest(userAccount, serverAddress);
    }

    public void save(UserAccount userAccount) {
        userAccount.setLastUpdate(Calendar.getInstance().getTime());
        em.merge(userAccount);
    }

    public void updateProfilePicture(UserAccount userAccount, String profilePicturePath) {
        userAccount = em.find(UserAccount.class, userAccount.getId());
        userAccount.setPhoto(profilePicturePath);
    }

    public void confirmUser(String confirmationCode) {
        try {
            UserAccount userAccount = (UserAccount)em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :code")
                                                     .setParameter("code", confirmationCode)
                                                     .getSingleResult();
            userAccount.setConfirmationCode(null);
            userAccount.setRegistrationDate(Calendar.getInstance().getTime());
            userAccount.setPassword(encryptPassword(userAccount.getPassword()));
            messengerBsn.sendWelcomeMessage(userAccount);

            AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
            List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
            messengerBsn.sendNewMemberAlertMessage(userAccount, leaders);
        }
        catch(NoResultException nre) {
            throw new IllegalArgumentException("Confirmation code "+ confirmationCode +" does not match any existing pendent account.", nre.getCause());
        }
    }

    public void deactivateMembership(UserAccount userAccount) {
        UserAccount existingUserAccount = findUserAccount(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());
        existingUserAccount.setDeactivationType(DeactivationType.ADMINISTRATIVE);

        save(existingUserAccount);
        userGroupBsn.removeUserFromAllGroups(userAccount);
        if(!existingUserAccount.getDeactivationReason().trim().isEmpty()) {
            messengerBsn.sendDeactivationReason(existingUserAccount);
        }
        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
        messengerBsn.sendDeactivationAlertMessage(existingUserAccount, leaders);
    }

    public void deactivateOwnMembership(UserAccount userAccount) {
        UserAccount existingUserAccount = findUserAccount(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());
        existingUserAccount.setDeactivationType(DeactivationType.OWNWILL);

        save(existingUserAccount);
        userGroupBsn.removeUserFromAllGroups(userAccount);
        if(!existingUserAccount.getDeactivationReason().trim().isEmpty()) {
            messengerBsn.sendDeactivationReason(existingUserAccount);
        }
        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
        messengerBsn.sendDeactivationAlertMessage(existingUserAccount, leaders);
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
            messengerBsn.sendConfirmationCode(userAccount, serverAddress);
        }
        else
            throw new PersistenceException("Usuário inexistente.");
    }

    public Boolean passwordMatches(UserAccount userAccount, String passwordToCheck) {
        if(userAccount.getPassword().equals(encryptPassword(passwordToCheck)))
            return Boolean.TRUE;
        else
            return Boolean.FALSE;
    }

    public void changePassword(UserAccount userAccount) {
        userAccount.setPassword(encryptPassword(userAccount.getPassword()));
        userAccount.setConfirmationCode(null);
        save(userAccount);
    }

    public void scheduleAccountMaintenance() {
        long duration = 1000 * 60 * 60 * 24 * 1;

        timer.createTimer(duration, "");
    }

    public List<Date> getScheduledAccountMaintenances() {
        Collection<Timer> timers = timer.getTimers();
        List<Date> schedules = new ArrayList<Date>();
        for(Timer tmr: timers) {
            schedules.add(tmr.getNextTimeout());
        }
        return schedules;
    }

    @Schedules({ @Schedule(hour="*/12") })
    public void removeNonConfirmedAccounts(Timer timer) {
        logger.info("Timer to remove non confirmed accounts started.");

        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.DAY_OF_YEAR, -2);

        Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        logger.log(Level.INFO, "Non confirmed accounts older than {0} will be removed.", formatter.format(twoDaysAgo.getTime()));

        int i = em.createQuery("delete from UserAccount ua where ua.registrationDate <= :twoDaysAgo and ua.confirmationCode is not null")
                  .setParameter("twoDaysAgo", twoDaysAgo.getTime())
                  .executeUpdate();

        logger.log(Level.INFO, "Number of removed non confirmed accounts: {0}", i);
        
        scheduleAccountMaintenance();
    }

    public void remove(String userId) {
        UserAccount userAccount = em.find(UserAccount.class, userId);
        em.remove(userAccount);
    }
}