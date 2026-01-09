package gui;

import javax.swing.*;
import model.Post;
import model.User;
import model.Comment;
import service.PostService;
import service.UserService;
import java.awt.*;

public class PostCardPanel extends JPanel {
    private Post post;
    private User currentUser;
    private PostService postService;
    private UserService userService;

    // Helper class to manage media items
    private static class MediaItem {
        String id;
        boolean isVideo;

        MediaItem(String id, boolean isVideo) {
            this.id = id;
            this.isVideo = isVideo;
        }
    }

    public PostCardPanel(Post post, User currentUser, PostService postService, UserService userService) {
        this.post = post;
        this.currentUser = currentUser;
        this.postService = postService;
        this.userService = userService;

        setLayout(new BorderLayout());
        setBackground(ModernUI.CARD_BACKGROUND);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new javax.swing.border.EmptyBorder(15, 15, 15, 15)));
        setMaximumSize(new Dimension(500, 600));

        initComponents();
    }

    private void initComponents() {
        removeAll();

        // Header with author
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernUI.CARD_BACKGROUND);
        User author = userService.getUserById(post.getUserId());
        if (author != null) {
            JLabel authorLabel = new JLabel(author.getUsername());
            authorLabel.setFont(ModernUI.BOLD_FONT);
            authorLabel.setForeground(ModernUI.TEXT_COLOR);
            headerPanel.add(authorLabel, BorderLayout.WEST);
        }
        add(headerPanel, BorderLayout.NORTH);

        // Image/Video Panel
        JPanel mediaPanel = new JPanel(new BorderLayout());
        mediaPanel.setBackground(ModernUI.CARD_BACKGROUND);
        mediaPanel.setBorder(new javax.swing.border.EmptyBorder(10, 0, 10, 0));

        // Media Label (for images or video placeholder)
        JLabel mediaLabel = new JLabel();
        mediaLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Play Button (initially hidden)
        JButton playButton = new JButton("▶ Play Video");
        playButton.setFont(new Font("Arial", Font.BOLD, 14));
        playButton.setBackground(new Color(0, 0, 0, 150));
        playButton.setForeground(Color.WHITE);
        playButton.setFocusPainted(false);
        playButton.setBorderPainted(false);
        playButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        playButton.setVisible(false);

        // Layered Pane to overlay play button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(450, 350));

        mediaLabel.setBounds(0, 0, 450, 350);
        playButton.setBounds(150, 150, 150, 50); // Center button

        layeredPane.add(mediaLabel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(playButton, JLayeredPane.PALETTE_LAYER);

        // Prepare Media Items
        java.util.List<MediaItem> mediaItems = new java.util.ArrayList<>();

        if (post.getImageUrls() != null) {
            for (String url : post.getImageUrls()) {
                mediaItems.add(new MediaItem(url, false));
            }
        }

        if (post.getVideoUrls() != null) {
            for (String url : post.getVideoUrls()) {
                mediaItems.add(new MediaItem(url, true));
            }
        }

        // Fallback for legacy
        if (mediaItems.isEmpty() && post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            mediaItems.add(new MediaItem(post.getImageUrl(), false));
        }

        // Logic to display media
        if (!mediaItems.isEmpty()) {
            final int[] currentIndex = { 0 };

            // Function to update display
            Runnable updateDisplay = new Runnable() {
                @Override
                public void run() {
                    MediaItem currentItem = mediaItems.get(currentIndex[0]);

                    if (currentItem.isVideo) {
                        // Show video placeholder
                        mediaLabel.setIcon(ImageUtil.createVideoPlaceholder(450, 350));
                        mediaLabel.setText("");
                        playButton.setVisible(true);
                        playButton.putClientProperty("videoId", currentItem.id);
                    } else {
                        // Show image
                        mediaLabel.setIcon(ImageUtil.loadImageFromPath(currentItem.id, 450, 350));
                        mediaLabel.setText("");
                        playButton.setVisible(false);
                    }
                    layeredPane.repaint();
                }
            };

            // Initial display
            updateDisplay.run();

            // Play button action
            for (java.awt.event.ActionListener al : playButton.getActionListeners())
                playButton.removeActionListener(al);
            playButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    playButton.setEnabled(false);
                    playButton.setText("Wait...");

                    SwingWorker<Void, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            String videoId = (String) playButton.getClientProperty("videoId");
                            if (videoId != null) {
                                postService.playVideo(videoId);
                            }
                            return null;
                        }

                        @Override
                        protected void done() {
                            playButton.setEnabled(true);
                            playButton.setText("▶ Play Video");
                            try {
                                get(); // Check for exceptions
                            } catch (Exception ex) {
                                ex.printStackTrace(); // Log for debug
                                String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                                JOptionPane.showMessageDialog(PostCardPanel.this,
                                        "Error playing video: " + msg,
                                        "Playback Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };
                    worker.execute();
                }
            });

            // Navigation
            if (mediaItems.size() > 1) {
                JButton prevBtn = new JButton("<");
                JButton nextBtn = new JButton(">");

                styleNavButton(prevBtn);
                styleNavButton(nextBtn);

                prevBtn.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (currentIndex[0] > 0) {
                            currentIndex[0]--;
                            updateDisplay.run();
                        }
                    }
                });

                nextBtn.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        if (currentIndex[0] < mediaItems.size() - 1) {
                            currentIndex[0]++;
                            updateDisplay.run();
                        }
                    }
                });

                mediaPanel.add(prevBtn, BorderLayout.WEST);
                mediaPanel.add(nextBtn, BorderLayout.EAST);
            }
        } else if (post.getImageUrl() != null) {
            // Fallback for old posts
            ImageIcon image = ImageUtil.loadImageFromPath(post.getImageUrl(), 450, 350);
            mediaLabel.setIcon(image);
            playButton.setVisible(false);
        }

        mediaPanel.add(layeredPane, BorderLayout.CENTER);
        add(mediaPanel, BorderLayout.CENTER);

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(ModernUI.CARD_BACKGROUND);

        // Stats
        int commentCount = postService.getCommentCount(post.getId());
        JLabel statsLabel = new JLabel(post.getLikes().size() + " likes | " + commentCount + " comments");
        statsLabel.setFont(ModernUI.SMALL_FONT);
        statsLabel.setForeground(ModernUI.SUBTEXT_COLOR);
        footerPanel.add(statsLabel);
        footerPanel.add(Box.createVerticalStrut(5));

        // Caption
        JLabel captionLabel = new JLabel(post.getCaption());
        captionLabel.setFont(ModernUI.BODY_FONT);
        footerPanel.add(captionLabel);
        footerPanel.add(Box.createVerticalStrut(10));

        // Comments
        if (!post.getComments().isEmpty()) {
            for (Comment comment : post.getComments()) {
                User commenter = userService.getUserById(comment.getUserId());
                String commenterName = commenter != null ? commenter.getUsername() : "Unknown";

                JPanel commentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                commentPanel.setBackground(ModernUI.CARD_BACKGROUND);

                // Comment content
                JLabel commentLabel = new JLabel(
                        "<html><b>" + commenterName + "</b>: " + comment.getText() + "</html>");
                commentLabel.setFont(ModernUI.SMALL_FONT);
                commentPanel.add(commentLabel);

                // Add Edit/Delete buttons if owner
                if (comment.getUserId().equals(currentUser.getId())) {
                    JButton editCommentBtn = new JButton("✎");
                    editCommentBtn.setToolTipText("Edit Comment");
                    editCommentBtn.setMargin(new Insets(0, 2, 0, 2));
                    editCommentBtn.setBorderPainted(false);
                    editCommentBtn.setContentAreaFilled(false);
                    editCommentBtn.setForeground(Color.GRAY);
                    editCommentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    editCommentBtn.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            String newText = JOptionPane.showInputDialog(PostCardPanel.this, "Edit comment:",
                                    comment.getText());
                            if (newText != null && !newText.trim().isEmpty() && !newText.equals(comment.getText())) {
                                if (postService.updateComment(comment.getId(), newText.trim())) {
                                    refresh();
                                } else {
                                    JOptionPane.showMessageDialog(PostCardPanel.this, "Failed to update comment.");
                                }
                            }
                        }
                    });

                    JButton deleteCommentBtn = new JButton("x");
                    deleteCommentBtn.setToolTipText("Delete Comment");
                    deleteCommentBtn.setMargin(new Insets(0, 2, 0, 2));
                    deleteCommentBtn.setBorderPainted(false);
                    deleteCommentBtn.setContentAreaFilled(false);
                    deleteCommentBtn.setForeground(Color.RED);
                    deleteCommentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    deleteCommentBtn.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            int choice = JOptionPane.showConfirmDialog(PostCardPanel.this, "Delete this comment?",
                                    "Confirm",
                                    JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                if (postService.deleteComment(comment.getId())) {
                                    refresh();
                                } else {
                                    JOptionPane.showMessageDialog(PostCardPanel.this, "Failed to delete comment.");
                                }
                            }
                        }
                    });

                    commentPanel.add(editCommentBtn);
                    commentPanel.add(deleteCommentBtn);
                }

                footerPanel.add(commentPanel);
            }
        }

        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        actionPanel.setBackground(ModernUI.CARD_BACKGROUND);

        // Like/Unlike button
        boolean tempHasLiked = false;
        for (model.Like l : post.getLikes()) {
            if (l.getUserId().equals(currentUser.getId())) {
                tempHasLiked = true;
                break;
            }
        }
        final boolean hasLiked = tempHasLiked;
        JButton likeButton = createActionButton(hasLiked ? "Unlike" : "Like",
                hasLiked ? ModernUI.ERROR_COLOR : ModernUI.PRIMARY_COLOR);
        likeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (hasLiked) {
                    postService.removeLike(post.getId(), currentUser.getId());
                } else {
                    postService.addLike(post.getId(), currentUser.getId(), post.getUserId());
                }
                refresh();
            }
        });
        actionPanel.add(likeButton);
        actionPanel.add(Box.createHorizontalStrut(10));

        // Comment button
        JButton commentButton = createActionButton("Comment", ModernUI.SECONDARY_COLOR);
        commentButton.setForeground(ModernUI.TEXT_COLOR);
        commentButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                openCommentDialog();
            }
        });
        actionPanel.add(commentButton);
        actionPanel.add(Box.createHorizontalStrut(10));

        // Delete post button (only for post owner)
        if (post.getUserId().equals(currentUser.getId())) {
            JButton editButton = createActionButton("Edit", ModernUI.SECONDARY_COLOR);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    editCaption();
                }
            });
            actionPanel.add(editButton);
            actionPanel.add(Box.createHorizontalStrut(10));

            JButton deleteButton = createActionButton("Delete", ModernUI.ERROR_COLOR);
            deleteButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    deletePost();
                }
            });
            actionPanel.add(deleteButton);
        }

        footerPanel.add(actionPanel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(ModernUI.SMALL_FONT);
        btn.setBackground(color);
        btn.setForeground(color == ModernUI.SECONDARY_COLOR ? ModernUI.TEXT_COLOR : Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(80, 30));
        return btn;
    }

    private void openCommentDialog() {
        String comment = JOptionPane.showInputDialog(this, "Enter your comment:", "");
        if (comment != null && !comment.trim().isEmpty()) {
            postService.addComment(currentUser.getId(), post.getId(), comment);
            refresh();
        }
    }

    private void editCaption() {
        String newCaption = (String) JOptionPane.showInputDialog(
                this,
                "Edit Caption:",
                "Edit Post",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                post.getCaption());

        if (newCaption != null && !newCaption.equals(post.getCaption())) {
            if (postService.updateCaption(post.getId(), newCaption)) {
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update caption.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deletePost() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this post?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (postService.deletePost(post.getId())) {
                JOptionPane.showMessageDialog(this, "Post deleted successfully!");
                Container parent = this.getParent();
                if (parent != null) {
                    parent.remove(this);
                    parent.revalidate();
                    parent.repaint();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete post.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refresh() {
        Post updatedPost = postService.getPostById(post.getId());
        if (updatedPost != null) {
            this.post = updatedPost;
            removeAll();
            initComponents();
            revalidate();
            repaint();
        }
    }

    private void styleNavButton(JButton btn) {
        btn.setBackground(new Color(0, 0, 0, 100));
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
