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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.entity.*;
import org.cejug.util.Base64Encoder;
import org.cejug.util.EntitySupport;

/**
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class UserAccountBsn {

    @EJB
    private AccessGroupBsn accessGroupBsn;

    @EJB
    private UserGroupBsn userGroupBsn;

    @EJB
    private LocationBsn locationBsn;

    @EJB
    private MessengerBsn messengerBsn;

    @EJB
    private ApplicationPropertyBsn applicationPropertyBsn;
    
    @PersistenceContext
    private EntityManager em;

    static final Logger logger = Logger.getLogger("org.cejug.business.UserAccountBsn");

    /** This method checks whether an user account exists in the database. */
    public boolean existingAccount(UserAccount userAccount) {
        UserAccount existing = findUserAccountByUsername(userAccount.getUsername());
        return existing != null;
    }

    /**
     * This method returns true if there is no account registered in the 
     * database.
     * */
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

    /**
     * Returns all activated user accounts ordered by name.
     */
    @SuppressWarnings("unchecked")
    public List<UserAccount> findUserAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated and ua.confirmationCode is null order by ua.firstName")
                 .setParameter("deactivated", Boolean.FALSE)
                 .getResultList();
    }

    /**
     * Returns user accounts ordered by registration date and in which the
     * registration date is between the informed period of time.
     */
    @SuppressWarnings("unchecked")
    public List<UserAccount> findConfirmedUserAccounts(Date from, Date to) {
        return em.createQuery("select ua from UserAccount ua where ua.confirmationCode is null and ua.registrationDate >= :from and ua.registrationDate <= :to order by ua.registrationDate asc")
                 .setParameter("from", from)
                 .setParameter("to", to)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserAccount> findNotVerifiedUsers() {
        return em.createQuery("select ua from UserAccount ua where ua.verified = :verified and ua.deactivated = :deactivated order by ua.registrationDate desc")
                 .setParameter("verified", Boolean.FALSE)
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

    /** 
     * Returns all users related to the informed city, independent of their 
     * confirmation, validation or deactivation status.
     */
    @SuppressWarnings("unchecked")
    public List<UserAccount> findInhabitantsFrom(City city) {
        return em.createQuery("select u from UserAccount u where u.city = :city order by u.firstName")
                .setParameter("city", city)
                .getResultList();
    }

    /** <p>Register new user accounts. For the moment, this is the only way an
     * user account can be created. In the moment of the registration, data,
     * such as country, city, website, etc., are saved as a contact record. This
     * contact record is related to the new user and it is set as his/her main
     * contact. In case the user inform a city that does not exist, then his/her
     * suggestion is registered, but checked as not valid. The server address is
     * informed just because it can only be detected automatically on the web
     * container.</p>
     * <p>When there is no user, the first registration creates a super user
     * with administrative rights.</p> */
    public void register(UserAccount userAccount, City newCity, String serverAddress) {
        boolean noAccount = noAccount();

        if(newCity != null) {
            newCity.setId(EntitySupport.generateEntityId());
            newCity.setValid(false);
            locationBsn.saveCity(newCity);
            userAccount.setCity(newCity);
        }

        userAccount.setConfirmationCode(generateConfirmationCode());
        userAccount.setRegistrationDate(Calendar.getInstance().getTime());
        userAccount.setPassword(encryptPassword(userAccount.getPassword()));
        userAccount.setId(EntitySupport.generateEntityId());
        em.persist(userAccount);

        if(noAccount) {
            userAccount.setConfirmationCode(null);            
            AccessGroup adminGroup = accessGroupBsn.findAdministrativeGroup();
            UserGroup userGroup = new UserGroup(adminGroup, userAccount);
            userGroupBsn.add(userGroup);
        }
        else {
            ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);
            if(appProp.sendEmailsEnabled())
                messengerBsn.sendEmailConfirmationRequest(userAccount, serverAddress);
        }
    }

    /**
     * This method finds the user account using the confirmation code, adds 
     * this user account in the default group and sends a welcome message to 
     * the user and a notification message to the leaders. The user has access 
     * to the application when he/she is added to the default group.
     * */
    public void confirmUser(String confirmationCode) {
    	if(confirmationCode == null || confirmationCode.isEmpty())
    		return;
    	
        try {
            UserAccount userAccount = (UserAccount)em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :code")
                                                     .setParameter("code", confirmationCode)
                                                     .getSingleResult();
            if(userAccount != null) {
            	userAccount.setConfirmationCode(null);
            	userAccount.setRegistrationDate(Calendar.getInstance().getTime());
            
	            // This step effectively allows the user to access the application.
	            AccessGroup defaultGroup = accessGroupBsn.findUserDefaultGroup();
	            
	            UserGroup userGroup = new UserGroup(defaultGroup, userAccount);
	            userGroupBsn.add(userGroup);
	
	            ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);
	            if(appProp.sendEmailsEnabled()) {
	                messengerBsn.sendWelcomeMessage(userAccount);
	
	                AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
	                List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
	                messengerBsn.sendNewMemberAlertMessage(userAccount, leaders);
	            }
            }
        }
        catch(NoResultException nre) {
            throw new IllegalArgumentException("Confirmation code "+ confirmationCode +" does not match any existing pendent account.", nre.getCause());
        }
    }
    
    public void save(UserAccount userAccount) {
        userAccount.setLastUpdate(Calendar.getInstance().getTime());
        em.merge(userAccount);
    }

    public void deactivateMembership(UserAccount userAccount) {
        UserAccount existingUserAccount = findUserAccount(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());
        existingUserAccount.setDeactivationType(DeactivationType.ADMINISTRATIVE);

        save(existingUserAccount);
        userGroupBsn.removeUserFromAllGroups(userAccount);

        ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);

        if(!existingUserAccount.getDeactivationReason().trim().isEmpty() && appProp.sendEmailsEnabled()) {
            messengerBsn.sendDeactivationReason(existingUserAccount);
        }
        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);

        if(appProp.sendEmailsEnabled())
            messengerBsn.sendDeactivationAlertMessage(existingUserAccount, leaders);
    }

    public void deactivateOwnMembership(UserAccount userAccount) {
        UserAccount existingUserAccount = findUserAccount(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());
        existingUserAccount.setDeactivationType(DeactivationType.OWNWILL);

        ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);

        save(existingUserAccount);
        userGroupBsn.removeUserFromAllGroups(userAccount);
        if(!existingUserAccount.getDeactivationReason().trim().isEmpty() && appProp.sendEmailsEnabled()) {
            messengerBsn.sendDeactivationReason(existingUserAccount);
        }
        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);

        if(appProp.sendEmailsEnabled())
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

        ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);

        if(userAccount != null) {
            userAccount.setConfirmationCode(generateConfirmationCode());
            if(appProp.sendEmailsEnabled())
                messengerBsn.sendConfirmationCode(userAccount, serverAddress);
        }
        else
            throw new PersistenceException("Usuário inexistente:"+ username);
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

    @Schedules({ @Schedule(hour="*/12") })
    public void removeNonConfirmedAccounts(Timer timer) {
        logger.log(Level.INFO, "Timer to remove non confirmed accounts started.");

        Calendar twoDaysAgo = Calendar.getInstance();
        twoDaysAgo.add(Calendar.DAY_OF_YEAR, -2);

        Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        logger.log(Level.INFO, "Non confirmed accounts older than {0} will be removed.", formatter.format(twoDaysAgo.getTime()));

        int i = em.createQuery("delete from UserAccount ua where ua.registrationDate <= :twoDaysAgo and ua.confirmationCode is not null")
                  .setParameter("twoDaysAgo", twoDaysAgo.getTime())
                  .executeUpdate();

        logger.log(Level.INFO, "Number of removed non confirmed accounts: {0}", i);
    }

    public void remove(String userId) {
        UserAccount userAccount = em.find(UserAccount.class, userId);
        em.remove(userAccount);
    }
}