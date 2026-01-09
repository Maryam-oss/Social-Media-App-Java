package service;

import model.Activity;
import java.util.*;

public interface IActivityService {

    boolean createActivity(String userId, String activityType, String targetUserId, String targetPostId,
            String description);

    List<Activity> getUserActivityFeed(String userId);

    List<Activity> getFollowingActivities(String userId, int limit);

    Activity getActivityById(String activityId);

    boolean deleteActivity(String activityId);

}
