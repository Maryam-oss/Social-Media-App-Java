package service;

import dao.FollowDAO;
import dao.DBConnection;
import model.User;

public class FollowService implements IFollowService {

    private static FollowService instance;

    private final FollowDAO followDAO;
    private final NotificationService notificationService;
    private final ActivityService activityService;
    private final dao.UserDAO userDAO;

    private FollowService() {
        // Use existing DB connection
        followDAO = new FollowDAO(DBConnection.getInstance());
        notificationService = new NotificationService();
        activityService = new ActivityService();
        userDAO = new dao.UserDAO();
    }

    public static FollowService getInstance() {
        if (instance == null)
            instance = new FollowService();
        return instance;
    }

    // Follow a user
    @Override
    public void follow(User follower, User target) {
        if (target.isPrivateAccount()) {
            boolean alreadyRequested = false;
            for (String reqId : target.getPendingFollowRequests()) {
                if (reqId.equals(follower.getId())) {
                    alreadyRequested = true;
                    break;
                }
            }

            if (!alreadyRequested) {
                // Update DB
                followDAO.addPendingRequest(target.getId(), follower.getId());

                // Update local objects
                target.addPendingFollowRequest(follower.getId());

                // Notify target
                String message = follower.getUsername() + " requested to follow you.";
                notificationService.createNotification(target.getId(), message, "follow_request", follower.getId());
            }
        } else {
            executeFollow(follower, target);
        }
    }

    private void executeFollow(User follower, User target) {
        // Update DB
        followDAO.followUser(follower.getId(), target.getId());

        // Update local objects
        follower.addFollowing(target.getId());
        target.addFollower(follower.getId());

        // Notify target
        String message = follower.getUsername() + " started following you!";
        notificationService.createNotification(target.getId(), message, "follow", follower.getId());

        // Create Activity
        activityService.createActivity(follower.getId(), "FOLLOWED", target.getId(), null, "Started following a user");
    }

    @Override
    public void acceptFollowRequest(User user, User follower) {
        boolean hasRequest = false;
        for (String reqId : user.getPendingFollowRequests()) {
            if (reqId.equals(follower.getId())) {
                hasRequest = true;
                break;
            }
        }

        if (hasRequest) {
            // Remove request
            followDAO.removePendingRequest(user.getId(), follower.getId());
            user.removePendingFollowRequest(follower.getId());

            // Execute follow
            executeFollow(follower, user);

            // Notify follower
            String message = user.getUsername() + " accepted your follow request.";
            notificationService.createNotification(follower.getId(), message, "follow_accept", user.getId());
        }
    }

    @Override
    public void declineFollowRequest(User user, User follower) {
        boolean hasRequest = false;
        for (String reqId : user.getPendingFollowRequests()) {
            if (reqId.equals(follower.getId())) {
                hasRequest = true;
                break;
            }
        }

        if (hasRequest) {
            // Remove request
            followDAO.removePendingRequest(user.getId(), follower.getId());
            user.removePendingFollowRequest(follower.getId());
        }
    }

    @Override
    public void cancelFollowRequest(User follower, User target) {
        boolean hasRequest = false;
        for (String reqId : target.getPendingFollowRequests()) {
            if (reqId.equals(follower.getId())) {
                hasRequest = true;
                break;
            }
        }

        if (hasRequest) {
            // Remove request
            followDAO.removePendingRequest(target.getId(), follower.getId());
            target.removePendingFollowRequest(follower.getId());
        }
    }

    // Unfollow a user
    @Override
    public void unfollow(User follower, User target) {
        // Update DB
        followDAO.unfollowUser(follower.getId(), target.getId());

        // Update local objects
        follower.removeFollowing(target.getId());
        target.removeFollower(follower.getId());
    }

    // Get the number of followers
    @Override
    public int getFollowersCount(User user) {
        return user.getFollowers().size();
    }

    // Get the number of people the user is following
    @Override
    public int getFollowingCount(User user) {
        return user.getFollowing().size();
    }

    @Override
    public boolean isFollowing(String followerId, String targetUserId) {
        User follower = userDAO.findById(followerId);
        if (follower != null) {
            boolean isFollowing = false;
            for (String followingId : follower.getFollowing()) {
                if (followingId.equals(targetUserId)) {
                    isFollowing = true;
                    break;
                }
            }
            return isFollowing;
        }
        return false;
    }
}
