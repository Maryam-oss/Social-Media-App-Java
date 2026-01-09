package service;

import dao.GroupDAO;
import dao.UserDAO;
import model.Group;

import java.util.List;

public class GroupService {
    private GroupDAO groupDAO;
    private UserDAO userDAO;

    public GroupService() {
        this.groupDAO = new GroupDAO();
        this.userDAO = new UserDAO();
    }

    public Group createGroup(String name, String ownerId, List<String> initialMemberIds) {
        if (name == null || name.trim().isEmpty() || ownerId == null) {
            return null;
        }

        Group group = new Group(name, ownerId);

        // Add initial members if any
        if (initialMemberIds != null) {
            for (String memberId : initialMemberIds) {
                if (userDAO.findById(memberId) != null) {
                    group.addMember(memberId);
                }
            }
        }

        groupDAO.create(group);
        return group;
    }

    public Group getGroupById(String groupId) {
        return groupDAO.findById(groupId);
    }

    public List<Group> getUserGroups(String userId) {
        return groupDAO.getGroupsForUser(userId);
    }

    public boolean addMemberToGroup(String groupId, String userId) {
        Group group = groupDAO.findById(groupId);
        if (group == null)
            return false;

        if (userDAO.findById(userId) == null)
            return false;

        group.addMember(userId);
        return groupDAO.update(group);
    }

    public boolean updateGroupName(String groupId, String newName) {
        Group group = groupDAO.findById(groupId);
        if (group == null || newName == null || newName.trim().isEmpty())
            return false;

        group.setName(newName.trim());
        return groupDAO.update(group);
    }

    public boolean deleteGroup(String groupId) {
        return groupDAO.delete(groupId);
    }
}
