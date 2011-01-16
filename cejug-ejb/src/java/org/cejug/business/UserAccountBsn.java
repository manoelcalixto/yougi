package org.cejug.business;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.City;
import org.cejug.entity.Contact;
import org.cejug.entity.ContactType;
import org.cejug.entity.EmailMessage;
import org.cejug.entity.Message;
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
        MimeMessage msg = new MimeMessage(mailSession);

        try {
            msg.setSubject("[CEJUG] Confirmação de Email", "UTF-8");
            msg.setRecipient(RecipientType.TO, new InternetAddress(userAccount.getEmail(), userAccount.toString()));
            msg.setText("<p>Oi <b>"+ userAccount.getFirstName() +"</b>,</p>" +
                        "<p>você parece ter se registrado como membro no CEJUG.</p>" +
                        "<p>Nós gostariamos de confirmar o seu endereço de email para podermos entrar em contato sempre que necessário. " +
                        "Você só precisa clicar no link abaixo para confirmar o seu registro no CEJUG:</p>" +
                        "<p><a href=\"http://"+ serverAddress + "/EmailConfirmation?code="+ userAccount.getConfirmationCode() + "\">http://"+ serverAddress +"/EmailConfirmation?code="+ userAccount.getConfirmationCode() +"</a></p>" +
                        "<p>Se o endereço acima não aparecer como link no seu cliente de email, selecione, copie e cole o endereço no seu navegador web.</p>" +
                        "<p>Se você não se registrou no CEJUG e acredita se tratar de um engano, por favor ignore esta mensagem e aceite nossas desculpas.</p>" +
                        "<p>Atenciosamente,</p>" +
                        "<p><b>Coordenação do CEJUG</b></p>", "UTF-8");
            msg.setHeader("Content-Type", "text/html;charset=UTF-8");
            Transport.send(msg);
        }
        catch(MessagingException me) {
            throw new PersistenceException("Error when sending the mail confirmation. The registration was not finalized.",me);
        }
        catch(UnsupportedEncodingException uee) {
            throw new PersistenceException("Error when sending the mail confirmation. The registration was not finalized.", uee);
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
        MimeMessage msg = new MimeMessage(mailSession);

        try {
            msg.setSubject("[CEJUG] Bem vindo ao CEJUG", "UTF-8");
            msg.setRecipient(RecipientType.TO, new InternetAddress(userAccount.getEmail(), userAccount.toString()));
            msg.setText("<p>Oi <b>"+ userAccount.getFirstName() +"</b>,</p>" +
                        "<p>seu registro foi confirmado. Seja bem vindo ao <b><a href=\"http://www.cejug.org\">CEJUG</a></b>!</p>" +
                        "<p>Atenciosamente,</p>" +
                        "<p><b>Coordenação do CEJUG</b></p>", "UTF-8");
            msg.setHeader("Content-Type", "text/html;charset=UTF-8");
            Transport.send(msg);
        }
        catch(MessagingException me) {
            throw new PersistenceException("Error when sending welcome message.",me);
        }
        catch(UnsupportedEncodingException uee) {
            throw new PersistenceException("Error when sending welcome message.", uee);
        }
    }

    private void sendNewMemberAlertMessage(UserAccount newMember) {
        MimeMessage msg = new MimeMessage(mailSession);

        AccessGroup administrativeGroup = accessGroupBsn.findAdministrativeGroup();
        List<UserAccount> leaders = userGroupBsn.findUsersGroup(administrativeGroup);
        InternetAddress[] addresses = new InternetAddress[leaders.size()];
        try {
            for(int i = 0;i < addresses.length;i++){
                addresses[i] = new InternetAddress(leaders.get(i).getEmail(), leaders.get(i).getFullName(), "UTF-8");
            }
        }
        catch(UnsupportedEncodingException uee) {
            throw new PersistenceException("Error when sending welcome message.", uee);
        }

        try {
            msg.setSubject("[CEJUG Admin] Um novo membro cadastrou-se no grupo", "UTF-8");
            msg.setRecipients(RecipientType.TO, addresses);
            StringBuilder message = new StringBuilder();
            message.append("<p>Caro Coordenador do CEJUG,</p>");
            message.append("<p><b>");
            message.append(newMember.getFullName());
            message.append("</b> registrou-se como novo membro do CEJUG em ");
            message.append(newMember.getRegistrationDate());
            message.append(".</p>");
            if(newMember.getMailingList()) {
                message.append("<p>Verifique se o email ");
                message.append(newMember.getEmail());
                message.append(" está registrado na ");
                message.append("<a href=\"http://java.net/projects/cejug/lists/discussao/subscribers\">lista de discussão</a>.</p>");
            }
            message.append("<p>Atenciosamente,</p>");
            message.append("<p><b>Sistema de Administração do CEJUG</b></p>");
            msg.setText(message.toString(), "UTF-8");
            msg.setHeader("Content-Type", "text/html;charset=UTF-8");
            Transport.send(msg);
        }
        catch(MessagingException me) {
            throw new PersistenceException("Error when sending welcome message.",me);
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
        MimeMessage msg = new MimeMessage(mailSession);

        try {
            msg.setSubject("[CEJUG] Cancelamento de Registro", "UTF-8");
            msg.setRecipient(RecipientType.TO, new InternetAddress(userAccount.getEmail(), userAccount.toString()));
            msg.setText("<p>Caro(a) <b>"+ userAccount.getFirstName() +"</b>," +
                        "<p>sentimos muito em informar que não poderemos manter o seu registro como membro do CEJUG.</p>" +
                        "<p>Motivo: \"<i>"+ userAccount.getDeactivationReason() +"</i>\"</p>" +
                        "<p>Pedimos desculpas pelo transtorno e contamos com a vossa compreensão.</p>" +
                        "<p>Atenciosamente,</p>" +
                        "<p><b>Coordenação do CEJUG</b></p>", "UTF-8");
            msg.setHeader("Content-Type", "text/html;charset=UTF-8");
            Transport.send(msg);
        }
        catch(MessagingException me) {
            throw new PersistenceException("Error when sending the mail confirmation. The registration was not finalized.",me);
        }
        catch(UnsupportedEncodingException uee) {
            throw new PersistenceException("Error when sending the mail confirmation. The registration was not finalized.", uee);
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
        Object[] values = {userAccount, serverAddress};
        EmailMessage emailMessage = new EmailMessage();
        
        messageTemplateBsn.applyEmailMessageTemplate(emailMessage, messageTemplate, values);

        try {
            Transport.send(emailMessage.createMimeMessage(mailSession));
        }
        catch(MessagingException me) {
            throw new RuntimeException("Error when sending the mail confirmation. The registration was not finalized.",me);
        }
//        MimeMessage msg = new MimeMessage(mailSession);
//
//        try {
//            msg.setSubject("[CEJUG] Mudança de Senha", "UTF-8");
//            msg.setRecipient(RecipientType.TO, new InternetAddress(userAccount.getEmail(), userAccount.toString()));
//            msg.setText("<p>Oi <b>"+ userAccount.getFirstName() +"</b>," +
//                        "<p>você solicitou a mudança da sua senha do CEJUG.</p>" +
//                        "<p>O código de autorização para mudar sua senha é: </p>" +
//                        "<p>"+ userAccount.getConfirmationCode() +"</p>"+
//                        "<p>Informe este código no formulário de mudança de senha ou siga o endereço abaixo para preencher o campo automaticamente:</p>" +
//                        "<p><a href=\"http://"+ serverAddress + "/change_password.xhtml?cc=" + userAccount.getConfirmationCode() +"\">http://"+ serverAddress + "/change_password.xhtml?cc=" + userAccount.getConfirmationCode() +"</a></p>"+
//                        "<p>Atenciosamente,</p>" +
//                        "<p><b>Coordenação do CEJUG</b></p>", "UTF-8");
//            msg.setHeader("Content-Type", "text/html;charset=UTF-8");
//            Transport.send(msg);
//        }
//        catch(MessagingException me) {
//            throw new PersistenceException("Error when sending the mail confirmation. The registration was not finalized.",me);
//        }
//        catch(UnsupportedEncodingException uee) {
//            throw new PersistenceException("Error when sending the mail confirmation. The registration was not finalized.", uee);
//        }
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