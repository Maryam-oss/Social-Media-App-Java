package service;

import model.Notification;
import java.util.*;

public interface INotificationService {

    Notification createNotification(String userId, String message, String type);

    Notification getNotificationById(String notificationId);

    List<Notification> getUserNotifications(String userId);

    boolean deleteNotification(String notificationId);

}
