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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.entity.*;
import org.cejug.exception.BusinessLogicException;
import org.cejug.knowledge.business.SubscriptionBsn;
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
    
    @EJB
    private SubscriptionBsn subscriptionBsn;
    
    @PersistenceContext
    private EntityManager em;

    static final Logger logger = Logger.getLogger("org.cejug.business.UserAccountBsn");

    /**
     * Checks whether an user account exists.
     * @param username the username that unically identify users.
     * @return true if the account already exists.
     */
    public boolean existingAccount(String username) {
        if(username == null || username.isEmpty())
            throw new BusinessLogicException("It is not possible to check if the account exists because the username was not informed.");
        
        UserAccount existing = findUserAccountByUsername(username);
        return existing != null;
    }

    /**
     * @return true if there is no account registered in the database.
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
    
    /**
     * Check if the username has authentication data related to it. If there is
     * no authentication data, then the user is considered as non-existing, even
     * if an user account exists.
     */
    public UserAccount findUserAccountByUsername(String username) {
        try {
            return (UserAccount) em.createQuery("select a.userAccount from Authentication a where a.username = :username")
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

    /**
     * @return the list of deactivated user accounts that were deactivated by
     * their own will or administratively.
     */
    public List<UserAccount> findDeactivatedUserAccounts() {
        return em.createQuery("select ua from UserAccount ua where ua.deactivated = :deactivated and ua.deactivationType <> :type order by ua.deactivationDate desc")
                 .setParameter("deactivated", Boolean.TRUE)
                 .setParameter("type", DeactivationType.UNREGISTERED)
                 .getResultList();
    }
    
    /**
     * Find a user account that was previously deactivated or not activated yet.
     * @param email The email address of the user.
     * @return the user account of an unregistered user.
     */
    public UserAccount findDeactivatedUserAccount(String email) {
        try {
            return (UserAccount) em.createQuery("select ua from UserAccount ua where ua.email = :email and ua.deactivationType is not null")
                                   .setParameter("email", email)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
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
    
    /**
     * @param userAccount the user who has authentication credentials registered.
     * @return the user's authentication data.
     */
    public Authentication findAuthenticationUser(UserAccount userAccount) {
        try {
            return (Authentication) em.createQuery("select a from Authentication a where a.userAccount = :userAccount")
                                   .setParameter("userAccount", userAccount)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
    }
    
    /**
     * @param userAccount the id of the user who has authentication credentials registered.
     * @return the user's authentication data.
     */
    public Authentication findAuthenticationUser(String userAccount) {
        try {
            return (Authentication) em.createQuery("select a from Authentication a where a.userAccount.id = :userAccount")
                                   .setParameter("userAccount", userAccount)
                                   .getSingleResult();
        }
        catch(NoResultException nre) {
            return null;
        }
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
    public void register(UserAccount newUserAccount, Authentication authentication, City newCity) {
                
        // true if there is no account registered so far.
        boolean noAccount = noAccount();
                
        /* In case there is at least one account, it checks if the current 
         * registration has a corresponding account that was deactivated before.
         * If there is then the current registration updates the existing
         * account. Otherwise, a new account is created. */
        UserAccount userAccount = null;
        boolean existingAccount = false;
        if(!noAccount) {
            userAccount = findDeactivatedUserAccount(newUserAccount.getUnverifiedEmail());
            if(userAccount != null) {
                existingAccount = true;
                userAccount.setUnverifiedEmail(newUserAccount.getUnverifiedEmail());
                userAccount.setFirstName(newUserAccount.getFirstName());
                userAccount.setLastName(newUserAccount.getLastName());
                userAccount.setGender(newUserAccount.getGender());
                userAccount.setBirthDate(newUserAccount.getBirthDate());
                userAccount.setWebsite(newUserAccount.getWebsite());
                userAccount.setTwitter(newUserAccount.getTwitter());
                userAccount.setPostalCode(newUserAccount.getPostalCode());
                userAccount.setMailingList(newUserAccount.getMailingList());
                userAccount.setPublicProfile(newUserAccount.getPublicProfile());
                userAccount.setEvent(newUserAccount.getEvent());
                userAccount.setNews(newUserAccount.getNews());
                userAccount.setGeneralOffer(newUserAccount.getGeneralOffer());
                userAccount.setJobOffer(newUserAccount.getJobOffer());
                userAccount.setSponsor(newUserAccount.getSponsor());
                userAccount.setSpeaker(newUserAccount.getSpeaker());
                userAccount.setDeactivated(false);
                userAccount.setDeactivationDate(null);
                userAccount.setDeactivationReason(null);
                userAccount.setDeactivationType(null);
                userAccount.setVerified(false);
                
                Country country = newUserAccount.getCountry();
                country = em.merge(country);
                userAccount.setCountry(country);
                
                Province province = newUserAccount.getProvince();
                if(province != null) {
                    province = em.merge(province);
                }
                userAccount.setProvince(province);
                
                City city = newUserAccount.getCity();
                if(city != null) {
                    city = em.merge(city);
                }
                userAccount.setCity(city);
            }
        }
        
        if(userAccount == null)
            userAccount = newUserAccount;
        
        ApplicationProperty timeZone = applicationPropertyBsn.findApplicationProperty(Properties.TIMEZONE);
        
        // A potential new city was informed.
        if(newCity != null) {
            // Check if the informed city already exists.
            City existingCity = locationBsn.findCityByName(newCity.getName());
                        
            // If the city exists it simply set the property of the user account.
            if(existingCity != null) {
                userAccount.setCity(existingCity);
                if(existingCity.getTimeZone() != null)
                    userAccount.setTimeZone(existingCity.getTimeZone());
                else
                    userAccount.setTimeZone(timeZone.getPropertyValue());
            }
            else { // If the city does not exist it is created and used to set the property of the user account.
                newCity.setTimeZone(timeZone.getPropertyValue());
                newCity.setCountry(userAccount.getCountry());
                newCity.setProvince(userAccount.getProvince());
                locationBsn.saveCity(newCity);
                
                userAccount.setCity(newCity);
                userAccount.setTimeZone(newCity.getTimeZone());
            }
        }
        /* If no new city was informed, it just takes the selected one to set
         * timezone of the user account. */
        else {
            if(userAccount.getCity().getTimeZone() != null)
                userAccount.setTimeZone(userAccount.getCity().getTimeZone());
            else
                userAccount.setTimeZone(timeZone.getPropertyValue());
        }
        
        userAccount.defineNewConfirmationCode();
        userAccount.setRegistrationDate(Calendar.getInstance().getTime());
        
        if(!existingAccount) {
            userAccount.setId(EntitySupport.generateEntityId());
            em.persist(userAccount);
        }
        
        authentication.setUserAccount(userAccount);
        em.persist(authentication);

        // In case there is no account, the user is added to the administrative group.
        if(noAccount) {
            userAccount.resetConfirmationCode();            
            AccessGroup adminGroup = accessGroupBsn.findAdministrativeGroup();
            UserGroup userGroup = new UserGroup(adminGroup, authentication);
            userGroupBsn.add(userGroup);
        }
        else {
            ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);
            if(appProp.sendEmailsEnabled()) {
                ApplicationProperty url = applicationPropertyBsn.findApplicationProperty(Properties.URL);
                messengerBsn.sendEmailConfirmationRequest(userAccount, url.getPropertyValue());
            }
        }
    }

    /**
     * Finds the user account using the confirmation code, adds this user 
     * account in the default group, sends a welcome message to the user and a 
     * notification message to the leaders. The user has access to the 
     * application when he/she is added to the default group.
     * @return The confirmed user account.
     * */
    public UserAccount confirmUser(String confirmationCode) {
    	if(confirmationCode == null || confirmationCode.isEmpty())
            return null;
    	
        try {
            UserAccount userAccount = (UserAccount)em.createQuery("select ua from UserAccount ua where ua.confirmationCode = :code")
                                                     .setParameter("code", confirmationCode)
                                                     .getSingleResult();
            if(userAccount != null) {
            	userAccount.resetConfirmationCode();
                userAccount.setEmail(userAccount.getUnverifiedEmail());
                userAccount.setUnverifiedEmail(null);
            	userAccount.setRegistrationDate(Calendar.getInstance().getTime());
            
                // This step effectively allows the user to access the application.
                AccessGroup defaultGroup = accessGroupBsn.findUserDefaultGroup();
                Authentication authentication = findAuthenticationUser(userAccount);
                UserGroup userGroup = new UserGroup(defaultGroup, authentication);
                userGroupBsn.add(userGroup);

                ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);
                if(appProp.sendEmailsEnabled()) {
                    messengerBsn.sendWelcomeMessage(userAccount);

                    AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
                    List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
                    messengerBsn.sendNewMemberAlertMessage(userAccount, leaders);
                }
            }
            
            return userAccount;
        }
        catch(NoResultException nre) {
            return null;
        }
    }
    
    public void save(UserAccount userAccount) {
        userAccount.setLastUpdate(Calendar.getInstance().getTime());
        em.merge(userAccount);
    }

    public void deactivateMembership(UserAccount userAccount, DeactivationType deactivationType) {
        UserAccount existingUserAccount = findUserAccount(userAccount.getId());

        existingUserAccount.setDeactivated(Boolean.TRUE);
        existingUserAccount.setDeactivationDate(Calendar.getInstance().getTime());
        existingUserAccount.setDeactivationReason(userAccount.getDeactivationReason());
        existingUserAccount.setDeactivationType(deactivationType);

        save(existingUserAccount);
        
        userGroupBsn.removeUserFromAllGroups(existingUserAccount);
        
        removeUserAuthentication(existingUserAccount);

        ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);

        if(!existingUserAccount.getDeactivationReason().trim().isEmpty() && appProp.sendEmailsEnabled()) {
            messengerBsn.sendDeactivationReason(existingUserAccount);
        }
        
        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);

        if(appProp.sendEmailsEnabled())
            messengerBsn.sendDeactivationAlertMessage(existingUserAccount, leaders);
    }
    
    public void removeUserAuthentication(UserAccount userAccount) {
        em.createQuery("delete from Authentication a where a.userAccount = :userAccount")
                .setParameter("userAccount", userAccount)
                .executeUpdate();
    }

    public void requestConfirmationPasswordChange(String username, String serverAddress) {
        UserAccount userAccount = findUserAccountByUsername(username);

        ApplicationProperty appProp = applicationPropertyBsn.findApplicationProperty(Properties.SEND_EMAILS);

        if(userAccount != null) {
            userAccount.defineNewConfirmationCode();
            if(appProp.sendEmailsEnabled())
                messengerBsn.sendConfirmationCode(userAccount, serverAddress);
        }
        else
            throw new PersistenceException("Usuário inexistente:"+ username);
    }

    /**
     * Compares the informed password with the one stored in the database.
     * @param userAccount the user account that has authentication credentials.
     * @param passwordToCheck the password to be compared with the one in the database.
     * @return true if the password matches.
     */
    public Boolean passwordMatches(UserAccount userAccount, String passwordToCheck) {
        try {
            Authentication authentication = (Authentication) em.createQuery("select a from Authentication a where a.userAccount = :userAccount and a.password = :password")
                                            .setParameter("userAccount", userAccount)
                                            .setParameter("password", (new Authentication()).hashPassword(passwordToCheck))
                                            .getSingleResult();
            if(authentication != null)
                return Boolean.TRUE;
        }
        catch(NoResultException nre) {
            return Boolean.FALSE;
        }
        
        return Boolean.FALSE;
    }

    /**
     * @param userAccount account of the user who wants to change his password.
     * @param newPassword the new password of the user.
     */
    public void changePassword(UserAccount userAccount, String newPassword) {
        try {
            // Retrieve the user authentication where the password is saved.
            Authentication authentication = (Authentication) em.createQuery("select a from Authentication a where a.userAccount = :userAccount")
                                            .setParameter("userAccount", userAccount)
                                            .getSingleResult();
            if(authentication != null) {
                authentication.setPassword(newPassword);
                userAccount.resetConfirmationCode();
                save(userAccount);
            }
        }
        catch(NoResultException nre) {
            throw new BusinessLogicException("User account not found. It is not possible to change the password.");
        }
    }
    
    /**
     * Changes the email address of the user without having to repeat the
     * registration process.
     * @param userAccount the user account that intends to change its email address.
     * @param newEmail the new email address of the user account.
     * @exception BusinessLogicException in case the newEmail is already registered.
     */
    public void changeEmail(UserAccount userAccount, String newEmail) {
        // Check if the new email already exists in the UserAccounts
        UserAccount existingUserAccount = findUserAccountByEmail(newEmail);

        if(existingUserAccount != null)
            throw new BusinessLogicException("errorCode0001");

        // Change the email address in the UserAccount
        userAccount.setUnverifiedEmail(newEmail);
        em.merge(userAccount);

        // Since the email address is also the username, change the username in the Authentication and in the UserGroup
        userGroupBsn.changeUsername(userAccount, newEmail);

        // In the MailingListSubscription, we close the subscription of the previous email address and subscribe the new one, linked to the same user account.
        subscriptionBsn.changeEmailAddress(userAccount);
        
        // Send an email to the user to confirm the new email address
        ApplicationProperty url = applicationPropertyBsn.findApplicationProperty(Properties.URL);    
        messengerBsn.sendEmailVerificationRequest(userAccount, url.getPropertyValue());
    }
    
    
    public void confirmEmailChange(UserAccount userAccount) {
        if(userAccount.getUnverifiedEmail() == null)
            throw new BusinessLogicException("errorCode0002");
        
        userAccount.resetConfirmationCode();
        userAccount.setEmail(userAccount.getUnverifiedEmail());
        userAccount.setUnverifiedEmail(null);
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
    
    /**
     * Update the time zone of all users that inhabit the informed city.
     */
    public void updateTimeZoneInhabitants(City city) {
        if(city.getTimeZone() != null && !city.getTimeZone().isEmpty()) {
            List<UserAccount> userAccounts = findInhabitantsFrom(city);
            for(UserAccount user: userAccounts) {
                user.setTimeZone(city.getTimeZone());
            }
        }
    }

    public void remove(String userId) {
        UserAccount userAccount = em.find(UserAccount.class, userId);
        em.remove(userAccount);
    }
}