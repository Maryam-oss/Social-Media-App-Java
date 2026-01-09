package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;

import model.Notification;
import model.User;
import service.NotificationService;
import service.UserService;

public class NotificationsPanel extends JPanel {

    private final User currentUser;
    private final NotificationService notificationService;
    private final UserService userService;
    private JPanel notificationsContainer;

    public NotificationsPanel(User currentUser) {
        this.currentUser = currentUser;
        this.notificationService = new NotificationService();
        this.userService = new UserService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
//refresh bttn
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshNotifications();
            }
        });
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);
//main container
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        notificationsContainer.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(notificationsContainer);
        add(scrollPane, BorderLayout.CENTER);

        refreshNotifications();

    }

    private void refreshNotifications() {
        try {

            String userId = currentUser.getId();
            List<Notification> notifications = notificationService.getNotifications(userId);

            updateNotificationsUI(notifications);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNotificationsUI(List<Notification> notifications) {
        notificationsContainer.removeAll();

        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            notificationsContainer.add(Box.createVerticalStrut(10));
            notificationsContainer.add(emptyLabel);
        } else {
//sorting notifications
            Comparator<Notification> notificationComparator = new Comparator<Notification>() {
               //overiding 
                public int compare(Notification n1, Notification n2) {
                    long t1 = n1.getCreatedAt();
                    long t2 = n2.getCreatedAt();

                    if (t2 > t1) {
                        return 1;
                    } else if (t2 < t1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            };
            Collections.sort(notifications, notificationComparator);
            //sorts by time 

            for (Notification notif : notifications) {
                //container panel for this notification.
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                panel.setAlignmentX(Component.LEFT_ALIGNMENT);
//message and time
                JPanel textPanel = new JPanel();
                textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
                textPanel.setBackground(Color.WHITE);

                JLabel messageLabel = new JLabel(notif.getMessage());
                messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
                textPanel.add(messageLabel);
  //readable date string       
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                JLabel timeLabel = new JLabel(sdf.format(new Date(notif.getCreatedAt())));
                timeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
                textPanel.add(timeLabel);

                panel.add(textPanel, BorderLayout.CENTER);

                JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                actionsPanel.setBackground(Color.WHITE);

                if ("follow_request".equals(notif.getType())) {
                    JButton acceptBtn = new JButton("Accept");
                    acceptBtn.setBackground(new Color(46, 204, 113));
                    acceptBtn.setForeground(Color.WHITE);
                    acceptBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String currentUserId = currentUser.getId();
                            String relatedUserId = notif.getRelatedUserId();

                            boolean success = userService.approveFollowRequest(currentUserId, relatedUserId);

                            if (success) {
                                String notifId = notif.getNotificationId();
                                notificationService.deleteNotification(notifId);//removes from notif

                                refreshNotifications();
                                JOptionPane.showMessageDialog(NotificationsPanel.this, "Follow request accepted.");
                            } else {
                                JOptionPane.showMessageDialog(NotificationsPanel.this, "Failed to accept request.",
                                        "Error",JOptionPane.ERROR_MESSAGE);
                            }  }
                    });
                    actionsPanel.add(acceptBtn);
//decline bttn
                    JButton declineBtn = new JButton("Decline");
                    declineBtn.setBackground(new Color(231, 76, 60));
                    declineBtn.setForeground(Color.WHITE);
                    declineBtn.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String currentUserId = currentUser.getId();
                            String relatedUserId = notif.getRelatedUserId();

                            boolean success = userService.rejectFollowRequest(currentUserId, relatedUserId);

                            if (success) {
                                String notifId = notif.getNotificationId();
                                notificationService.deleteNotification(notifId);

                                refreshNotifications();
                                JOptionPane.showMessageDialog(NotificationsPanel.this, "Follow request declined.");
                            } else {
                                JOptionPane.showMessageDialog(NotificationsPanel.this, "Failed to decline request.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                    actionsPanel.add(declineBtn);
                }

                JButton deleteBtn = new JButton("Delete");
                deleteBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String notifId = notif.getNotificationId();
                        boolean deleted = notificationService.deleteNotification(notifId);

                        if (deleted) {
                            refreshNotifications();
                        } else {
                            JOptionPane.showMessageDialog(NotificationsPanel.this, "Failed to delete notification",
                                    "Error",JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
                actionsPanel.add(deleteBtn);

                panel.add(actionsPanel, BorderLayout.EAST);

                notificationsContainer.add(panel);
                notificationsContainer.add(Box.createVerticalStrut(5));
            }
        }

        notificationsContainer.revalidate();
        notificationsContainer.repaint();
    }

}
