package gui;

import javax.swing.*;
import model.User;
import model.Post;
import service.PostService;
import service.UserService;
import java.awt.*;
import java.util.List;

public class FeedPanel extends JPanel {
    private User currentUser;
    private PostService postService;
    private UserService userService;
    private JPanel feedContainer;

    private Timer pollingTimer;

    public FeedPanel(User currentUser) {
        this.currentUser = currentUser;
        this.postService = new PostService();
        this.userService = new UserService();
        setLayout(new BorderLayout());
        setBackground(ModernUI.BACKGROUND_COLOR);
        initComponents();
        refreshFeed();
        startPolling();
    }

    private void initComponents() {
        feedContainer = new JPanel();
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));
        feedContainer.setBackground(ModernUI.BACKGROUND_COLOR);
        feedContainer.setBorder(new javax.swing.border.EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(feedContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(ModernUI.BACKGROUND_COLOR);
        JButton refreshButton = ModernUI.createSecondaryButton("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                refreshFeed();
            }
        });
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);
    }

    private void refreshFeed() {
        // Use SwingWorker to load data in background
        new SwingWorker<List<Post>, Void>() {
            @Override
            protected List<Post> doInBackground() throws Exception {
                // Reload user to get latest blocked list
                User refreshedUser = userService.getUserById(currentUser.getId());
                if (refreshedUser != null) {
                    currentUser = refreshedUser;
                }

                // Heavy DB call
                return postService.getFeedForUser(currentUser.getFollowing(), currentUser.getBlockedUsers());
            }

            @Override
            protected void done() {
                try {
                    List<Post> feedPosts = get();
                    updateFeedUI(feedPosts);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(FeedPanel.this, "Error loading feed: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void updateFeedUI(List<Post> feedPosts) {
        feedContainer.removeAll();

        if (feedPosts.isEmpty()) {
            JLabel noPostsLabel = new JLabel("No posts in feed. Follow users to see their posts.");
            noPostsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            feedContainer.add(noPostsLabel);
        } else {
            for (Post post : feedPosts) {
                feedContainer.add(new PostCardPanel(post, currentUser, postService, userService));
                feedContainer.add(Box.createVerticalStrut(10));
            }
        }
        feedContainer.revalidate();
        feedContainer.repaint();
    }

    private void startPolling() {
        // Polling removed to improve performance
    }

    private void refreshPostsData() {
        // Not used anymore
    }
}
