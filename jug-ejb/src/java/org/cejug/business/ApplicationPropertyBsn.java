package org.cejug.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.cejug.entity.ApplicationProperty;
import org.cejug.entity.Properties;

@Stateless
@LocalBean
public class ApplicationPropertyBsn {

    @PersistenceContext
    EntityManager em;

    public Map<String, String> findApplicationProperties() {
        Map<String, String> propertiesMap = new HashMap<String, String>();
        List<ApplicationProperty> properties = em.createQuery("select ap from ApplicationProperty ap").getResultList();
        for(ApplicationProperty property: properties) {
            propertiesMap.put(property.getPropertyKey(), property.getPropertyValue());
        }

        // If there is no property in the database, it creates all properties according to the enumeration Properties.
        if(propertiesMap.isEmpty()) {
            Properties[] props = Properties.values();
            for(int i = 0;i < props.length;i++) {
                propertiesMap.put(props[i].getKey(), props[i].getDefaultValue());
            }
            create(propertiesMap);
        }
        // If there is more properties in the enumeration than in the database, then additional enumerations are persisted.
        else if(Properties.values().length > propertiesMap.size()) {
            Properties[] props = Properties.values();
            for(int i = 0;i < props.length;i++) {
                if(!propertiesMap.containsKey(props[i].getKey())) {
                    propertiesMap.put(props[i].getKey(), props[i].getDefaultValue());
                    create(props[i].getKey(), props[i].getDefaultValue());
                }
            }
        }
        // If there is more persisted properties than in the enumeration, then exceding properties are removed.
        else if(Properties.values().length < propertiesMap.size()) {
            Set<Map.Entry<String, String>> propEntries = propertiesMap.entrySet();
            Iterator iProps = propEntries.iterator();
            Map.Entry<String, String> entry;
            Properties[] props = Properties.values();
            while(iProps.hasNext()) {
                entry = (Map.Entry)iProps.next();
                for(int i = 0; i < props.length; i++) {
                    if(!entry.getKey().equals(props[i].getKey()))
                        remove(entry.getKey());
                }
            }
        }

        return propertiesMap;
    }

    public ApplicationProperty findApplicationProperty(Properties properties) {
        ApplicationProperty applicationProperty = null;
        try {
            applicationProperty = (ApplicationProperty)em.createQuery("select ap from ApplicationProperty ap where ap.propertyKey = :key")
                                                                         .setParameter("key", properties.getKey())
                                                                         .getSingleResult();
        }
        catch(NoResultException nre) {
            Map applicationProperties = findApplicationProperties();
            String key = properties.getKey();
            applicationProperty = new ApplicationProperty(key, (String)applicationProperties.get(key));
        }
        return applicationProperty;
    }

    public void save(Map<String, String> properties) {
        List<ApplicationProperty> existingProperties = em.createQuery("select ap from ApplicationProperty ap").getResultList();
        for(ApplicationProperty property: existingProperties) {
            property.setPropertyValue(properties.get(property.getPropertyKey()));
            em.merge(property);
        }
    }

    private void create(Map<String, String> properties) {
        Set<Map.Entry<String, String>> props = properties.entrySet();
        Iterator iProps = props.iterator();
        ApplicationProperty appProp;
        Map.Entry<String, String> entry;
        while(iProps.hasNext()) {
            entry = (Map.Entry)iProps.next();
            appProp = new ApplicationProperty(entry.getKey(), entry.getValue());
            em.persist(appProp);
        }
    }
    
    private void create(String key, String value) {
        ApplicationProperty appProp = new ApplicationProperty(key, value);
        em.persist(appProp);
    }

    private void remove(String key) {
        ApplicationProperty applicationProperty = em.find(ApplicationProperty.class, key);
        em.remove(applicationProperty);
    }
}