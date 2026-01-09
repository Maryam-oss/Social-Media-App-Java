package service;

import model.User;
import dao.UserDAO;
import dao.PostDAO;
import dao.MessageDAO;
import model.Post;
import model.Message;

import java.util.List;

public class UserService implements IUserService {

    private final UserDAO userDAO;
    private final PostDAO postDAO;
    private final MessageDAO messageDAO;

    public UserService() {
        this.userDAO = new UserDAO();
        this.postDAO = new PostDAO();
        this.messageDAO = new MessageDAO();
    }

    // Signup / Login

    public boolean signup(String username, String email, String password, String fullName, String gender, String dob) {
        if (!ValidationUtil.isValidUsername(username))
            return false;
        if (!ValidationUtil.isValidEmail(email))
            return false;
        if (!ValidationUtil.isValidPassword(password))
            return false;
        if (!ValidationUtil.isValidFullName(fullName))
            return false;
        if (userDAO.findByUsername(username) != null)
            return false;

        User user = new User(username, email, password, fullName, gender, dob);
        userDAO.create(user);
        return true;
    }

    public User login(String username, String password) {
        User user = userDAO.findByUsername(username);
        if (user != null) {
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public void logout(String userId) {
        // Logging out
        User user = userDAO.findById(userId);
        if (user != null) {
            user.logout();
        }
    }

    // User Management

    public boolean deleteAccount(String userId) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;

        // Delete Posts
        List<Post> posts = postDAO.getPostsByUser(userId);
        for (Post post : posts) {
            postDAO.delete(post.getId());
        }

        // Delete Messages
        List<Message> allMessages = messageDAO.getAll();
        for (Message msg : allMessages) {
            if (msg.getSenderId().equals(userId) || msg.getReceiverId().equals(userId)) {
                messageDAO.delete(msg.getId());
            }
        }

        // Remove from followers/following of others
        List<User> allUsers = userDAO.getAll();
        for (User u : allUsers) {
            u.removeFollower(userId);
            u.removeFollowing(userId);
            u.removePendingFollowRequest(userId);
            userDAO.update(u);
        }

        return userDAO.delete(userId);
    }

    public boolean togglePrivateAccount(String userId, boolean isPrivate) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;
        user.setPrivateAccount(isPrivate);
        return userDAO.update(user);
    }

    // Follow / Unfollow / Block

    public boolean followUser(String myId, String targetId) {
        User me = userDAO.findById(myId);
        User target = userDAO.findById(targetId);
        if (me == null || target == null)
            return false;
        if (myId.equals(targetId))
            return false;

        List<String> blocked = me.getBlockedUsers();
        boolean isBlocked = false;
        for (String id : blocked) {
            if (id.equals(targetId)) {
                isBlocked = true;
                break;
            }
        }

        if (isBlocked)
            return false;

        service.FollowService.getInstance().follow(me, target);
        return true;
    }

    public boolean unfollowUser(String myId, String targetId) {
        User me = userDAO.findById(myId);
        User target = userDAO.findById(targetId);
        if (me == null || target == null)
            return false;

        service.FollowService.getInstance().unfollow(me, target);
        return true;
    }

    public boolean blockUser(String userId, String blockedUserId) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;
        user.blockUser(blockedUserId);
        return userDAO.update(user);
    }

    public boolean unblockUser(String userId, String blockedUserId) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;
        user.unblockUser(blockedUserId);
        return userDAO.update(user);
    }

    // Follow requests

    public boolean approveFollowRequest(String userId, String followerId) {
        User user = userDAO.findById(userId);
        User follower = userDAO.findById(followerId);
        if (user == null || follower == null)
            return false;

        service.FollowService.getInstance().acceptFollowRequest(user, follower);
        return true;
    }

    public boolean rejectFollowRequest(String userId, String followerId) {
        User user = userDAO.findById(userId);
        User follower = userDAO.findById(followerId);
        if (user == null || follower == null)
            return false;

        service.FollowService.getInstance().declineFollowRequest(user, follower);
        return true;
    }

    // Profile & Password

    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;
        if (!user.getPassword().equals(oldPassword))
            return false;

        if (!ValidationUtil.isValidPassword(newPassword))
            return false;
        user.setPassword(newPassword);
        return userDAO.update(user);
    }

    public boolean updateProfile(String userId, String bio, String profilePic, String fullName) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;
        user.updateProfile(bio, profilePic, fullName);
        return userDAO.update(user);
    }

    // Update Profile Picture

    @Override
    public boolean uploadProfilePicture(String userId, String profilePicPath) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;

        String storedImageId = "";
        if (profilePicPath != null && !profilePicPath.isEmpty()) {
            java.io.InputStream streamToUploadFrom = null;
            try {
                java.io.File file = new java.io.File(profilePicPath);
                if (file.exists()) {
                    streamToUploadFrom = new java.io.FileInputStream(file);
                    com.mongodb.client.gridfs.GridFSBucket gridFSBucket = dao.DBConnection.getGridFSBucket();
                    org.bson.types.ObjectId fileId = gridFSBucket.uploadFromStream(file.getName(), streamToUploadFrom);
                    storedImageId = fileId.toHexString();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (streamToUploadFrom != null) {
                    try {
                        streamToUploadFrom.close();
                    } catch (java.io.IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }

        if (!storedImageId.isEmpty()) {
            user.setProfilePic(storedImageId);
            return userDAO.update(user);
        }
        return false;
    }

    // Delete / Reset Profile Picture

    @Override
    public boolean deleteProfilePicture(String userId) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;

        user.setProfilePic(null); // set default path like "default.png"
        return userDAO.update(user);
    }

    // Get / Search Users

    public User getUserById(String userId) {
        return userDAO.findById(userId);
    }

    public List<User> getAllUsers() {
        return userDAO.getAll();
    }

    public List<User> searchUsers(String query) {
        return userDAO.searchByUsername(query);
    }

    // Method Overloading Example
    public List<User> searchUsers(String query, boolean caseSensitive) {
        // Simple overloading logic
        if (!caseSensitive) {
            return userDAO.searchByUsername(query.toLowerCase());
        }
        return userDAO.searchByUsername(query);
    }

    // Object Arrays Example (Requirement)
    public User[] getAllUsersAsArray() {
        List<User> list = getAllUsers();
        User[] array = new User[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    @Override
    public List<String> getBlockedUsers(String userId) {
        User user = userDAO.findById(userId);
        if (user != null) {
            return user.getBlockedUsers();
        } else {
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public boolean setPrivateAccount(String userId, boolean isPrivate) {
        User user = userDAO.findById(userId);
        if (user == null)
            return false;
        user.setPrivateAccount(isPrivate);
        return userDAO.update(user);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Override
    public User getUserProfile(String userId) {
        return userDAO.findById(userId);
    }

}
