package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.User;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

// main frame of the social app gui
// handles navigation, sidebar, and different panels
public class MainAppFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebarPanel;

    private ArrayList<JButton> navButtons;
    private String currentCard = "Feed";

    // constructor, takes the logged in user
    public MainAppFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Social Media - " + currentUser.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        initComponents();
    }

    // initialize all gui components
    private void initComponents() {
        setLayout(new BorderLayout());

        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(ModernUI.PRIMARY_COLOR);
        sidebarPanel.setPreferredSize(new Dimension(220, getHeight()));
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel appTitle = new JLabel("Social App");
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        appTitle.setForeground(Color.WHITE);
        appTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(appTitle);
        sidebarPanel.add(Box.createVerticalStrut(40));

        navButtons = new ArrayList<JButton>();

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(ModernUI.BACKGROUND_COLOR);

        ProfilePanel profilePanel = new ProfilePanel(currentUser, currentUser);

        PostCreationListener listener = new PostCreationListener() {
            @Override
            public void onPostCreated(model.Post newPost) {
                profilePanel.addPostToProfile(newPost);
                switchTab("Profile");
            }
        };

        FeedPanel feedPanel = new FeedPanel(currentUser);
        addTab("Feed", feedPanel);

        addTab("Profile", profilePanel);

        CreatePostPanel createPostPanel = new CreatePostPanel(currentUser, listener);
        addTab("Create Post", createPostPanel);

        SearchPanel searchPanel = new SearchPanel(currentUser);
        addTab("Search", searchPanel);

        MessagesPanel messagesPanel = new MessagesPanel(currentUser);
        addTab("Messages", messagesPanel);

        NotificationsPanel notificationsPanel = new NotificationsPanel(currentUser);
        addTab("Notifications", notificationsPanel);

        ActivityPanel activityPanel = new ActivityPanel(currentUser);
        addTab("Activity", activityPanel);

        SettingsPanel settingsPanel = new SettingsPanel(currentUser, this);
        addTab("Settings", settingsPanel);

        sidebarPanel.add(Box.createVerticalGlue());
        JButton logoutBtn = createNavButton("Logout");
        logoutBtn.addActionListener(new ActionListener() {
        
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        sidebarPanel.add(logoutBtn);

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        switchTab("Feed");
    }

    // add a new tab and its  button to sidebar
    private void addTab(String name, JPanel panel) {
        contentPanel.add(panel, name);
        JButton btn = createNavButton(name);

        btn.setActionCommand(name);

        btn.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                switchTab(name);
            }
        });

        navButtons.add(btn);
        sidebarPanel.add(btn);
        sidebarPanel.add(Box.createVerticalStrut(10));
    }

    // switch between panels and update active button 
    private void switchTab(String name) {

        for (int i = 0; i < navButtons.size(); i++) {
            JButton btn = navButtons.get(i);
            if (btn.getActionCommand().equals(currentCard)) {
                styleNavButton(btn, false);
            }
        }

        currentCard = name;
        cardLayout.show(contentPanel, name);

        for (int i = 0; i < navButtons.size(); i++) {
            JButton btn = navButtons.get(i);
            if (btn.getActionCommand().equals(name)) {
                styleNavButton(btn, true);
            }
        }
    }

    // create a navigation button for sidebar
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(new Color(200, 200, 255));
        btn.setBackground(ModernUI.PRIMARY_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return btn;
    }

    // change button style depending on whether it is active or not
    private void styleNavButton(JButton btn, boolean active) {
        if (active) {
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        } else {
            btn.setForeground(new Color(200, 200, 255));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }
}
