package service;

import model.Comment;
import java.util.List;

public interface ICommentService {
    // Add a comment to a post
    boolean addComment(String userId, String postId, String text);

    // Get all comments for a post
    List<Comment> getPostComments(String postId);

    // Get a specific comment by ID
    Comment getCommentById(String commentId);

    // Update a comment
    boolean updateComment(String commentId, String newText);

    // Delete a comment
    boolean deleteComment(String commentId);

    // Get comment count for a post
    int getCommentCount(String postId);

    // Get comments by a specific user
    List<Comment> getCommentsByUser(String userId);

}
