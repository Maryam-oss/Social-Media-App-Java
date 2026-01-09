package gui;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import model.Message;
import model.User;
import service.MessageService;
import service.UserService;

public class MessagesPanel extends JPanel {

    private final User currentUser;
    private final MessageService messageService;
    private final UserService userService;
    private final service.GroupService groupService; // Add GroupService

    private JComboBox<Object> conversationCombo; // Changed to Object to hold User or Group
    private JPanel chatPanel;
    private JTextField messageField;
    private String selectedDetailsId; // Could be UserId or GroupId
    private boolean isGroupSelected = false;
    private JButton addMemberBtn;
    private JButton viewMembersBtn;
    private JButton editGroupBtn;
    private JButton deleteGroupBtn;

    public MessagesPanel(User currentUser) {
        this.currentUser = currentUser;
        this.messageService = new MessageService();
        this.userService = new UserService();
        this.groupService = new service.GroupService();

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        initComponents();
        initRealTimeListener(); // Start the real-time listener
    }

    private void initComponents() {
        // Chat display panel
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // Top panel: Selection + Create Group
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Selection Combo
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(Color.WHITE);
        selectionPanel.add(new JLabel("To:"));

        conversationCombo = new JComboBox<>();
        conversationCombo.setPreferredSize(new Dimension(200, 30));
        // Custom renderer
        conversationCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User) {
                    setText(((User) value).getUsername());
                    setIcon(null); // Could add user icon
                } else if (value instanceof model.Group) {
                    setText("[Group] " + ((model.Group) value).getName());
                    setForeground(new Color(0, 102, 204));
                }
                return this;
            }
        });
        selectionPanel.add(conversationCombo);
        topPanel.add(selectionPanel, BorderLayout.CENTER);

        // Create Group Button
        JButton createGroupBtn = new JButton("+ Group");
        createGroupBtn.setBackground(new Color(40, 167, 69));
        createGroupBtn.setForeground(Color.WHITE);
        createGroupBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                createGroup();
            }
        });

        // Add Member Button
        addMemberBtn = new JButton("+ Member");
        addMemberBtn.setBackground(new Color(0, 123, 255));
        addMemberBtn.setForeground(Color.WHITE);
        addMemberBtn.setVisible(false); // Initially hidden
        addMemberBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                addMember();
            }
        });

        // View Members Button
        viewMembersBtn = new JButton("Members");
        viewMembersBtn.setBackground(new Color(23, 162, 184)); // Info color
        viewMembersBtn.setForeground(Color.WHITE);
        viewMembersBtn.setVisible(false); // Initially hidden
        viewMembersBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                viewMembers();
            }
        });

        // Edit Group Button
        editGroupBtn = new JButton("Edit");
        editGroupBtn.setBackground(new Color(255, 193, 7)); // Warning color
        editGroupBtn.setForeground(Color.BLACK);
        editGroupBtn.setVisible(false);
        editGroupBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                editGroup();
            }
        });

        // Delete Group Button
        deleteGroupBtn = new JButton("Delete");
        deleteGroupBtn.setBackground(new Color(220, 53, 69)); // Danger color
        deleteGroupBtn.setForeground(Color.WHITE);
        deleteGroupBtn.setVisible(false);
        deleteGroupBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                deleteGroup();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewMembersBtn);
        buttonPanel.add(addMemberBtn);
        buttonPanel.add(editGroupBtn);
        buttonPanel.add(deleteGroupBtn);
        buttonPanel.add(createGroupBtn);

        topPanel.add(buttonPanel, BorderLayout.EAST);

        loadConversations();
        conversationCombo.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                loadConversation();
            }
        });

        add(topPanel, BorderLayout.NORTH);

        // Message input panel
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        messageField = new JTextField(30);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                sendMessage();
            }
        });

        bottomPanel.add(messageField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void viewMembers() {
        if (!isGroupSelected || selectedDetailsId == null)
            return;

        model.Group currentGroup = groupService.getGroupById(selectedDetailsId);
        if (currentGroup == null)
            return;

        List<String> memberIds = currentGroup.getMemberIds();
        StringBuilder membersList = new StringBuilder("Group Members:\n\n");

        for (String memberId : memberIds) {
            User user = userService.getUserById(memberId);
            if (user != null) {
                membersList.append("- ").append(user.getUsername());
                if (currentGroup.getOwnerId().equals(user.getId())) {
                    membersList.append(" (Owner)");
                }
                membersList.append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, membersList.toString(), "Group Members", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addMember() {
        if (!isGroupSelected || selectedDetailsId == null)
            return;

        // Get all users
        List<User> allUsers = userService.getAllUsers();
        model.Group currentGroup = groupService.getGroupById(selectedDetailsId);

        // Filter out existing members
        List<User> eligibleUsers = new java.util.ArrayList<>();
        for (User u : allUsers) {
            if (!currentGroup.getMemberIds().contains(u.getId())) {
                eligibleUsers.add(u);
            }
        }

        if (eligibleUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No more users to add!");
            return;
        }

        // Show selection dialog
        User[] usersArray = eligibleUsers.toArray(new User[0]);
        User selectedUser = (User) JOptionPane.showInputDialog(
                this,
                "Select user to add:",
                "Add Member",
                JOptionPane.QUESTION_MESSAGE,
                null,
                usersArray,
                usersArray[0]);

        if (selectedUser != null) {
            if (groupService.addMemberToGroup(currentGroup.getId(), selectedUser.getId())) {
                JOptionPane.showMessageDialog(this, selectedUser.getUsername() + " added to group!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add member.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void createGroup() {
        String groupName = JOptionPane.showInputDialog(this, "Enter Group Name:");
        if (groupName != null && !groupName.trim().isEmpty()) {
            model.Group newGroup = groupService.createGroup(groupName, currentUser.getId(),
                    java.util.Collections.singletonList(currentUser.getId()));
            if (newGroup != null) {
                JOptionPane.showMessageDialog(this, "Group '" + groupName + "' created!");
                loadConversations();
            }
        }
    }

    private void editGroup() {
        if (!isGroupSelected || selectedDetailsId == null)
            return;

        model.Group currentGroup = groupService.getGroupById(selectedDetailsId);
        if (currentGroup == null)
            return;

        String newName = JOptionPane.showInputDialog(this, "Enter New Group Name:", currentGroup.getName());
        if (newName != null && !newName.trim().isEmpty() && !newName.equals(currentGroup.getName())) {
            if (groupService.updateGroupName(currentGroup.getId(), newName)) {
                JOptionPane.showMessageDialog(this, "Group renamed successfully!");
                loadConversations(); // Refresh list to show new name
            } else {
                JOptionPane.showMessageDialog(this, "Failed to rename group.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteGroup() {
        if (!isGroupSelected || selectedDetailsId == null)
            return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this group?\nThis action cannot be undone.",
                "Delete Group",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (groupService.deleteGroup(selectedDetailsId)) {
                JOptionPane.showMessageDialog(this, "Group deleted successfully!");
                loadConversations(); // Refresh list
                chatPanel.removeAll();
                chatPanel.revalidate();
                chatPanel.repaint();
                selectedDetailsId = null;
                isGroupSelected = false;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete group.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Load Users and Groups
    private void loadConversations() {
        if (conversationCombo == null)
            return;

        conversationCombo.removeAllItems();

        // 1. Load Groups
        List<model.Group> myGroups = groupService.getUserGroups(currentUser.getId());
        for (model.Group g : myGroups) {
            conversationCombo.addItem(g);
        }

        // 2. Load Users (Friends/All)
        List<User> allUsers = userService.getAllUsers();
        if (allUsers == null)
            allUsers = List.of();

        for (User user : allUsers) {
            if (user.getId() != null && !user.getId().equals(currentUser.getId())) {
                conversationCombo.addItem(user);
            }
        }

        if (conversationCombo.getItemCount() > 0) {
            conversationCombo.setSelectedIndex(0);
            loadConversation();
        }
    }

    // Load conversation
    private void loadConversation() {
        if (chatPanel == null || conversationCombo == null || conversationCombo.getSelectedIndex() < 0)
            return;

        Object selected = conversationCombo.getSelectedItem();
        if (selected == null)
            return;

        chatPanel.removeAll();
        java.util.List<Message> messages;

        if (selected instanceof model.Group) {
            model.Group group = (model.Group) selected;
            selectedDetailsId = group.getId();
            isGroupSelected = true;
            messages = messageService.getGroupMessages(selectedDetailsId);
            if (addMemberBtn != null)
                addMemberBtn.setVisible(true);
            if (viewMembersBtn != null)
                viewMembersBtn.setVisible(true);

            // Show management buttons if owner
            if (group.getOwnerId().equals(currentUser.getId())) {
                if (editGroupBtn != null)
                    editGroupBtn.setVisible(true);
                if (deleteGroupBtn != null)
                    deleteGroupBtn.setVisible(true);
            } else {
                if (editGroupBtn != null)
                    editGroupBtn.setVisible(false);
                if (deleteGroupBtn != null)
                    deleteGroupBtn.setVisible(false);
            }
        } else {
            User user = (User) selected;
            selectedDetailsId = user.getId();
            isGroupSelected = false;
            messages = messageService.getConversation(currentUser.getId(), selectedDetailsId);
            if (addMemberBtn != null)
                addMemberBtn.setVisible(false); // Hide button for users
            if (viewMembersBtn != null)
                viewMembersBtn.setVisible(false);
            if (editGroupBtn != null)
                editGroupBtn.setVisible(false);
            if (deleteGroupBtn != null)
                deleteGroupBtn.setVisible(false);
        }

        if (messages == null)
            messages = List.of();

        for (Message msg : messages) {
            addMessageToChatPanel(msg);
        }

        chatPanel.revalidate();
        chatPanel.repaint();
    }

    // Send message
    private void sendMessage() {
        String content = messageField.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Message cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedDetailsId == null) {
            JOptionPane.showMessageDialog(this, "Select a user or group first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        messageService.sendMessage(currentUser.getId(), selectedDetailsId, content);
        messageField.setText("");
        // Real-time listener updates GUI automatically
    }

    // Add a single message to chat panel
    private void addMessageToChatPanel(Message msg) {
        JPanel msgPanel = new JPanel();
        boolean isMe = msg.getSenderId().equals(currentUser.getId());
        msgPanel.setLayout(new FlowLayout(isMe ? FlowLayout.RIGHT : FlowLayout.LEFT));
        msgPanel.setBackground(new Color(245, 245, 245));

        String displayText = msg.getContent();
        // If group chat and not me, show sender name
        if (isGroupSelected && !isMe) {
            User sender = userService.getUserById(msg.getSenderId());
            String senderName = (sender != null) ? sender.getUsername() : "Unknown";
            displayText = "<html><font color='gray' size='-2'>" + senderName + "</font><br>" + msg.getContent()
                    + "</html>";
        }

        JLabel msgLabel = new JLabel(displayText);
        msgLabel.setOpaque(true);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        if (isMe) {
            msgLabel.setBackground(new Color(51, 102, 255));
            msgLabel.setForeground(Color.WHITE);
        } else {
            msgLabel.setBackground(new Color(200, 200, 200));
            msgLabel.setForeground(Color.BLACK);
        }

        // Add delete button for ALL messages
        JButton deleteBtn = new JButton("x");
        deleteBtn.setMargin(new Insets(0, 2, 0, 2));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setForeground(Color.GRAY);

        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (isMe) {
                    // For my messages: Choose between Delete for Me or Everyone
                    Object[] options = { "Delete for Me", "Delete for Everyone", "Cancel" };
                    int choice = JOptionPane.showOptionDialog(MessagesPanel.this,
                            "How would you like to delete this message?",
                            "Delete Message",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[2]);

                    if (choice == 0) { // Delete for Me
                        messageService.deleteMessageForMe(msg.getId(), currentUser.getId());
                        chatPanel.remove(msgPanel);
                        chatPanel.revalidate();
                        chatPanel.repaint();
                    } else if (choice == 1) { // Delete for Everyone
                        messageService.deleteMessage(msg.getId());
                        chatPanel.remove(msgPanel);
                        chatPanel.revalidate();
                        chatPanel.repaint();
                    }
                } else {
                    // For received messages: Only Delete for Me
                    int confirm = JOptionPane.showConfirmDialog(MessagesPanel.this,
                            "Delete this message for yourself?",
                            "Delete for Me",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        messageService.deleteMessageForMe(msg.getId(), currentUser.getId());
                        chatPanel.remove(msgPanel);
                        chatPanel.revalidate();
                        chatPanel.repaint();
                    }
                }
            }
        });

        if (isMe) {
            msgPanel.add(msgLabel);
            msgPanel.add(deleteBtn);
        } else {
            msgPanel.add(deleteBtn);
            msgPanel.add(msgLabel);
        }

        // Context Menu (Right Click) - Optional redundancy
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteMeItem = new JMenuItem("Delete For Me");
        deleteMeItem.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                messageService.deleteMessageForMe(msg.getId(), currentUser.getId());
                chatPanel.remove(msgPanel);
                chatPanel.revalidate();
                chatPanel.repaint();
            }
        });
        if (isMe) {
            JMenuItem editItem = new JMenuItem("Edit");
            editItem.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    String newContent = JOptionPane.showInputDialog(MessagesPanel.this, "Edit message:",
                            msg.getContent());
                    if (newContent != null && !newContent.trim().isEmpty() && !newContent.equals(msg.getContent())) {
                        if (messageService.updateMessage(msg.getId(), newContent.trim())) {
                            msgLabel.setText(newContent.trim()); // Update UI directly
                            msgLabel.revalidate();
                            msgLabel.repaint();
                        } else {
                            JOptionPane.showMessageDialog(MessagesPanel.this, "Failed to update message.");
                        }
                    }
                }
            });
            popupMenu.add(editItem);
        }

        popupMenu.add(deleteMeItem);
        msgLabel.setComponentPopupMenu(popupMenu);

        chatPanel.add(msgPanel);
        chatPanel.revalidate();
        chatPanel.repaint();

        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JScrollBar vertical = ((JScrollPane) chatPanel.getParent().getParent()).getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        });
    }

    // Real-time listener for incoming messages
    private void initRealTimeListener() {
        messageService.startRealTimeListener(new dao.MessageListener() {
            @Override
            public void onMessageReceived(Message newMessage) {
                if (isGroupSelected) {
                    if (newMessage.getReceiverId().equals(selectedDetailsId)) {
                        addMessageToChatPanel(newMessage);
                    }
                } else {
                    if ((newMessage.getSenderId().equals(selectedDetailsId)
                            && newMessage.getReceiverId().equals(currentUser.getId()))
                            || (newMessage.getSenderId().equals(currentUser.getId())
                                    && newMessage.getReceiverId().equals(selectedDetailsId))) {
                        addMessageToChatPanel(newMessage);
                    }
                }
            }
        });
    }
}
