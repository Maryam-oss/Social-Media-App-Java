package service;

import model.User;
import model.Post;
import dao.UserDAO;
import dao.PostDAO;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class RecommendationService implements IRecommendationService {
    private UserService userService;

    public RecommendationService() {
        this.userService = new UserService();
    }

    @Override
    public List<User> getRecommendedUsers(String userId, int limit) {
        try {
            User currentUser = userService.getUserProfile(userId);
            if (currentUser == null) {
                return List.of();
            }

            UserDAO userDAO = new UserDAO();
            List<User> allUsers = userDAO.getAll();

            // Filter out: self, already following, already followers
            List<User> recommended = new java.util.ArrayList<>();

            for (User u : allUsers) {
                if (!u.getId().equals(userId) &&
                        !currentUser.getFollowing().contains(u.getId()) &&
                        !currentUser.getFollowers().contains(u.getId())) {
                    recommended.add(u);
                }
            }

            java.util.Collections.sort(recommended, new java.util.Comparator<User>() {
                @Override
                public int compare(User a, User b) {
                    int mutualA = 0;
                    for (String f : currentUser.getFollowing()) {
                        if (a.getFollowers().contains(f)) {
                            mutualA++;
                        }
                    }
                    int mutualB = 0;
                    for (String f : currentUser.getFollowing()) {
                        if (b.getFollowers().contains(f)) {
                            mutualB++;
                        }
                    }
                    return Integer.compare(mutualB, mutualA);
                }
            });

            if (recommended.size() > limit) {
                return recommended.subList(0, limit);
            }
            return recommended;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

}
