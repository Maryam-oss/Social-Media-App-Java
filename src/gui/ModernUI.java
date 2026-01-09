package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class ModernUI {
    // fixed these so they can be used all over
    public static final Color BACKGROUND_COLOR = new Color(250, 250, 250);//grey
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color PRIMARY_COLOR = new Color(51, 102, 255);//blue
    public static final Color SECONDARY_COLOR = new Color(230, 230, 230); //l gray
    public static final Color TEXT_COLOR = new Color(50, 50, 50);
    public static final Color SUBTEXT_COLOR = new Color(120, 120, 120);
    public static final Color ERROR_COLOR = new Color(231, 76, 60);//red

    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public static JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BOLD_FONT);
        btn.setForeground(TEXT_COLOR);
        btn.setBackground(SECONDARY_COLOR);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        return btn;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(BODY_FONT);
        Color borderColor = new Color(200, 200, 200);
        Border lineBorder = BorderFactory.createLineBorder(borderColor);
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 15, 10, 15);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);
        field.setBorder(compoundBorder);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(BODY_FONT);
        Color borderColor = new Color(200, 200, 200);
        Border lineBorder = BorderFactory.createLineBorder(borderColor);
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 15, 10, 15);
        Border compoundBorder = BorderFactory.createCompoundBorder(lineBorder, emptyBorder);
        field.setBorder(compoundBorder);
        return field;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    public static JPanel createBrandPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_COLOR);
        panel.setLayout(new GridBagLayout());

        JLabel title = new JLabel("Social App");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Connect with friends");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitle.setForeground(new Color(220, 220, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(title, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(subtitle, gbc);

        return panel;
    }
}
