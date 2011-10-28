package org.cejug.partnership.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cejug.entity.Country;
import org.cejug.partnership.entity.Partner;
import org.cejug.partnership.entity.Representative;

/**
 * Manages partners of the user group.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class RepresentativeBsn {
	
    @PersistenceContext
    private EntityManager em;

    public Representative findRepresentative(String id) {
        if(id != null)
            return em.find(Representative.class, id);
        else
            return null;
    }
    
    public void save(Representative representative) {
        Representative existing = em.find(Representative.class, representative.getId());
        if(existing == null)
            em.persist(representative);
        else
            em.merge(representative);
    }

    public void remove(String id) {
        Representative representative = em.find(Representative.class, id);
        if(representative != null)
            em.remove(representative);
    }
}