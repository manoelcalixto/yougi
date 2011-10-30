package org.cejug.partnership.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.cejug.partnership.entity.Partner;
import org.cejug.util.EntitySupport;

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
    
    @SuppressWarnings("unchecked")
	public List<Partner> findPartners() {
        return em.createQuery("select p from Partner p order by p.name asc")
                 .getResultList();
    }

    public void save(Partner partner) {
    	if(partner.getId() == null || partner.getId().isEmpty()) {
            partner.setId(EntitySupport.generateEntityId());
            em.persist(partner);
        }
        else {
            em.merge(partner);
        }
    }

    public void remove(String id) {
        Partner partner = em.find(Partner.class, id);
        if(partner != null)
            em.remove(partner);
    }
}