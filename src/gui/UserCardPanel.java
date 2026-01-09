package gui;

import javax.swing.*;
import java.awt.*;
import model.User;
import service.FollowService;
import service.UserService;

public class UserCardPanel extends JPanel {

    private User user; // user being displayed
    private User currentUser; // logged-in user
    private FollowService followService;

    private JLabel followersLabel;
    private JLabel followingLabel;
    private JButton followButton;
    private JButton blockButton;

    private UserService userService;

    public UserCardPanel(User user, User currentUser, FollowService followService) {
        this.user = user;
        this.currentUser = currentUser;
        this.followService = followService;
        this.userService = new UserService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        initComponents();
    }

    // BLOCK / UNBLOCK
    private void toggleBlock() {
        if (currentUser.getBlockedUsers().contains(user.getId())) {
            if (userService.unblockUser(currentUser.getId(), user.getId())) {
                currentUser.unblockUser(user.getId());
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unblock user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (userService.blockUser(currentUser.getId(), user.getId())) {
                currentUser.blockUser(user.getId());

                // Optional: auto unfollow if blocked
                if (currentUser.getFollowing().contains(user.getUsername())) {
                    followService.unfollow(currentUser, user);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to block user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        updateBlockButtonText();
        updateCounts();
        updateFollowButtonText();
    }

    private void initComponents() {
        removeAll();

        // PROFILE PIC
        JLabel profilePicLabel = new JLabel();
        ImageIcon profilePic = ImageUtil.loadImageFromPath(user.getProfilePic(), 60, 60);
        profilePicLabel.setIcon(profilePic);
        profilePicLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(profilePicLabel, BorderLayout.WEST);

        // USER INFO
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some padding

        JLabel usernameLabel = new JLabel(user.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(usernameLabel);

        followersLabel = new JLabel("Followers: " + user.getFollowers().size());
        followingLabel = new JLabel("Following: " + user.getFollowing().size());
        infoPanel.add(followersLabel);
        infoPanel.add(followingLabel);

        add(infoPanel, BorderLayout.CENTER);

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);

        // Follow / Unfollow
        // Follow / Unfollow
        followButton = new JButton();
        updateFollowButtonText();
        followButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleFollow();
            }
        });
        buttonPanel.add(followButton);

        // Block / Unblock
        blockButton = new JButton();
        updateBlockButtonText();
        blockButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                toggleBlock();
            }
        });
        buttonPanel.add(blockButton);

        // View Profile
        JButton viewProfileButton = new JButton("View Profile");
        viewProfileButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openUserProfile();
            }
        });
        buttonPanel.add(viewProfileButton);

        add(buttonPanel, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    // FOLLOW / UNFOLLOW
    private void toggleFollow() {
        if (currentUser.getFollowing().contains(user.getId())) {
            followService.unfollow(currentUser, user);
        } else if (user.getPendingFollowRequests().contains(currentUser.getId())) {
            followService.cancelFollowRequest(currentUser, user);
        } else {
            followService.follow(currentUser, user);
        }
        updateCounts();
        updateFollowButtonText();
    }

    private void updateCounts() {
        followersLabel.setText("Followers: " + user.getFollowers().size());
        followingLabel.setText("Following: " + user.getFollowing().size());
    }

    private void updateFollowButtonText() {
        if (currentUser.getFollowing().contains(user.getId())) {
            followButton.setText("Unfollow");
        } else if (user.getPendingFollowRequests().contains(currentUser.getId())) {
            followButton.setText("Requested");
        } else {
            followButton.setText("Follow");
        }
    }

    // BLOCK / UNBLOCK
    private void updateBlockButtonText() {
        if (currentUser.getBlockedUsers().contains(user.getId())) {
            blockButton.setText("Unblock");
        } else {
            blockButton.setText("Block");
        }
    }

    // VIEW PROFILE
    private void openUserProfile() {
        if (currentUser.getBlockedUsers().contains(user.getId())) {
            JFrame frame = new JFrame("Profile Not Found");
            frame.setSize(300, 150);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JLabel label = new JLabel("Profile Not Found", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            panel.add(label, BorderLayout.CENTER);

            frame.add(panel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            return;
        }

        // Normal profile view
        JFrame profileFrame = new JFrame(user.getUsername() + "'s Profile");
        profileFrame.setSize(600, 700);
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Use the reused ProfilePanel
        ProfilePanel profilePanel = new ProfilePanel(user, currentUser);
        profileFrame.add(profilePanel);

        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(true);
    }

}
