package service;

import model.User;

//Interface for Follow Service operations
//Provides abstraction for follow/unfollow functionality

public interface IFollowService {

    // Follow Operations
    void follow(User follower, User target);

    void unfollow(User follower, User target);

    // Follow Request Operations
    void acceptFollowRequest(User user, User follower);

    void declineFollowRequest(User user, User follower);

    void cancelFollowRequest(User follower, User target);

    // Follow Information
    boolean isFollowing(String followerId, String targetUserId);

    int getFollowersCount(User user);

    int getFollowingCount(User user);
}
