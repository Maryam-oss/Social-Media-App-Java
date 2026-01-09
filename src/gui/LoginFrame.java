package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.*;
import model.User;
import service.UserService;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserService userService;
    private MainAppFrame mainAppFrame;

    public LoginFrame() {
        this.userService = new UserService();
        setTitle("Social Media - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600); // Wider aspect ratio for split view
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 2));
        add(ModernUI.createBrandPanel()); // Left Side
        add(createLoginFormPanel()); // Right Side
    }

    private JPanel createLoginFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setPreferredSize(new Dimension(300, 400));

        // Add UI Components
        addHeader(contentPanel);
        addFields(contentPanel);
        addButtons(contentPanel);

        formPanel.add(contentPanel);
        return formPanel;
    }

    private void addHeader(JPanel panel) {
        JLabel title = ModernUI.createHeaderLabel("Welcome Back");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subTitle = ModernUI.createLabel("Please enter your details.");
        subTitle.setForeground(ModernUI.SUBTEXT_COLOR);
        subTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(subTitle);
        panel.add(Box.createVerticalStrut(40));
    }

    private void addFields(JPanel panel) {
        // Username
        panel.add(createLabeledField("Username", usernameField = ModernUI.createTextField()));
        panel.add(Box.createVerticalStrut(20));

        // Password
        panel.add(createLabeledField("Password", passwordField = ModernUI.createPasswordField()));
        panel.add(Box.createVerticalStrut(40));
    }

    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = ModernUI.createLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        return panel;
    }

    private void addButtons(JPanel panel) {
        loginButton = ModernUI.createPrimaryButton("Login");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createSignupLink());
    }

    private JPanel createSignupLink() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel text = new JLabel("Don't have an account? ");
        text.setFont(ModernUI.BODY_FONT);

        JButton link = new JButton("Sign up");
        link.setFont(ModernUI.BOLD_FONT);
        link.setForeground(ModernUI.PRIMARY_COLOR);
        link.setBorderPainted(false);
        link.setContentAreaFilled(false);
        link.setCursor(new Cursor(Cursor.HAND_CURSOR));
        link.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignupFrame();
            }
        });

        panel.add(text);
        panel.add(link);
        return panel;
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.login(username, password);
        if (user != null) {
            dispose();
            mainAppFrame = new MainAppFrame(user);
            mainAppFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openSignupFrame() {
        dispose();
        SignupFrame signupFrame = new SignupFrame(this);
        signupFrame.setVisible(true);
    }
}
