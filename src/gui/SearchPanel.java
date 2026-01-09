package gui;

import javax.swing.*;
import model.User;
import service.UserService;
import service.FollowService;
import java.awt.*;
import java.util.*;
import java.util.List;

public class SearchPanel extends JPanel {

    private User currentUser;
    private UserService userService;
    private FollowService followService;
    private JTextField searchField;
    private JPanel resultsPanel;
    private JPanel suggestedPanel;

    public SearchPanel(User currentUser) {
        this.currentUser = currentUser;
        this.userService = new UserService();
        this.followService = FollowService.getInstance();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        // TABBED PANE
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        // Search Tab
        JPanel searchTab = createSearchTab();
        tabbedPane.addTab("Search", searchTab);

        // Suggested Followers Tab
        JPanel suggestedTab = createSuggestedTab();
        tabbedPane.addTab("Suggested", suggestedTab);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSearchTab() {
        JPanel searchTab = new JPanel(new BorderLayout());
        searchTab.setBackground(new Color(245, 245, 245));

        // SEARCH BAR
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                performSearch();
            }
        });

        searchPanel.add(new JLabel("Search Users:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        searchTab.add(searchPanel, BorderLayout.NORTH);

        // RESULTS PANEL
        resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        searchTab.add(scrollPane, BorderLayout.CENTER);

        return searchTab;
    }

    private JPanel createSuggestedTab() {
        JPanel suggestedTab = new JPanel(new BorderLayout());
        suggestedTab.setBackground(new Color(245, 245, 245));

        // HEADER
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel headerLabel = new JLabel("People you may know");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);

        suggestedTab.add(headerPanel, BorderLayout.NORTH);

        // SUGGESTED PANEL
        suggestedPanel = new JPanel();
        suggestedPanel.setLayout(new BoxLayout(suggestedPanel, BoxLayout.Y_AXIS));
        suggestedPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(suggestedPanel);
        suggestedTab.add(scrollPane, BorderLayout.CENTER);

        // Load suggested followers
        loadSuggestedFollowers();

        return suggestedTab;
    }

    private void loadSuggestedFollowers() {
        suggestedPanel.removeAll();

        List<User> suggestions = getSuggestedFollowers();

        if (suggestions.isEmpty()) {
            JLabel noSuggestions = new JLabel("No suggestions available");
            noSuggestions.setFont(new Font("Arial", Font.ITALIC, 12));
            noSuggestions.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            suggestedPanel.add(noSuggestions);
        } else {
            for (User user : suggestions) {
                suggestedPanel.add(new UserCardPanel(user, currentUser, followService));
                suggestedPanel.add(Box.createVerticalStrut(10));
            }
        }

        suggestedPanel.revalidate();
        suggestedPanel.repaint();
    }

    private List<User> getSuggestedFollowers() {
        Set<String> suggested = new HashSet<>();

        // Get all followers of the current user
        List<String> myFollowers = currentUser.getFollowers();

        // For each of my followers, get who they follow
        for (String followerId : myFollowers) {
            User follower = userService.getUserById(followerId);
            if (follower != null) {
                // Add people that my followers follow
                for (String theirFollowingId : follower.getFollowing()) {
                    // Don't suggest myself or people I already follow
                    if (!theirFollowingId.equals(currentUser.getId())
                            && !currentUser.getFollowing().contains(theirFollowingId)
                            && !currentUser.getBlockedUsers().contains(theirFollowingId)) {
                        suggested.add(theirFollowingId);
                    }
                }
            }
        }

        // Convert IDs to User objects
        List<User> suggestedUsers = new ArrayList<>();
        for (String userId : suggested) {
            User user = userService.getUserById(userId);
            if (user != null) {
                suggestedUsers.add(user);
            }
        }

        // Limit to 10 suggestions
        return suggestedUsers.size() > 10 ? suggestedUsers.subList(0, 10) : suggestedUsers;
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a username to search", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultsPanel.removeAll();
        List<User> results = userService.searchUsers(query);

        if (results.isEmpty()) {
            JLabel noResults = new JLabel("No users found");
            noResults.setFont(new Font("Arial", Font.ITALIC, 12));
            resultsPanel.add(noResults);
        } else {
            for (User user : results) {
                if (!user.getUsername().equals(currentUser.getUsername())) {
                    // Use the new UserCardPanel with follow/block functionality
                    resultsPanel.add(new UserCardPanel(user, currentUser, followService));
                    resultsPanel.add(Box.createVerticalStrut(10));
                }
            }
        }

        resultsPanel.revalidate();
        resultsPanel.repaint();
    }
}
