package org.cejug.partnership.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cejug.partnership.entity.Partner;

/**
 * Manages partners of the user group.
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class PartnerBsn {
	
    @PersistenceContext
    private EntityManager em;

    public Partner findPartner(String id) {
        if(id != null)
            return em.find(Partner.class, id);
        else
            return null;
    }
    
    public List<Partner> findPartners() {
        return em.createQuery("select p from Partner p order by p.name asc")
                 .getResultList();
    }

    public void save(Partner partner) {
        Partner existing = em.find(Partner.class, partner.getId());
        if(existing == null)
            em.persist(partner);
        else
            em.merge(partner);
    }

    public void remove(String id) {
        Partner partner = em.find(Partner.class, id);
        if(partner != null)
            em.remove(partner);
    }
}