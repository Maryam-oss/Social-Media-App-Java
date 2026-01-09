package model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group implements Displayable {
    private String id;
    private String name;
    private String ownerId;
    private List<String> memberIds;
    private long createdAt;

    public Group() {
    }

    public Group(String name, String ownerId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.ownerId = ownerId;
        this.memberIds = new ArrayList<>();
        this.memberIds.add(ownerId); // Owner is automatically a member
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }

    public void removeMember(String userId) {
        memberIds.remove(userId);
    }

    @Override
    public String getSummary() {
        return "[Group] " + name;
    }

    @Override
    public void display() {
        System.out.println("Group: " + name);
    }
}
