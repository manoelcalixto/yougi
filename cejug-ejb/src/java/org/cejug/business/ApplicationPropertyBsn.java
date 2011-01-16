package org.cejug.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.Properties;

@Stateless
@LocalBean
public class ApplicationPropertyBsn {

    @PersistenceContext
    EntityManager em;

    public Map<String, String> findApplicationProperties() {
        Properties[] props = Properties.values();
        Map propertiesMap = new HashMap<String, String>();
        for(Properties prop: props) {
            propertiesMap.put(prop.getName(), "");
        }

        List<ApplicationProperty> properties = em.createQuery("select ap from ApplicationProperty ap").getResultList();
        for(ApplicationProperty property: properties) {
            propertiesMap.put(property.getPropertyName(), property.getPropertyValue());
        }

        return propertiesMap;
    }

    public void save(Map<String, String> properties) {
        List<ApplicationProperty> existingProperties = em.createQuery("select ap from ApplicationProperty ap").getResultList();
        for(ApplicationProperty property: existingProperties) {
            property.setPropertyValue(properties.get(property.getPropertyName()));
            properties.remove(property.getPropertyName());
            em.merge(property);
        }

        if(!properties.isEmpty()) {
            Set entryProperties = properties.entrySet();
            Iterator iEntryProperties = entryProperties.iterator();
            Map.Entry entry;
            ApplicationProperty property;
            while(iEntryProperties.hasNext()) {
                entry = (Map.Entry)iEntryProperties.next();
                property = new ApplicationProperty((String)entry.getKey(), (String)entry.getValue());
                em.persist(property);
            }
        }
    }
}