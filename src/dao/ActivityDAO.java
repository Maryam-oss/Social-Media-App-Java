package dao;

import model.Activity;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.client.FindIterable;
import static com.mongodb.client.model.Filters.*;
import java.util.*;
import java.time.*;

public class ActivityDAO extends BaseDAO<Activity> implements IActR {

    // constructor - initialize collection
    public ActivityDAO() {
        super("activities");
    }

    // create a new activity in the database
    public String create(Activity activity) {
        try {
            ObjectId objectId = new ObjectId();
            String id = objectId.toString();
            activity.setId(id);

            Document doc = activityToDocument(activity);
            collection.insertOne(doc);

            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // find an activity by its id
    public Activity findById(String id) {
        try {
            Bson filter = eq("_id", id);
            FindIterable<Document> iter = collection.find(filter);
            Document doc = iter.first();

            if (doc != null) {
                Activity activity = documentToActivity(doc);
                return activity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // get all activities from the database
    public List<Activity> getAll() {
        List<Activity> activities = new ArrayList<>();
        FindIterable<Document> iter = collection.find();

        for (Document doc : iter) {
            Activity activity = documentToActivity(doc);
            activities.add(activity);
        }

        return activities;
    }

    // update an existing activity
    public boolean update(Activity activity) {
        try {
            Document docFields = activityToDocument(activity);
            Document updateDoc = new Document("$set", docFields);

            String id = activity.getId();
            Bson filter = eq("_id", id);

            collection.updateOne(filter, updateDoc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // delete an activity by id
    public boolean delete(String id) {
        try {
            Bson filter = eq("_id", id);
            collection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // get all activities for a specific user
    
    public List<Activity> getActivitiesByUserId(String userId) {
        List<Activity> activities = new ArrayList<>();

        Bson filter = eq("userId", userId);
        FindIterable<Document> iter = collection.find(filter);

        for (Document doc : iter) {
            Activity activity = documentToActivity(doc);
            activities.add(activity);
        }

        return activities;
    }

    // get all activities for a specific post
    public List<Activity> getActivitiesByPostId(String postId) {
        List<Activity> activities = new ArrayList<>();

        Bson filter = eq("targetPostId", postId);
        FindIterable<Document> iter = collection.find(filter);

        for (Document doc : iter) {
            Activity activity = documentToActivity(doc);
            activities.add(activity);
        }

        return activities;
    }

    // convert activity object to mongodb document
    private Document activityToDocument(Activity activity) {
        Document doc = new Document();

        String id = activity.getId();
        doc.append("_id", id);

        String userId = activity.getUserId();
        doc.append("userId", userId);

        String type = activity.getActivityType();
        doc.append("activityType", type);

        String targetUserId = activity.getTargetUserId();
        doc.append("targetUserId", targetUserId);

        String targetPostId = activity.getTargetPostId();
        doc.append("targetPostId", targetPostId);

        String targetCommentId = activity.getTargetCommentId();
        doc.append("targetCommentId", targetCommentId);

        if (activity.getTimestamp() != null) {
            LocalDateTime localTime = activity.getTimestamp();
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = localTime.atZone(zoneId);
            Instant instant = zonedDateTime.toInstant();
            Date date = Date.from(instant);
            doc.append("timestamp", date);
        } else {
            doc.append("timestamp", null);
        }

        String description = activity.getDescription();
        doc.append("description", description);

        return doc;
    }

    // convert mongodb document to activity object
    private Activity documentToActivity(Document doc) {
        Activity activity = new Activity();
        activity.setId(doc.getString("_id"));
        activity.setUserId(doc.getString("userId"));
        activity.setActivityType(doc.getString("activityType"));
        activity.setTargetUserId(doc.getString("targetUserId"));
        activity.setTargetPostId(doc.getString("targetPostId"));
        activity.setTargetCommentId(doc.getString("targetCommentId"));

        Date timestampDate = doc.getDate("timestamp");
        if (timestampDate != null) {
            Instant instant = timestampDate.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zonedDateTime = instant.atZone(zoneId);
            LocalDateTime localTime = zonedDateTime.toLocalDateTime();
            activity.setTimestamp(localTime);
        } else {
            activity.setTimestamp(null);
        }

        activity.setDescription(doc.getString("description"));
        return activity;
    }
}
