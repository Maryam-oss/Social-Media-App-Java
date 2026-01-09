package dao;

import model.Activity;
import java.util.*;

public interface IActR {

    String create(Activity activity);

    Activity findById(String id);

    List<Activity> getAll();

    boolean update(Activity activity);

    boolean delete(String id);

    List<Activity> getActivitiesByUserId(String userId);

    List<Activity> getActivitiesByPostId(String postId);
}
