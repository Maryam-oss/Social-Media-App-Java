package gui;

import javax.swing.*;
import model.User;
import model.Post;
import service.PostService;
import service.UserService;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Comparator;
import java.util.Collections;

import java.awt.*;
import java.io.File;
import java.util.List;

public class ProfilePanel extends JPanel {
    private User userToView;
    private User currentUser;
    private PostService postService;
    private UserService userService;
    private JPanel postsContainer;
    private JLabel statsLabel;
    private JLabel bioLabel;
    private JLabel profilePicLabel;
    private JButton followButton;
    private JButton followersBtn;
    private JButton followingBtn;
    private Timer pollingTimer;

    public ProfilePanel(User userToView, User currentUser) {
        this.userToView = userToView;
        this.currentUser = currentUser;
        this.postService = new PostService();
        this.userService = new UserService();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
        refreshProfile();
        // Polling removed as per manual refactoring preference
    }

    private void initComponents() {
        // Profile Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Profile picture
        gbc.gridx = 0;
        gbc.gridy = 0;
        profilePicLabel = new JLabel();
        ImageIcon profilePic = ImageUtil.loadImageFromPath(userToView.getProfilePic(), 100, 100);
        profilePicLabel.setIcon(profilePic);
        infoPanel.add(profilePicLabel, gbc);

        // Upload and Delete buttons - ONLY IF CURRENT USER
        if (isCurrentUserProfile()) {
            JPanel picButtonsPanel = new JPanel();
            picButtonsPanel.setBackground(Color.WHITE);
            JButton uploadButton = new JButton("Upload");
            uploadButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    uploadProfilePic();
                }
            });
            picButtonsPanel.add(uploadButton);

            JButton deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    deleteProfilePic();
                }
            });
            picButtonsPanel.add(deleteButton);
            gbc.gridx = 0;
            gbc.gridy = 1;
            infoPanel.add(picButtonsPanel, gbc);
        } else {
            // Not current user -> Show Follow and Block buttons
            JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            actionButtonsPanel.setBackground(Color.WHITE);

            // Follow Button
            followButton = new JButton();
            updateFollowButtonState();
            followButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleFollow();
                }
            });
            actionButtonsPanel.add(followButton);

            // Block Button
            JButton blockButton = new JButton();
            if (currentUser.getBlockedUsers().contains(userToView.getId())) {
                blockButton.setText("Unblock");
            } else {
                blockButton.setText("Block");
            }
            blockButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    toggleBlock(blockButton);
                }
            });
            actionButtonsPanel.add(blockButton);

            gbc.gridx = 0;
            gbc.gridy = 1;
            infoPanel.add(actionButtonsPanel, gbc);
        }

        // User info
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(userToView.getUsername());
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        detailsPanel.add(usernameLabel);

        JLabel fullNameLabel = new JLabel(userToView.getFullName());
        fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsPanel.add(fullNameLabel);

        bioLabel = new JLabel("Bio: " + userToView.getBio());
        bioLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        detailsPanel.add(bioLabel);

        // Stats Panel with clickable followers/following
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statsPanel.setBackground(Color.WHITE);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statsPanel.add(statsLabel);

        // Clickable Followers button
        followersBtn = new JButton();
        followersBtn.setBorderPainted(false);
        followersBtn.setContentAreaFilled(false);
        followersBtn.setFocusPainted(false);
        followersBtn.setForeground(new Color(51, 102, 255));
        followersBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        followersBtn.setFont(new Font("Arial", Font.BOLD, 12));
        followersBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFollowersList();
            }
        });
        statsPanel.add(followersBtn);

        // Clickable Following button
        followingBtn = new JButton();
        followingBtn.setBorderPainted(false);
        followingBtn.setContentAreaFilled(false);
        followingBtn.setFocusPainted(false);
        followingBtn.setForeground(new Color(51, 102, 255));
        followingBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        followingBtn.setFont(new Font("Arial", Font.BOLD, 12));
        followingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFollowingList();
            }
        });
        statsPanel.add(followingBtn);

        detailsPanel.add(statsPanel);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        infoPanel.add(detailsPanel, gbc);

        headerPanel.add(infoPanel, BorderLayout.CENTER);

        // Posts Container
        postsContainer = new JPanel();
        postsContainer.setLayout(new BoxLayout(postsContainer, BoxLayout.Y_AXIS));
        postsContainer.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(postsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, headerPanel, scrollPane);
        splitPane.setDividerLocation(220);
        add(splitPane);
    }

    private boolean isCurrentUserProfile() {
        return userToView.getId().equals(currentUser.getId());
    }

    public void refreshProfile() {
        // Use SwingWorker for background loading
        new SwingWorker<Void, Void>() {
            private User refreshedUser;
            private List<Post> userPosts;

            @Override
            protected Void doInBackground() throws Exception {
                refreshedUser = userService.getUserById(userToView.getId());
                if (refreshedUser != null)
                    userToView = refreshedUser;

                userPosts = postService.getPostsByUser(userToView.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Check for exceptions
                    updateProfileUI(userPosts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateProfileUI(List<Post> userPosts) {
        profilePicLabel.setIcon(ImageUtil.loadImageFromPath(userToView.getProfilePic(), 100, 100));

        postsContainer.removeAll();

        // Stats
        statsLabel.setText("Posts: " + userPosts.size() + " |");
        followersBtn.setText("Followers: " + userToView.getFollowers().size());
        followingBtn.setText("Following: " + userToView.getFollowing().size());

        // Privacy Check
        boolean isPrivate = userToView.isPrivateAccount();
        boolean isFollowing = currentUser.getFollowing().contains(userToView.getId());
        boolean isMe = isCurrentUserProfile();

        if (isPrivate && !isFollowing && !isMe) {
            JLabel privateLabel = new JLabel("This account is private. Follow to see their posts.");
            privateLabel.setFont(new Font("Arial", Font.BOLD, 14));
            privateLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            postsContainer.add(Box.createVerticalStrut(50));
            postsContainer.add(privateLabel);
        } else {
            if (userPosts.isEmpty()) {
                JLabel noPosts = new JLabel("No posts yet");
                noPosts.setFont(new Font("Arial", Font.ITALIC, 12));
                postsContainer.add(noPosts);
            } else {
                // Show newest post on top - explicit sort
                Collections.sort(userPosts, new Comparator<Post>() {
                    @Override
                    public int compare(Post p1, Post p2) {
                        return Long.compare(p2.getTimestamp(), p1.getTimestamp());
                    }
                });
                for (Post post : userPosts) {
                    postsContainer.add(new PostCardPanel(post, currentUser, postService, userService));
                    postsContainer.add(Box.createVerticalStrut(10));
                }
            }
        }
        postsContainer.revalidate();
        postsContainer.repaint();

        // Update follow button state after data refresh
        if (!isCurrentUserProfile() && followButton != null) {
            updateFollowButtonState();
        }
    }

    public void addPostToProfile(Post post) {
        // Only add if we are viewing the profile that just posted (which should be
        // current user)
        if (!isCurrentUserProfile())
            return;

        // Add post at top
        postsContainer.add(new PostCardPanel(post, currentUser, postService, userService), 0);
        postsContainer.add(Box.createVerticalStrut(10), 1);

        // Update stats
        statsLabel.setText("Posts: " + postService.getPostsByUser(userToView.getId()).size() + " |");
        followersBtn.setText("Followers: " + userToView.getFollowers().size());
        followingBtn.setText("Following: " + userToView.getFollowing().size());

        postsContainer.revalidate();
        postsContainer.repaint();
    }

    private void uploadProfilePic() {
        JFileChooser chooser = new JFileChooser();
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            String selectedPath = selectedFile.getAbsolutePath();
            if (userService.uploadProfilePicture(userToView.getId(), selectedPath)) {
                JOptionPane.showMessageDialog(this, "Profile picture updated successfully!");
                refreshProfile();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile picture.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteProfilePic() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete your profile picture?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (userService.deleteProfilePicture(userToView.getId())) {
                JOptionPane.showMessageDialog(this, "Profile picture deleted successfully!");
                refreshProfile();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete profile picture.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleBlock(JButton blockButton) {
        if (currentUser.getBlockedUsers().contains(userToView.getId())) {
            if (userService.unblockUser(currentUser.getId(), userToView.getId())) {
                currentUser.unblockUser(userToView.getId());
                blockButton.setText("Block");
                JOptionPane.showMessageDialog(this, "User unblocked.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to unblock user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (userService.blockUser(currentUser.getId(), userToView.getId())) {
                currentUser.blockUser(userToView.getId());
                blockButton.setText("Unblock");
                JOptionPane.showMessageDialog(this, "User blocked.");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to block user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshPostsData() {
        for (Component comp : postsContainer.getComponents()) {
            if (comp instanceof PostCardPanel) {
                ((PostCardPanel) comp).refresh();
            }
        }
    }

    private void updateFollowButtonState() {
        if (followButton == null)
            return;

        if (currentUser.getFollowing().contains(userToView.getId())) {
            followButton.setText("Unfollow");
            followButton.setBackground(Color.GRAY);
            followButton.setForeground(Color.WHITE);
        } else if (userToView.getPendingFollowRequests().contains(currentUser.getId())) {
            followButton.setText("Requested");
            followButton.setBackground(Color.LIGHT_GRAY);
            followButton.setForeground(Color.BLACK);
        } else {
            followButton.setText("Follow");
            followButton.setBackground(new Color(51, 102, 255));
            followButton.setForeground(Color.WHITE);
        }
    }

    private void toggleFollow() {
        if (currentUser.getFollowing().contains(userToView.getId())) {
            if (userService.unfollowUser(currentUser.getId(), userToView.getId())) {
                currentUser.removeFollowing(userToView.getId());
                JOptionPane.showMessageDialog(this, "Unfollowed " + userToView.getUsername());
                refreshProfile();
            }
        } else {
            if (userService.followUser(currentUser.getId(), userToView.getId())) {
                if (userToView.isPrivateAccount() && !userToView.getFollowers().contains(currentUser.getId())) {
                    JOptionPane.showMessageDialog(this, "Follow request sent.");
                } else {
                    currentUser.addFollowing(userToView.getId());
                    JOptionPane.showMessageDialog(this, "You are now following " + userToView.getUsername());
                }
                refreshProfile();
            }
        }
    }

    private void showFollowersList() {
        showUserListDialog(userToView, "followers");
    }

    private void showFollowingList() {
        showUserListDialog(userToView, "following");
    }

    private void showUserListDialog(User targetUser, String listType) {
        boolean isPrivate = targetUser.isPrivateAccount();
        boolean isFollowing = currentUser.getFollowing().contains(targetUser.getId());
        boolean isMe = targetUser.getId().equals(currentUser.getId());

        if (isPrivate && !isFollowing && !isMe) {
            JOptionPane.showMessageDialog(this, "This account is private.", "Private Account",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        UserListDialog dialog = new UserListDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                targetUser,
                listType,
                currentUser,
                userService);
        dialog.setVisible(true);
    }
}
