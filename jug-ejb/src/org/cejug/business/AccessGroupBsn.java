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
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.UserAccount;
import org.cejug.entity.UserGroup;
import org.cejug.util.EntitySupport;

/**
 * @author Hildeberto Mendonca
 */
@Stateless
@LocalBean
public class AccessGroupBsn {

    @PersistenceContext
    private EntityManager em;

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private UserGroupBsn userGroupBsn;

    public static final String ADMIN_GROUP = "leaders";
    public static final String DEFAULT_GROUP = "members";

    public AccessGroup findAccessGroup(String groupId) {
        return em.find(AccessGroup.class, groupId);
    }

    public AccessGroup findUserDefaultGroup() {
        AccessGroup defaultUserGroup = null;
        try {
            defaultUserGroup = (AccessGroup) em.createQuery("select ag from AccessGroup ag where ag.userDefault = :default")
                                        .setParameter("default", Boolean.TRUE)
                                        .getSingleResult();
        }
        catch(NoResultException nre) {
            defaultUserGroup = new AccessGroup(DEFAULT_GROUP,"Default Members Group");
            defaultUserGroup.setId(EntitySupport.generateEntityId());
            defaultUserGroup.setUserDefault(Boolean.TRUE);
            em.persist(defaultUserGroup);
        }
        return defaultUserGroup;
    }

    /** Returns the existing administrative group. If it doesn't find anyone 
     *  then a new one is created and returned. */
    public AccessGroup findAdministrativeGroup() {
        AccessGroup group = null;
        try {
            group = (AccessGroup) em.createQuery("select ag from AccessGroup ag where ag.name = :name")
                                        .setParameter("name", ADMIN_GROUP)
                                        .getSingleResult();
        }
        catch(Exception nre) {
            group = new AccessGroup(ADMIN_GROUP,"JUG Leaders Group");
            group.setId(EntitySupport.generateEntityId());
            em.persist(group);
        }
        return group;
    }

    @SuppressWarnings("unchecked")
    public List<AccessGroup> findAccessGroups() {
        return em.createQuery("select ag from AccessGroup ag order by ag.name").getResultList();
    }

    public AccessGroup findAccessGroupByName(String name) {
        try {
            return (AccessGroup) em.createQuery("select ag from AccessGroup ag where ag.name = :name")
                               .setParameter("name", name)
                               .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public void save(AccessGroup accessGroup, List<UserAccount> members) {
        if(accessGroup.getUserDefault()) {
            AccessGroup defaultGroup = findUserDefaultGroup();
            defaultGroup.setUserDefault(false);
        }

        if(accessGroup.getId() == null || accessGroup.getId().isEmpty()) {
            try {
                AccessGroup group = findAccessGroupByName(accessGroup.getName());
                if(group != null)
                	throw new PersistenceException("A group named '"+ accessGroup.getName() +"' already exists.");
            }
            catch(NoResultException nre) {
                accessGroup.setId(EntitySupport.generateEntityId());
                em.persist(accessGroup);
            }
        }
        else
            em.merge(accessGroup);

        if(members != null) {
            UserAccount usr;
            List<UserGroup> usersGroup = new ArrayList<UserGroup>();
            for(UserAccount member: members) {
                usr = userAccountBsn.findUserAccount(member.getId());

                usersGroup.add(new UserGroup(accessGroup, usr));
            }
            userGroupBsn.update(accessGroup, usersGroup);
        }
    }

    public void remove(String groupId) {
        AccessGroup accessGroup = em.find(AccessGroup.class, groupId);
        em.remove(accessGroup);
    }
}