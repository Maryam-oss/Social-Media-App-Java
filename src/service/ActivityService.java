package service;

import model.Activity;
import dao.ActivityDAO;
import dao.IActR;
import model.User;
import java.util.*;
import java.time.*;

public class ActivityService implements IActivityService {
    private IActR activityRepository;
    private UserService userService;



    // constructor initializes dao and user service

    public ActivityService() {
        this.activityRepository = new ActivityDAO();
        this.userService = new UserService();
    }

        // create a new activity for a user
    public boolean createActivity(String userId, String activityType, String targetUserId, String targetPostId,
            String description) {
        try {
            if (userId == null || activityType == null) {
                return false;
            }

            Activity activity = new Activity();
            String type = activityType;

            String id = UUID.randomUUID().toString();
            activity.setId(id);

            activity.setUserId(userId);
            activity.setActivityType(type);
            activity.setTargetUserId(targetUserId);
            activity.setTargetPostId(targetPostId);
            activity.setDescription(description);

            LocalDateTime now = LocalDateTime.now();
            activity.setTimestamp(now);

            String resultId = activityRepository.create(activity);

            if (resultId != null) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

  //get all activities for a user
    public List<Activity> getUserActivityFeed(String userId) {
        try {
            ActivityDAO dao = (ActivityDAO) activityRepository;
            List<Activity> result = dao.getActivitiesByUserId(userId);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // get activities from users that the current user is following
    public List<Activity> getFollowingActivities(String userId, int limit) {
        try {

            UserService userService = new UserService();
            User user = userService.getUserProfile(userId);

            if (user == null) {
                return new ArrayList<Activity>();
            }

            List<String> following = user.getFollowing();
            if (following == null) {
                return new ArrayList<Activity>();
            }

            List<Activity> allActivities = new ArrayList<>();

            for (String followingUserId : following) {
                List<Activity> userActivities = getUserActivityFeed(followingUserId);
                allActivities.addAll(userActivities);
            }
 // sort activities by timestamp descending
            Comparator<Activity> activityComparator = new Comparator<Activity>() {
              
                public int compare(Activity a, Activity b) {
                    LocalDateTime timeA = a.getTimestamp();
                    LocalDateTime timeB = b.getTimestamp();

                    if (timeA == null || timeB == null)
                        return 0;

                    if (timeB.isAfter(timeA)) {
                        return 1;
                    } else if (timeB.isBefore(timeA)) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            };
            Collections.sort(allActivities, activityComparator);

            if (limit > 0 && allActivities.size() > limit) {
                List<Activity> subList = allActivities.subList(0, limit);
                return subList;
            }
            return allActivities;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Activity>();
        }
    }

    public Activity getActivityById(String activityId) {
        try {
            return activityRepository.findById(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean deleteActivity(String activityId) {
        try {
            return activityRepository.delete(activityId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
