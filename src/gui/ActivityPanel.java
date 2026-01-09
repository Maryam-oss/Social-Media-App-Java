package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.*;
import java.util.*;
import java.util.List;
import java.time.*;

import model.Activity;
import model.User;
import service.ActivityService;

public class ActivityPanel extends JPanel {

    private final User currentUser;
    private final ActivityService activityService;
    private JPanel activitiesContainer;

    // initializes panel and loads activities
    public ActivityPanel(User currentUser) {
        this.currentUser = currentUser;
        this.activityService = new ActivityService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // top panel with refresh button
        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                refreshActivities();
            }
        });
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);

        // container to hold activity cards
        activitiesContainer = new JPanel();
        activitiesContainer.setLayout(new BoxLayout(activitiesContainer, BoxLayout.Y_AXIS));
        activitiesContainer.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(activitiesContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // initially load activities
        refreshActivities();
    }

    // refresh all activities from service
    private void refreshActivities() {
        try {
            String userId = currentUser.getId();
            List<Activity> activities = activityService.getUserActivityFeed(userId);

            updateActivitiesUI(activities);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update the ui with activity cards
    private void updateActivitiesUI(List<Activity> activities) {
        activitiesContainer.removeAll();

        if (activities.isEmpty()) {
            JLabel emptyLabel = new JLabel("no recent activity");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            activitiesContainer.add(Box.createVerticalStrut(20));
            activitiesContainer.add(emptyLabel);
        } else {

            // sort activities by timestamp descending
            Comparator<Activity> activityComparator = new Comparator<Activity>() {
      
                public int compare(Activity a1, Activity a2) {
                    long t1 = getTimestampMillis(a1);
                    long t2 = getTimestampMillis(a2);

                    if (t2 > t1) {
                        return 1;
                    } else if (t2 < t1) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            };
            Collections.sort(activities, activityComparator);

            // create a card for each activity
            for (Activity activity : activities) {
                JPanel card = createActivityCard(activity);
                activitiesContainer.add(card);
                activitiesContainer.add(Box.createVerticalStrut(10));
            }
        }

        activitiesContainer.revalidate();
        activitiesContainer.repaint();
    }

    //  activity timestamp to milliseconds for sorting
    private long getTimestampMillis(Activity a) {
        try {
            LocalDateTime localDateTime = a.getTimestamp();
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            Date date = Date.from(zonedDateTime.toInstant());
            long millis = date.getTime();
            return millis;
        } catch (Exception e) {
            return 0;
        }
    }

    // create ui card for a single activity
    private JPanel createActivityCard(Activity activity) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // icon for activity type
        JLabel typeLabel = new JLabel(getActivityIcon(activity.getActivityType()));
        typeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        typeLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        card.add(typeLabel, BorderLayout.WEST);

        // text and time
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);

        String text = formatActivityText(activity);
        JLabel messageLabel = new JLabel("<html>" + text + "</html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(messageLabel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        String formattedDate = activity.getTimestamp().format(formatter);
        JLabel timeLabel = new JLabel(formattedDate);
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        timeLabel.setForeground(Color.GRAY);
        contentPanel.add(timeLabel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    // get emoji based on activity type
    private String getActivityIcon(String type) {
        switch (type) {
            case "LIKED":
                return "💗";
            case "COMMENTED":
                return "💬";
            case "POSTED":
                return "📝";
            case "FOLLOWED":
                return "👤";
            case "REPLIED":
                return "↩️";
            default:
                return "•";
        }
    }

    // readable text for activity type
    private String formatActivityText(Activity activity) {
        String desc = activity.getDescription();
        switch (activity.getActivityType()) {
            case "LIKED":
                return "you liked a post.";
            case "COMMENTED":
                return "you commented on a post.";
            case "POSTED":
                return "you created a new post.";
            case "FOLLOWED":
                return "you started following a user.";
            case "REPLIED":
                return "you replied to a comment.";
            default:
                return desc != null ? desc : "unknown activity";
        }
    }
}
