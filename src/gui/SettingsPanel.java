package gui;

import javax.swing.*;
import model.User;
import service.UserService;
import service.ValidationUtil;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class SettingsPanel extends JPanel {
    private User currentUser;
    private UserService userService;
    private JFrame mainFrame;
    private JLabel emailLabel, dobLabel, genderLabel;
    private JTextArea bioArea;
    private JTextField fullNameField;

    public SettingsPanel(User currentUser, JFrame mainFrame) {
        this.currentUser = currentUser;
        this.userService = new UserService();
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Account", createAccountTab());
        tabbedPane.addTab("Privacy", createPrivacyTab());

        add(tabbedPane);
    }

    private JPanel createAccountTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel sectionLabel = new JLabel("Profile Information");
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(sectionLabel);
        panel.add(Box.createVerticalStrut(10));
//for user details
        panel.add(new JLabel("Username: " + currentUser.getUsername()));
        emailLabel = new JLabel("Email: " + currentUser.getEmail());
        panel.add(emailLabel);
        genderLabel = new JLabel("Gender: " + currentUser.getGender());
        panel.add(genderLabel);
        dobLabel = new JLabel("Date of Birth: " + currentUser.getDateOfBirth());
        panel.add(dobLabel);

        panel.add(Box.createVerticalStrut(20));

        JLabel editLabel = new JLabel("Edit Profile");
        editLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(editLabel);

        panel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField(currentUser.getFullName(), 20);
        panel.add(fullNameField);

        panel.add(new JLabel("Bio:"));
        bioArea = new JTextArea(currentUser.getBio(), 3, 20);
        //in a scroll pane 
        bioArea.setLineWrap(true);
        bioArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(bioArea));

        JButton updateProfileButton = new JButton("Update Profile");
        updateProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateProfile();
            }
        });
        panel.add(updateProfileButton);

        panel.add(Box.createVerticalStrut(20));

        JLabel passwordLabel = new JLabel("Change Password");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(passwordLabel);

        panel.add(new JLabel("Old Password:"));
      //for hidden password
        JPasswordField oldPasswordField = new JPasswordField(20);
        panel.add(oldPasswordField);

        panel.add(new JLabel("New Password:"));
        JPasswordField newPasswordField = new JPasswordField(20);
        panel.add(newPasswordField);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                changePassword(oldPasswordField, newPasswordField);
            }
        });
        panel.add(changePasswordButton);

        panel.add(Box.createVerticalStrut(20));
//delete button 
        JLabel deleteLabel = new JLabel("Danger Zone");
        deleteLabel.setFont(new Font("Arial", Font.BOLD, 14));
        deleteLabel.setForeground(Color.RED);
        panel.add(deleteLabel);

        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setBackground(Color.RED);
        deleteAccountButton.setForeground(Color.WHITE);
        deleteAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAccount();
            }
        });
        panel.add(deleteAccountButton);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel createPrivacyTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JCheckBox privateCheckBox = new JCheckBox("Private Account", currentUser.isPrivateAccount());
        privateCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = currentUser.getId();
                boolean selected = privateCheckBox.isSelected();
              //in user base
                userService.togglePrivateAccount(id, selected);

                currentUser.setPrivateAccount(selected);
            }
        });
        panel.add(privateCheckBox);

        panel.add(Box.createVerticalStrut(20));
//logout
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(51, 102, 255));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        panel.add(logoutButton);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void updateProfile() {
        String bio = bioArea.getText();
        String fullName = fullNameField.getText();

        if (!ValidationUtil.isValidFullName(fullName)) {
            JOptionPane.showMessageDialog(this, "Invalid full name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
//from user base or service
        String id = currentUser.getId();
        String pic = currentUser.getProfilePic();

        boolean success = userService.updateProfile(id, bio, pic, fullName);

        if (success) {
            currentUser = userService.getUserById(id);
            JOptionPane.showMessageDialog(this, "Profile updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update profile", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changePassword(JPasswordField oldPasswordField, JPasswordField newPasswordField) {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());

        if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
//from validation class
        if (!ValidationUtil.isValidPassword(newPassword)) {
            JOptionPane.showMessageDialog(this, ValidationUtil.getPasswordRequirements(), "Invalid Password",  JOptionPane.ERROR_MESSAGE);
            return;
        }

        String userId = currentUser.getId();
        boolean success = userService.changePassword(userId, oldPassword, newPassword);

        if (success) {
            JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success",JOptionPane.INFORMATION_MESSAGE);
            oldPasswordField.setText("");
            newPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to change password. Check old password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAccount() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account? This cannot be undone.", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String password = JOptionPane.showInputDialog(this, "Enter your password to confirm deletion:");
//user service fucntions
            if (password != null) {
                User user = userService.login(currentUser.getUsername(), password);

                if (user != null) {
                    boolean deleted = userService.deleteAccount(currentUser.getId());
                    if (deleted) {
                        JOptionPane.showMessageDialog(this, "Account deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        mainFrame.dispose();
                        LoginFrame loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete account", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid password", "Error", JOptionPane.ERROR_MESSAGE);
                }}  }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout",JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser.logout();
            mainFrame.dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}
