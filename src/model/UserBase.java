package model;

import java.util.UUID;

public abstract class UserBase {
    private String id;
    private String username;
    private String email;
    private String password;
    private String bio;
    private String profilePic;
    private boolean isPrivateAccount;
    private String fullName;
    private String gender;
    private String dateOfBirth;

    public UserBase(String username, String email, String password, String fullName, String gender,
            String dateOfBirth) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.bio = "";
        this.profilePic = "";
        this.isPrivateAccount = false;
    }

    public abstract boolean login(String username, String password);

    public abstract void logout();

    public abstract boolean updateProfile(String bio, String profilePic, String fullName);

    public abstract boolean changePassword(String oldPassword, String newPassword);

    // Getters & Setters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public boolean isPrivateAccount() {
        return isPrivateAccount;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public void setPrivateAccount(boolean isPrivate) {
        this.isPrivateAccount = isPrivate;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Protected setters for subclass access
    protected void setUsername(String username) {
        this.username = username;
    }

    protected void setEmail(String email) {
        this.email = email;
    }
}
