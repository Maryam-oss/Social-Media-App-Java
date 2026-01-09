package dao;

import model.Post;
import java.util.List;

/**
 * Repository interface for Post data access
 * Provides abstraction over data persistence layer
 */
public interface IPostR {
    String create(Post post);

    Post findById(String id);

    List<Post> getAll();

    boolean update(Post post);

    boolean delete(String id);

    List<Post> getPostsByUserId(String userId);

    List<Post> getFeedPosts(List<String> followingList, List<String> blockedUsers);
}
