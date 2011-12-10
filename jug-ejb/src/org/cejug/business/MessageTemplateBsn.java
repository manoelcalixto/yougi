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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    @SuppressWarnings("unchecked")
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

    public void applyEmailMessageTemplate(EmailMessage emailMessage, MessageTemplate template, Map<String, Object> values) {
        Pattern pattern = Pattern.compile(VAR_PATTERN);
        List<String> variables = findVariables(pattern, template.getBody());
        String body = template.getBody();
        Object value;
        for(String variable: variables) {
            variable = variable.substring(2, variable.length() - 1);
            value = values.get(variable);
            if(value != null)
                body = body.replace("#{" + variable + "}", values.get(variable).toString());
        }
        emailMessage.setSubject(template.getTitle());
        emailMessage.setBody(body);
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