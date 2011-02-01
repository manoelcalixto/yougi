package org.cejug.business;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.UserAccount;
import org.cejug.entity.UserGroup;

/**
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class UserGroupBsn {

    @PersistenceContext
    EntityManager em;

    @EJB
    AccessGroupBsn accessGroupBsn;

    @SuppressWarnings("unchecked")
	public List<UserAccount> findUsersGroup(AccessGroup accessGroup) {
        return em.createQuery("select ug.userAccount from UserGroup ug where ug.accessGroup = :accessGroup order by ug.userAccount.firstName")
                 .setParameter("accessGroup", accessGroup)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<UserGroup> findUsersGroups(AccessGroup accessGroup) {
        return em.createQuery("select ug from UserGroup ug where ug.accessGroup = :accessGroup")
                 .setParameter("accessGroup", accessGroup)
                 .getResultList();
    }

    @SuppressWarnings("unchecked")
	public List<AccessGroup> findGroupsUser(UserAccount userAccount) {
        return em.createQuery("select ug.accessGroup from UserGroup ug where ug.userAccount = :userAccount")
                 .setParameter("userAccount", userAccount)
                 .getResultList();
    }

    public void update(AccessGroup accessGroup, List<UserGroup> userGroups) {
        if(userGroups.isEmpty()) {
            em.createQuery("delete from UserGroup ug where ug.accessGroup = :accessGroup")
                    .setParameter("accessGroup", accessGroup)
                    .executeUpdate();
            return;
        }
        
        List<UserGroup> currentUserGroups = findUsersGroups(accessGroup);

        for(UserGroup userGroup: currentUserGroups) {
            if(!userGroups.contains(userGroup)) {
                em.remove(userGroup);
            }
        }

        for(UserGroup userGroup: userGroups) {
            if(!currentUserGroups.contains(userGroup)) {
                em.persist(userGroup);
            }
        }
    }

    public void removeUserFromAllGroups(UserAccount userAccount) {
        em.createQuery("delete from UserGroup ug where ug.userAccount = :userAccount")
                .setParameter("userAccount", userAccount)
                .executeUpdate();
    }

    public void add(UserGroup userGroup) {
        em.persist(userGroup);
    }
}