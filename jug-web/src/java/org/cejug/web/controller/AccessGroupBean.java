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
        for(int i = 0;i < membersIds.size();i++) {
            selectedMembers.add(new UserAccount(membersIds.get(i).toString()));
        }

        accessGroupBsn.save(this.group, selectedMembers);
        return "groups?faces-redirect=true";
    }

    public String remove(String groupId) {
        accessGroupBsn.remove(groupId);
        return "groups?faces-redirect=true";
    }
}