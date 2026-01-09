package service;

import model.User;
import java.util.List;

//Interface for User Service operations
//Provides abstraction for user-related business logic

public interface IUserService {

    // Authentication & Account Management
    boolean signup(String username, String email, String password, String fullName, String gender, String dob);

    User login(String username, String password);

    void logout(String userId);

    // Profile Management
    User getUserProfile(String userId);

    User getUserByUsername(String username);

    boolean updateProfile(String userId, String bio, String profilePic, String fullName);

    boolean changePassword(String userId, String oldPassword, String newPassword);

    boolean setPrivateAccount(String userId, boolean isPrivate);

    // Search
    List<User> searchUsers(String query);

    // Block Operations
    boolean blockUser(String userId, String targetUserId);

    boolean unblockUser(String userId, String targetUserId);

    List<String> getBlockedUsers(String userId);

    // Profile Picture Management
    boolean uploadProfilePicture(String userId, String filePath);

    boolean deleteProfilePicture(String userId);
}
