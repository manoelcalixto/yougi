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
package org.cejug.web.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.cejug.business.AccessGroupBsn;
import org.cejug.business.UserAccountBsn;
import org.cejug.business.UserGroupBsn;
import org.cejug.entity.AccessGroup;
import org.cejug.entity.UserAccount;
import org.primefaces.model.DualListModel;

/**
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class AccessGroupBean {

    @EJB
    private AccessGroupBsn accessGroupBsn;

    @EJB
    private UserAccountBsn userAccountBsn;

    @EJB
    private UserGroupBsn userGroupBsn;

    @ManagedProperty(value="#{param.id}")
    private String groupId;

    private AccessGroup group;

    // List of members for the picklist.
    private DualListModel<UserAccount> members;
    
    public AccessGroupBean() {}

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public AccessGroup getGroup() {
        return group;
    }

    public void setGroup(AccessGroup group) {
        this.group = group;
    }

    public List<AccessGroup> getGroups() {
        return accessGroupBsn.findAccessGroups();
    }

    public DualListModel<UserAccount> getMembers() {
        return members;
    }

    public void setMembers(DualListModel<UserAccount> members) {
        this.members = members;
    }

    @PostConstruct
    public void load() {
        List<UserAccount> allUsers = userAccountBsn.findUserAccounts();
        List<UserAccount> target = new ArrayList<UserAccount>();

        if(groupId != null && !groupId.isEmpty()) {
            this.group = accessGroupBsn.findAccessGroup(this.groupId);

            target.addAll(userGroupBsn.findUsersGroup(group));
            allUsers.removeAll(target);
        }
        else {
            this.group = new AccessGroup();
        }
        this.members = new DualListModel<UserAccount>(allUsers, target);
    }

    @SuppressWarnings("rawtypes")
    public String save() {
        List<UserAccount> selectedMembers = new ArrayList<UserAccount>();
        List membersIds = this.members.getTarget();
        UserAccount userAccount;
        for(int i = 0;i < membersIds.size();i++) {
            userAccount = new UserAccount(((UserAccount)membersIds.get(i)).getId());
            selectedMembers.add(userAccount);
        }

        accessGroupBsn.save(this.group, selectedMembers);
        return "groups?faces-redirect=true";
    }

    public String remove(String groupId) {
        accessGroupBsn.remove(groupId);
        return "groups?faces-redirect=true";
    }
}