package model;

import java.util.*;

public class User extends UserBase {
    private List<String> followers;
    private List<String> following;
    private List<String> pendingFollowRequests;
    private List<String> blockedUsers;
    private List<Message> inbox;
    private List<Notification> notifications;

    // Full Constructor
    public User(String username, String email, String password, String fullName, String gender, String dateOfBirth) {
        super(username, email, password, fullName, gender, dateOfBirth);
        initializeLists();
    }

    private void initializeLists() {
        this.followers = new ArrayList<>();
        this.following = new ArrayList<>();
        this.pendingFollowRequests = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
        this.inbox = new ArrayList<>();
        this.notifications = new ArrayList<>();
    }

    @Override
    public boolean login(String username, String password) {
        if (this.getUsername().equals(username)) {
            if (this.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void logout() {
        System.out.println("User " + this.getUsername() + " logged out");
    }

    @Override
    public boolean updateProfile(String bio, String profilePic, String fullName) {
        this.setBio(bio);
        this.setProfilePic(profilePic);
        this.setFullName(fullName);
        return true;
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.getPassword().equals(oldPassword)) {
            this.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public List<String> getFollowing() {
        return following;
    }

    public List<String> getPendingFollowRequests() {
        return pendingFollowRequests;
    }

    public List<String> getBlockedUsers() {
        return blockedUsers;
    }

    public List<Message> getInbox() {
        return inbox;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void addFollower(String userId) {
        boolean exists = false;
        for (String id : followers) {
            if (id.equals(userId)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            followers.add(userId);
        }
    }

    public void removeFollower(String userId) {
        followers.remove(userId);
    }

    public void addFollowing(String userId) {
        boolean exists = false;
        for (String id : following) {
            if (id.equals(userId)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            following.add(userId);
        }
    }

    public void removeFollowing(String userId) {
        following.remove(userId);
    }

    public void addPendingFollowRequest(String userId) {
        boolean exists = false;
        for (String id : pendingFollowRequests) {
            if (id.equals(userId)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            pendingFollowRequests.add(userId);
        }
    }

    public void removePendingFollowRequest(String userId) {
        pendingFollowRequests.remove(userId);
    }

    public void blockUser(String userId) {
        boolean exists = false;
        for (String id : blockedUsers) {
            if (id.equals(userId)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            blockedUsers.add(userId);
        }
    }

    public void unblockUser(String userId) {
        blockedUsers.remove(userId);
    }

    // 3. Object Class Overrides (toString, equals)
    @Override
    public String toString() {
        return "User{username='" + getUsername() + "', fullName='" + getFullName() + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (getClass() != o.getClass())
            return false;

        User user = (User) o;
        return getId().equals(user.getId()); // Compare by unique ID
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}
