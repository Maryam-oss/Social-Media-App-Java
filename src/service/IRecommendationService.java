package service;

import model.User;
import java.util.List;

public interface IRecommendationService {
    List<User> getRecommendedUsers(String userId, int limit);

}
