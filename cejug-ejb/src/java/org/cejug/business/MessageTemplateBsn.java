package org.cejug.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.EmailMessage;
import org.cejug.entity.MessageTemplate;
import org.cejug.util.EntitySupport;

/**
 * Business logic related to MessageTemplate entity class.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class MessageTemplateBsn {
    
    @PersistenceContext
    private EntityManager em;

    private static final String VAR_PATTERN = "\\#\\{([a-z][a-zA-Z_0-9]*(\\.)?)+\\}";

    public MessageTemplate findMessageTemplate(String id) {
        return em.find(MessageTemplate.class, id);
    }

    public List<MessageTemplate> findMessageTemplates() {
        return em.createQuery("select mt from MessageTemplate mt order by mt.title").getResultList();
    }

    public void save(MessageTemplate messageTemplate) {
        if(messageTemplate.getId() == null || messageTemplate.getId().isEmpty()) {
            messageTemplate.setId(EntitySupport.generateEntityId());
            em.persist(messageTemplate);
        }
        else {
            em.merge(messageTemplate);
        }
    }

    public void remove(String userId) {
        MessageTemplate messageTemplate = em.find(MessageTemplate.class, userId);
        if(messageTemplate != null)
            em.remove(messageTemplate);
    }

    public void applyEmailMessageTemplate(EmailMessage emailMessage, MessageTemplate template, Object[] values) {
        Pattern pattern = Pattern.compile(VAR_PATTERN);
        List<String> variables = findVariables(pattern, template.getBody());

        String variable;
        String className;
        String value = null;
        int point;
        String body = template.getBody();
        for (int i = 0; i < variables.size(); i++) {
            variable = variables.get(i);
            variable = variable.substring(2, variable.length() - 1);
            for (int j = 0; j < values.length; j++) {
                className = values[j].getClass().getSimpleName();
                className = className.substring(0, 1).toLowerCase() + className.substring(1);
                point = variable.indexOf(".");
                if (point > 0) {
                    if (className.equals(variable.substring(0, point))) {
                        value = getValue(values[j], variable.substring(point + 1));
                        break;
                    }
                } else {
                    if (className.equals(variable)) {
                        value = getValue(values[j], null);
                        break;
                    }
                }
            }
            if (value != null) {
                body = body.replace("#{" + variable + "}", value);
            }
        }
        emailMessage.setBody(body);
    }

    private String getValue(Object object, String var) {
        if (var != null) {
            int point = var.indexOf(".");
            try {
                if (point > 0) {
                    String methodName = "get" + var.substring(0, 1).toUpperCase() + var.substring(1, point);
                    Method method = object.getClass().getMethod(methodName, (Class[]) null);
                    Object obj = method.invoke(object, (Object[]) null);
                    return getValue(obj, var.substring(point + 1));
                } else {
                    String nomeMetodo = "get" + var.substring(0, 1).toUpperCase() + var.substring(1);
                    Method metodo = object.getClass().getMethod(nomeMetodo, (Class[]) null);
                    Object obj = metodo.invoke(object, (Object[]) null);
                    return getValue(obj, null);
                }
            } catch (Exception e) {
                return "";
            }
        } else {
            return object.toString();
        }
    }

    private List<String> findVariables(Pattern pattern, CharSequence charSequence) {
        Matcher m = pattern.matcher(charSequence);
        List<String> matches = new ArrayList<String>();
        while (m.find()) {
            matches.add(m.group());
        }
        return matches;
    }
}