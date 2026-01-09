package gui;

import javax.swing.*;
import model.User;
import model.Post;
import service.PostService;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class CreatePostPanel extends JPanel {
    private User currentUser;
    private PostService postService;
    private JTextArea captionArea;
    private JLabel imagePathLabel;
    private java.util.List<String> selectedImagePaths; // Changed to List
    private java.util.List<String> selectedVideoPaths;

    private PostCreationListener postCreationListener;

    public CreatePostPanel(User currentUser, PostCreationListener listener) {
        this.currentUser = currentUser;
        this.postCreationListener = listener;
        this.postService = new PostService();
        this.selectedImagePaths = new java.util.ArrayList<>();
        this.selectedVideoPaths = new java.util.ArrayList<>();
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(new Color(245, 245, 245));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Create New Post");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Caption
        formPanel.add(new JLabel("Caption:"));
        captionArea = new JTextArea(5, 30);
        captionArea.setLineWrap(true);
        captionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(captionArea));
        formPanel.add(Box.createVerticalStrut(10));

        // Image selection
        formPanel.add(new JLabel("Media (Images/Videos):"));
        imagePathLabel = new JLabel("No media selected");
        formPanel.add(imagePathLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton selectImageButton = new JButton("Add Media");
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectMedia();
            }
        });
        buttonPanel.add(selectImageButton);

        JButton clearImagesButton = new JButton("Clear Media");
        clearImagesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearMedia();
            }
        });
        buttonPanel.add(clearImagesButton);

        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Post button
        JButton postButton = new JButton("Post");
        postButton.setBackground(new Color(51, 102, 255));
        postButton.setForeground(Color.WHITE);
        postButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handlePost();
            }
        });
        formPanel.add(postButton);

        add(formPanel, BorderLayout.NORTH);
    }

    private void selectMedia() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true); // Enable multi-selection
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images & Videos", "jpg", "jpeg",
                "png", "gif", "mp4", "avi", "mov", "mkv"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".mp4") || name.endsWith(".avi") || name.endsWith(".mov") || name.endsWith(".mkv")) {
                    selectedVideoPaths.add(file.getAbsolutePath());
                } else {
                    selectedImagePaths.add(file.getAbsolutePath());
                }
            }
            updateMediaLabel();
        }
    }

    private void clearMedia() {
        selectedImagePaths.clear();
        selectedVideoPaths.clear();
        updateMediaLabel();
    }

    private void updateMediaLabel() {
        if (selectedImagePaths.isEmpty() && selectedVideoPaths.isEmpty()) {
            imagePathLabel.setText("No media selected");
        } else {
            imagePathLabel.setText(
                    selectedImagePaths.size() + " image(s), " + selectedVideoPaths.size() + " video(s) selected");
        }
    }

    private void handlePost() {
        String caption = captionArea.getText();

        if (selectedImagePaths.isEmpty() && selectedVideoPaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one image or video", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Use the new service method that accepts list of images and videos
            Post newPost = postService.createPost(currentUser.getId(), caption, selectedImagePaths, selectedVideoPaths);

            if (postCreationListener != null) {
                postCreationListener.onPostCreated(newPost);
            }
            JOptionPane.showMessageDialog(this, "Post created successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            captionArea.setText("");
            clearMedia();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating post: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
