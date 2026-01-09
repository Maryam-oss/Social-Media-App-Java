package dao;

import model.Notification;
import java.util.*;

public interface INotifR {
    String create(Notification notification);

    Notification findById(String id);

    List<Notification> getAll();

    boolean update(Notification notification);

    boolean delete(String id);

    List<Notification> getUserNotifications(String userId);

}
