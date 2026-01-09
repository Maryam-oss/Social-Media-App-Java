package service;

import model.Comment;
import dao.CommentDAO;
import dao.IComR;
import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

public class CommentService implements ICommentService {
    private IComR commentRepository;
    private ActivityService activityService;

    public CommentService() {
        this.commentRepository = new CommentDAO();
        this.activityService = new ActivityService();
    }

    @Override
    public boolean addComment(String userId, String postId, String text) {
        try {
            if (userId == null || postId == null || text == null || text.trim().isEmpty()) {
                return false;
            }

            Comment comment = new Comment();
            comment.setId(UUID.randomUUID().toString());
            comment.setUserId(userId);
            comment.setPostId(postId);
            comment.setText(text);
            comment.setCreatedAt(LocalDateTime.now());
            comment.setUpdatedAt(LocalDateTime.now());
            // taggedUserIds removed

            String id = commentRepository.create(comment);

            if (id != null) {
                // Create Activity
                activityService.createActivity(userId, "COMMENTED", null, postId, "Commented on a post");
            }
            return id != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Comment> getPostComments(String postId) {
        try {
            return ((CommentDAO) commentRepository).getCommentsByPostId(postId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public Comment getCommentById(String commentId) {
        try {
            return commentRepository.findById(commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getCommentCount(String postId) {
        try {
            return getPostComments(postId).size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<Comment> getCommentsByUser(String userId) {
        try {
            return ((CommentDAO) commentRepository).getCommentsByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean updateComment(String commentId, String newText) {
        try {
            Comment comment = commentRepository.findById(commentId);
            if (comment == null)
                return false;

            comment.setText(newText);
            comment.setUpdatedAt(LocalDateTime.now());
            return commentRepository.update(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteComment(String commentId) {
        try {
            return commentRepository.delete(commentId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
