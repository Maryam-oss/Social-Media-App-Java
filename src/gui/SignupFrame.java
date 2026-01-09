package gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import service.UserService;
import service.ValidationUtil;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SignupFrame extends JFrame {
    private JTextField usernameField, emailField, fullNameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> genderCombo, dayCombo, monthCombo, yearCombo;
    private JButton signupButton, backButton;
    private UserService userService;
    private JFrame loginFrame;

    public SignupFrame(JFrame loginFrame) {
        this.loginFrame = loginFrame;
        this.userService = new UserService();
        setTitle("Social Media - Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700); // Wider for split view
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridLayout(1, 2));

        // Left Side - Branding
        JPanel brandPanel = ModernUI.createBrandPanel();
        add(brandPanel);

        // Right Side - Signup Form
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title
        JLabel titleLabel = ModernUI.createHeaderLabel("Create Account");
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Fields
        addField(contentPanel, "Username", usernameField = ModernUI.createTextField());
        addField(contentPanel, "Email", emailField = ModernUI.createTextField());
        addField(contentPanel, "Full Name", fullNameField = ModernUI.createTextField());

        // Gender
        contentPanel.add(ModernUI.createLabel("Gender"));
        contentPanel.add(Box.createVerticalStrut(5));
        genderCombo = new JComboBox<>(new String[] { "Male", "Female", "Other" });
        genderCombo.setFont(ModernUI.BODY_FONT);
        genderCombo.setBackground(Color.WHITE);
        genderCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        genderCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(genderCombo);
        contentPanel.add(Box.createVerticalStrut(15));

        // Date of Birth
        contentPanel.add(ModernUI.createLabel("Date of Birth"));
        contentPanel.add(Box.createVerticalStrut(5));
        JPanel dobPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        dobPanel.setBackground(Color.WHITE);
        dobPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dobPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        dayCombo = new JComboBox<>(getDays());
        monthCombo = new JComboBox<>(getMonths());
        yearCombo = new JComboBox<>(getYears());

        styleComboBox(dayCombo);
        styleComboBox(monthCombo);
        styleComboBox(yearCombo);

        dobPanel.add(dayCombo);
        dobPanel.add(monthCombo);
        dobPanel.add(yearCombo);
        contentPanel.add(dobPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        // Password
        addField(contentPanel, "Password", passwordField = ModernUI.createPasswordField());
        addField(contentPanel, "Confirm Password", confirmPasswordField = ModernUI.createPasswordField());

        contentPanel.add(Box.createVerticalStrut(20));

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        backButton = ModernUI.createSecondaryButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });

        signupButton = ModernUI.createPrimaryButton("Sign Up");
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSignup();
            }
        });

        buttonPanel.add(backButton);
        buttonPanel.add(signupButton);
        contentPanel.add(buttonPanel);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        formWrapper.add(scrollPane, BorderLayout.CENTER);
        add(formWrapper);
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = ModernUI.createLabel(labelText);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(ModernUI.BODY_FONT);
        box.setBackground(Color.WHITE);
    }

    private void handleSignup() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String fullName = fullNameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String gender = (String) genderCombo.getSelectedItem();
        String day = (String) dayCombo.getSelectedItem();
        String month = (String) monthCombo.getSelectedItem();
        String year = (String) yearCombo.getSelectedItem();
        String dob = day + "/" + month + "/" + year;

        if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidUsername(username)) {
            JOptionPane.showMessageDialog(this, "Invalid username format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!ValidationUtil.isValidPassword(password)) {
            JOptionPane.showMessageDialog(this, ValidationUtil.getPasswordRequirements(), "Invalid Password",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userService.signup(username, email, password, fullName, gender, dob)) {
            JOptionPane.showMessageDialog(this, "Account created successfully! Please login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            loginFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists or signup failed", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBack() {
        dispose();
        loginFrame.setVisible(true);
    }

    private String[] getDays() {
        String[] days = new String[31];
        for (int i = 0; i < 31; i++)
            days[i] = String.valueOf(i + 1);
        return days;
    }

    private String[] getMonths() {
        return new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
    }

    private String[] getYears() {
        String[] years = new String[100];
        int startYear = 1924;
        for (int i = 0; i < 100; i++)
            years[i] = String.valueOf(startYear + i);
        return years;
    }
}
