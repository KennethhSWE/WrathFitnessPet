package com.heroacademygym;

import java.util.Date;
import static spark.Spark.*;
import com.heroacademygym.models.User;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.google.gson.Gson;
import org.mindrot.jbcrypt.BCrypt;

public class Main {
    private static MongoClient mongoClient; // Class-level declaration for mongoClient
    private static MongoCollection<Document> userCollection;
    private static MongoCollection<Document> workoutCollection;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        // Configure port and MongoDB connection
        String portEnv = System.getenv("PORT");
        if (!isNullOrEmpty(portEnv)) {
            port(Integer.parseInt(portEnv));
        } else {
            port(8080); // Default port for local testing
        }

        ipAddress("0.0.0.0");
        staticFiles.location("/public");

        // Initialize mongoClient here
        mongoClient = MongoClients.create(System.getenv("MONGO_DB_URI"));
        MongoDatabase database = mongoClient.getDatabase("HeroAcademyGym");
        userCollection = database.getCollection("Users");
        workoutCollection = database.getCollection("Workouts");

        System.out.println("Welcome to Hero Academy Gym!");

        // Redirect root to index.html
        get("/", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });

        // LOGIN ENDPOINT
        post("/login", (req, res) -> {
            String username = req.queryParams("username").trim();
            String password = req.queryParams("password").trim();

            if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                res.status(400);
                return "Error: Username and password are required.";
            }

            Document userDoc = userCollection.find(Filters.eq("username", username)).first();
            if (userDoc == null || !BCrypt.checkpw(password, userDoc.getString("password"))) {
                res.status(401);
                return "Error: Invalid username or password.";
            }

            // Create session
            ObjectId userId = userDoc.getObjectId("_id");
            req.session(true).attribute("userId", userId.toHexString());
            return "Login successful for user " + username;
        });

        // SIGN UP ENDPOINT
        post("/signup", (req, res) -> {
            String username = req.queryParams("username").trim();
            String password = req.queryParams("password").trim();

            if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                res.status(400);
                return "Error: Username and password are required.";
            }

            if (userCollection.find(Filters.eq("username", username)).first() != null) {
                res.status(409);
                return "Error: Username already exists.";
            }

            // Hash the password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // Create a new user and save it
            User newUser = new User(username, hashedPassword);
            saveUser(newUser);

            return "Account created successfully for " + username;
        });

        // LOGOUT ENDPOINT
        post("/logout", (req, res) -> {
            req.session().invalidate();
            return "You have been logged out.";
        });

        // GET AVATAR STATS ENDPOINT
        get("/getAvatar", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401);
                return "Unauthorized";
            }

            ObjectId userId = new ObjectId(userIdStr);
            Document userDoc = userCollection.find(Filters.eq("_id", userId)).first();
            if (userDoc != null) {
                User user = gson.fromJson(userDoc.toJson(), User.class);
                res.type("application/json");
                return gson.toJson(user.getAvatar());
            } else {
                res.status(404);
                return "User not found";
            }
        });

        // LOG WORKOUT ENDPOINT
        post("/logWorkout", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401);
                return "Unauthorized";
            }

            ObjectId userId = new ObjectId(userIdStr);
            Document userDoc = userCollection.find(Filters.eq("_id", userId)).first();
            if (userDoc != null) {
                User user = gson.fromJson(userDoc.toJson(), User.class);

                // Parse workout details from JSON
                Document workoutData = Document.parse(req.body());
                int weight = workoutData.getInteger("weight");
                int reps = workoutData.getInteger("reps");
                boolean hitDailyGoal = workoutData.getBoolean("hitDailyGoal", false);
                boolean hitStreakBonus = workoutData.getBoolean("hitStreakBonus", false);

                // Update avatar stats
                user.getAvatar().completeWorkout(weight, reps, hitDailyGoal, hitStreakBonus);

                // Save updated user data
                userCollection.replaceOne(Filters.eq("_id", userId), Document.parse(gson.toJson(user)));

                // Save workout log
                Document workoutLog = new Document("userId", userId)
                    .append("weight", weight)
                    .append("reps", reps)
                    .append("hitDailyGoal", hitDailyGoal)
                    .append("hitStreakBonus", hitStreakBonus)
                    .append("date", new Date());
                workoutCollection.insertOne(workoutLog);

                return "Workout logged successfully";
            } else {
                res.status(404);
                return "User not found";
            }
        });

        // VIEW WORKOUT HISTORY ENDPOINT
        get("/view-workouts", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401);
                return "Please log in to view your workout history.";
            }

            ObjectId userId = new ObjectId(userIdStr);
            StringBuilder workoutHistory = new StringBuilder("Workout History:\n");

            // Fetch workouts
            workoutCollection.find(Filters.eq("userId", userId)).forEach(doc -> {
                workoutHistory.append("Date: ").append(doc.getDate("date"))
                    .append(", Weight: ").append(doc.getInteger("weight"))
                    .append(", Reps: ").append(doc.getInteger("reps"))
                    .append(", Daily Goal: ").append(doc.getBoolean("hitDailyGoal"))
                    .append(", Streak Bonus: ").append(doc.getBoolean("hitStreakBonus"))
                    .append("\n");
            });

            return workoutHistory.length() > 0 ? workoutHistory.toString() : "No workouts found.";
        });
    }

    // Utility Methods
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static User getUserById(ObjectId userId) {
        Document doc = userCollection.find(Filters.eq("_id", userId)).first();
        if (doc == null) return null;

        User user = new User(doc.getString("username"), doc.getString("password"));
        user.getAvatar().setStrength(doc.getInteger("strength"));
        user.getAvatar().setStamina(doc.getInteger("stamina"));
        user.getAvatar().setCardioHealth(doc.getInteger("cardioHealth"));
        return user;
    }

    private static void saveUser(User user) {
        Document doc = new Document("username", user.getUsername())
            .append("password", user.getPassword())
            .append("strength", user.getAvatar().getStrength())
            .append("stamina", user.getAvatar().getStamina())
            .append("cardioHealth", user.getAvatar().getCardioHealth());

        userCollection.replaceOne(Filters.eq("_id", user.getId()), doc, new ReplaceOptions().upsert(true));
    }
}
