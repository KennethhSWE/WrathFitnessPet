package com.heroacademygym;

import java.util.Date;
import static spark.Spark.*;
import com.heroacademygym.models.SignUpCode;
import com.heroacademygym.models.User;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.types.ObjectId;

public class Main {
    private static MongoCollection<Document> signUpCodeCollection;
    private static MongoCollection<Document> userCollection;
    private static MongoClient mongoClient;
    private static MongoCollection<Document> workoutCollection;

    public static void main(String[] args) {
        String portEnv = System.getenv("PORT");
        if (!isNullOrEmpty(portEnv)) {
            port(Integer.parseInt(portEnv));
        } else {
            port(8080); // Default port for local testing
        }

        ipAddress("0.0.0.0");

        staticFiles.location("/frontend");

        mongoClient = MongoClients.create(System.getenv("MONGO_DB_URI"));
        MongoDatabase database = mongoClient.getDatabase("HeroAcademyGym");
        signUpCodeCollection = database.getCollection("SignUpCodes");
        userCollection = database.getCollection("Users");
        workoutCollection = database.getCollection("Workouts"); 

        System.out.println("Welcome to Hero Academy Gym!");

        get("/", (req, res) -> "Welcome to Hero Academy Gym");

        // Add this temporary route to access the index.html file in the frontend folder Remove AFTER TEST!
        get("/frontend", (req, res) -> {
            res.redirect("/index.html");
            return null;
        });


        post("/workout", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                return "Please log in to log a workout.";
            }

            ObjectId userId = new ObjectId(userIdStr);
            User user = getUserById(userId);

            if (user == null) {
                return "User not found. Please create an account first.";
            }

            int weight = Integer.parseInt(req.queryParams("weight"));
            int reps = Integer.parseInt(req.queryParams("reps"));
            boolean hitDailyGoal = Boolean.parseBoolean(req.queryParams("hitDailyGoal"));
            boolean hitStreakBonus = Boolean.parseBoolean(req.queryParams("hitStreakBonus"));

            user.updateAvatarStats(weight, reps, hitDailyGoal, hitStreakBonus);
            saveUser(user);

            // log the workout in MongoDB
            Document workoutLog = new Document("userId", userId)
            .append("weight", weight)
            .append("reps", reps)
            .append("hitDailyGoal", hitDailyGoal)
            .append("hitStreakBonus", hitStreakBonus)
            .append("date", new Date()); 

            workoutCollection.insertOne(workoutLog); //saves the workout log 

            return "Workout complete for" + user.getUsername() + "!";
        });

        // New endpoint to view the workout history 
        get("/view-workouts", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                return "Please log in to view your workout history.";
            }

            ObjectId userId = new ObjectId(userIdStr);
            StringBuilder workoutHistory = new StringBuilder("Workout History:\n");

            // fetch workouts from the collection 
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

        // Route to create a new account and avatar with sign-up code verification
        post("/create-account", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String signUpCode = req.queryParams("signUpCode");

            if (isNullOrEmpty(username) || isNullOrEmpty(password) || isNullOrEmpty(signUpCode)) {
                return "Error: Username, password, and sign-up code are required.";
            }

            Document codeDoc = signUpCodeCollection.find(Filters.eq("code", signUpCode)).first();
            if (codeDoc == null || codeDoc.getBoolean("isRedeemed")) {
                return "Error: Invalid or already redeemed sign-up code.";
            }

            ObjectId userId = new ObjectId();
            Document update = new Document("$set", new Document("isRedeemed", true).append("redeemedBy", userId));
            signUpCodeCollection.updateOne(Filters.eq("code", signUpCode), update);

            User newUser = new User(username, password);
            newUser.setId(userId); // Set the generated ID
            saveUser(newUser);
            return "Account created for " + username + " with a verified sign-up code!";
        });

        // Login route
        post("/login", (req, res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");

            if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                return "Error: Username and password are required.";
            }

            Document userDoc = userCollection.find(Filters.eq("username", username)).first();
            if (userDoc == null || !userDoc.getString("password").equals(password)) {
                return "Error: Invalid username or password.";
            }

            ObjectId userId = userDoc.getObjectId("_id");
            req.session(true).attribute("userId", userId.toHexString());
            return "Login successful for user " + username;
        });

        // Logout route
        post("/logout", (req, res) -> {
            req.session().invalidate();
            return "You have been logged out.";
        });

        // Route to view the avatar's stats
        get("/view-avatar", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                return "Please log in to view your avatar.";
            }

            ObjectId userId = new ObjectId(userIdStr);
            User user = getUserById(userId);

            if (user == null) {
                return "User not found.";
            }

            return "Avatar Stats:\n" +
                   "Name: " + user.getUsername() + "\n" +
                   "Strength: " + user.getAvatar().getStrength() + "\n" +
                   "Stamina: " + user.getAvatar().getStamina() + "\n" +
                   "Cardio Health: " + user.getAvatar().getCardioHealth();
        });

        // Route to simulate or test a workout
        post("/workout", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                return "Please log in to log a workout.";
            }

            ObjectId userId = new ObjectId(userIdStr);
            User user = getUserById(userId);

            if (user == null) {
                return "User not found. Please create an account first.";
            }

            int weight = Integer.parseInt(req.queryParams("weight"));
            int reps = Integer.parseInt(req.queryParams("reps"));
            boolean hitDailyGoal = Boolean.parseBoolean(req.queryParams("hitDailyGoal"));
            boolean hitStreakBonus = Boolean.parseBoolean(req.queryParams("hitStreakBonus"));

            user.updateAvatarStats(weight, reps, hitDailyGoal, hitStreakBonus);
            saveUser(user);

            return "Workout complete for " + user.getUsername() + "!";
        });

        // Route to set the avatar's appearance
        post("/set-avatar-appearance", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                return "Please log in to customize your avatar.";
            }

            ObjectId userId = new ObjectId(userIdStr);
            User user = getUserById(userId);

            if (user == null) {
                return "User not found.";
            }

            String outfitChoice = req.queryParams("outfitChoice");
            if (isNullOrEmpty(outfitChoice)) {
                return "Error: Outfit choice is required.";
            }

            user.getAvatar().setAppearance(outfitChoice);
            saveUser(user);

            return "Avatar appearance updated for " + user.getUsername() + "!";
        });
    }

    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String generateSignUpCode() {
        String code = generateRandomCode();
        SignUpCode signUpCode = new SignUpCode(code);

        Document doc = new Document("code", signUpCode.getCode())
            .append("isRedeemed", signUpCode.isRedeemed())
            .append("createdAt", signUpCode.getCreatedAt());

        signUpCodeCollection.insertOne(doc);
        System.out.println("Generated sign-up code: " + code);
        return code;
    }

    private static String generateRandomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    public static boolean redeemSignUpCode(String code, ObjectId userId) {
        Document foundCode = signUpCodeCollection.find(Filters.eq("code", code)).first();

        if (foundCode == null || foundCode.getBoolean("isRedeemed")) {
            System.out.println("Invalid or already redeemed code.");
            return false;
        }

        Document update = new Document("$set", new Document("isRedeemed", true).append("redeemedBy", userId));
        signUpCodeCollection.updateOne(Filters.eq("code", code), update);

        System.out.println("Code redeemed successfully for user ID: " + userId);
        return true;
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

