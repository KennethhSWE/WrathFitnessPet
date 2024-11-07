package main;

import org.bson.types.ObjectId;

public class User {
    private ObjectId id;               // MongoDB document ID
    private String username;           // Username
    private String password;           // Password (to be hashed in a real app)
    private Avatar avatar;             // Avatar stats

    // Constructor for a new user with default avatar stats
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.avatar = new Avatar(username); // Initializes a new avatar with default stats
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public void setId(ObjectId id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Avatar getAvatar() { return avatar; }

    // Method to update avatar stats after a workout
    public void updateAvatarStats(int weight, int reps, boolean hitDailyGoal, boolean hitStreakBonus) {
        avatar.completeWorkout(weight, reps, hitDailyGoal, hitStreakBonus);
    }
}
