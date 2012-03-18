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

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.UserAccount;
import org.cejug.entity.UserGroup;

/**
 * Business logic to manage the relationship between users and access groups.
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