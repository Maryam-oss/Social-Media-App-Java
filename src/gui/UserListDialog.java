package gui;

import model.User;
import service.UserService;
import service.FollowService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Stack;

public class UserListDialog extends JDialog {
    private User currentUser;
    private UserService userService;
    private JPanel contentPanel;
    private JPanel headerPanel;
    private JLabel titleLabel;
    private JButton backButton;
    private Stack<DialogState> navigationStack;

    private static class DialogState {
        User targetUser;
        String listType;

        DialogState(User targetUser, String listType) {
            this.targetUser = targetUser;
            this.listType = listType;
        }
    }

    public UserListDialog(Frame owner, User targetUser, String listType, User currentUser,
            UserService userService) {
        super(owner, "", true);
        this.currentUser = currentUser;
        this.userService = userService;
        this.navigationStack = new Stack<>();

        setSize(450, 550);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Modern gradient background color
        getContentPane().setBackground(new Color(240, 242, 245));

        initHeader();
        initContent();

        // Show initial content
        showUserList(targetUser, listType);
    }

    private void initHeader() {
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 102, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Back button
        backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(41, 82, 204));
        backButton.setBorderPainted(false);
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setVisible(false);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        headerPanel.add(backButton, BorderLayout.WEST);

        // Title
        titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void initContent() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(240, 242, 245));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void showUserList(User targetUser, String listType) {
        // Update navigation stack
        navigationStack.push(new DialogState(targetUser, listType));
        backButton.setVisible(navigationStack.size() > 1);

        // Update title
        String title;
        if (listType.equals("followers")) {
            title = "Followers of " + targetUser.getUsername();
        } else {
            title = "Following by " + targetUser.getUsername();
        }
        titleLabel.setText(title);

        // Get user list
        java.util.List<String> userIds;
        if (listType.equals("followers")) {
            userIds = targetUser.getFollowers();
        } else {
            userIds = targetUser.getFollowing();
        }

        // Clear and rebuild content
        contentPanel.removeAll();
        contentPanel.add(Box.createVerticalStrut(10));

        if (userIds.isEmpty()) {
            JLabel emptyLabel = new JLabel("No users to display");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(100, 100, 100));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
            contentPanel.add(emptyLabel);
        } else {
            for (String userId : userIds) {
                User user = userService.getUserById(userId);
                if (user != null) {
                    UserListItemPanel itemPanel = new UserListItemPanel(user, currentUser, this);
                    contentPanel.add(itemPanel);
                    contentPanel.add(Box.createVerticalStrut(8));
                }
            }
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void goBack() {
        if (navigationStack.size() > 1) {
            navigationStack.pop(); // Remove current state
            DialogState previousState = navigationStack.pop(); // Get previous state
            showUserList(previousState.targetUser, previousState.listType);
        }
    }

    public void navigateToUserFollowers(User user) {
        showUserList(user, "followers");
    }

    public void navigateToUserFollowing(User user) {
        showUserList(user, "following");
    }

    // Inner panel for each user item
    private static class UserListItemPanel extends JPanel {
        public UserListItemPanel(User user, User currentUser, UserListDialog parentDialog) {
            setLayout(new BorderLayout(10, 0));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

            // Profile picture
            JLabel profilePicLabel = new JLabel();
            ImageIcon profilePic = ImageUtil.loadImageFromPath(user.getProfilePic(), 55, 55);
            profilePicLabel.setIcon(profilePic);
            add(profilePicLabel, BorderLayout.WEST);

            // User info
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);

            JLabel usernameLabel = new JLabel(user.getUsername());
            usernameLabel.setFont(new Font("Arial", Font.BOLD, 15));
            infoPanel.add(usernameLabel);

            JLabel fullNameLabel = new JLabel(user.getFullName());
            fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            fullNameLabel.setForeground(new Color(100, 100, 100));
            infoPanel.add(fullNameLabel);

            // Clickable stats
            JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            statsPanel.setBackground(Color.WHITE);

            JButton followersBtn = createStatButton("Followers: " + user.getFollowers().size());
            followersBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parentDialog.navigateToUserFollowers(user);
                }
            });
            statsPanel.add(followersBtn);

            JButton followingBtn = createStatButton("Following: " + user.getFollowing().size());
            followingBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    parentDialog.navigateToUserFollowing(user);
                }
            });
            statsPanel.add(followingBtn);

            infoPanel.add(statsPanel);
            add(infoPanel, BorderLayout.CENTER);

            // Action buttons
            JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            buttonPanel.setBackground(Color.WHITE);

            // Follow button
            if (!user.getId().equals(currentUser.getId())) {
                JButton followBtn = new JButton();
                updateFollowButton(followBtn, user, currentUser);
                followBtn.setFont(new Font("Arial", Font.BOLD, 12));
                followBtn.setFocusPainted(false);
                followBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                followBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        toggleFollow(user, currentUser);
                        updateFollowButton(followBtn, user, currentUser);
                    }
                });
                buttonPanel.add(followBtn);
            }

            add(buttonPanel, BorderLayout.EAST);
        }

        private JButton createStatButton(String text) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setForeground(new Color(51, 102, 255));
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        private void updateFollowButton(JButton btn, User user, User currentUser) {
            boolean isFollowing = false;
            for (String id : currentUser.getFollowing()) {
                if (id.equals(user.getId())) {
                    isFollowing = true;
                    break;
                }
            }

            boolean isRequested = false;
            for (String id : user.getPendingFollowRequests()) {
                if (id.equals(currentUser.getId())) {
                    isRequested = true;
                    break;
                }
            }

            if (isFollowing) {
                btn.setText("Unfollow");
                btn.setBackground(new Color(200, 200, 200));
                btn.setForeground(Color.BLACK);
            } else if (isRequested) {
                btn.setText("Requested");
                btn.setBackground(new Color(220, 220, 220));
                btn.setForeground(Color.BLACK);
            } else {
                btn.setText("Follow");
                btn.setBackground(new Color(51, 102, 255));
                btn.setForeground(Color.WHITE);
            }
        }

        private void toggleFollow(User user, User currentUser) {
            FollowService followService = FollowService.getInstance();

            boolean isFollowing = false;
            for (String id : currentUser.getFollowing()) {
                if (id.equals(user.getId())) {
                    isFollowing = true;
                    break;
                }
            }

            boolean isRequested = false;
            for (String id : user.getPendingFollowRequests()) {
                if (id.equals(currentUser.getId())) {
                    isRequested = true;
                    break;
                }
            }

            if (isFollowing) {
                followService.unfollow(currentUser, user);
            } else if (isRequested) {
                followService.cancelFollowRequest(currentUser, user);
            } else {
                followService.follow(currentUser, user);
            }
        }
    }
}
