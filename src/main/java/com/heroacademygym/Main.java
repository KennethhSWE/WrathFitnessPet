package com.heroacademygym;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import static spark.Spark.*;
import com.heroacademygym.models.User;
import com.heroacademygym.models.SignUpCode;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.mindrot.jbcrypt.BCrypt;

public class Main {
    private static MongoClient mongoClient;
    private static MongoCollection<Document> userCollection;
    private static MongoCollection<Document> workoutCollection;
    private static MongoCollection<Document> signUpCodeCollection; // New collection for sign-up codes
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        String portEnv = System.getenv("PORT");
        if (!isNullOrEmpty(portEnv)) {
            port(Integer.parseInt(portEnv));
        } else {
            port(8080);
        }

        ipAddress("0.0.0.0");
        staticFiles.location("/public");

        mongoClient = MongoClients.create(System.getenv("MONGO_DB_URI"));
        MongoDatabase database = mongoClient.getDatabase("HeroAcademyGym");
        userCollection = database.getCollection("Users");
        workoutCollection = database.getCollection("Workouts");
        signUpCodeCollection = database.getCollection("SignUpCodes"); // Initialize the sign-up codes collection

        System.out.println("Welcome to Hero Academy Gym!");

        // Insert hardcoded sign-up code for testing
        insertTestSignUpCode();

        // Redirect root to index.html
        get("/", (req, res) -> {
            if (req.session().attribute("userId") != null) {
                res.redirect("/dashboard.html");
            } else {
                res.redirect("/index.html");
            }
            return null;
        });

        // LOGIN ENDPOINT
        post("/login", (req, res) -> {
            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                String username = json.get("username").getAsString();
                String password = json.get("password").getAsString();

                if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                    res.status(400);
                    return "Error: Username and password are required.";
                }

                Document userDoc = userCollection.find(Filters.eq("username", username)).first();
                if (userDoc == null || !BCrypt.checkpw(password, userDoc.getString("passwordHash"))) {
                    res.status(401);
                    return "Error: Invalid username or password.";
                }

                // Create session
                ObjectId userId = userDoc.getObjectId("_id");
                req.session(true).attribute("userId", userId.toHexString());
                return "Login successful for user " + username;
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Server error during login.";
            }
        });

        // SIGN UP ENDPOINT
        post("/signup", (req, res) -> {
            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                String username = json.get("username").getAsString();
                String password = json.get("password").getAsString();
                String email = json.get("email").getAsString();
                String signUpCode = json.get("signUpCode").getAsString(); // New code field

                if (isNullOrEmpty(username) || isNullOrEmpty(password) || isNullOrEmpty(email) || isNullOrEmpty(signUpCode)) {
                    res.status(400);
                    return "Error: Username, password, email, and sign-up code are required.";
                }

                // Validate the sign-up code
                Document codeDoc = signUpCodeCollection.find(Filters.eq("code", signUpCode)).first();
                if (codeDoc == null || codeDoc.getBoolean("redeemed")) {
                    res.status(400);
                    return "Error: Invalid or already redeemed sign-up code.";
                }

                if (userCollection.find(Filters.eq("username", username)).first() != null) {
                    res.status(409);
                    return "Error: Username already exists.";
                }

                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                User newUser = new User(username, hashedPassword, email);
                saveUser(newUser);

                // Mark the sign-up code as redeemed
                codeDoc.put("redeemed", true);
                codeDoc.put("redeemedBy", newUser.getUsername());
                signUpCodeCollection.replaceOne(Filters.eq("_id", codeDoc.getObjectId("_id")), codeDoc);

                return "Account created successfully for " + username;
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Server error during sign-up.";
            }
        });

        // LOGOUT ENDPOINT
        post("/logout", (req, res) -> {
            req.session().invalidate();
            return "You have been logged out.";
        });

        // CHECK SESSION ENDPOINT
        get("/check-session", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401);
                return "Unauthorized";
            }
            Document userDoc = userCollection.find(Filters.eq("_id", new ObjectId(userIdStr))).first();
            if (userDoc == null) {
                res.status(404);
                return "User not found";
            }
            return userDoc.getString("username");
        });

        // LOG WORKOUT ENDPOINT
        post("/logWorkout", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401);
                return "Unauthorized";
            }

            try {
                JsonObject json = JsonParser.parseString(req.body()).getAsJsonObject();
                double weight = json.get("weight").getAsDouble();
                int reps = json.get("reps").getAsInt();
                boolean dailyGoal = json.get("dailyGoal").getAsBoolean();
                boolean streakBonus = json.get("streakBonus").getAsBoolean();

                ObjectId userId = new ObjectId(userIdStr);

                Document workout = new Document("userId", userId)
                        .append("timestamp", new Date())
                        .append("weight", weight)
                        .append("reps", reps)
                        .append("dailyGoal", dailyGoal)
                        .append("streakBonus", streakBonus);

                workoutCollection.insertOne(workout);
                return "Workout logged successfully!";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Error logging workout.";
            }
        });

        // GET WORKOUT HISTORY ENDPOINT
        get("/getWorkoutHistory", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401);
                return "Unauthorized";
            }

            try {
                ObjectId userId = new ObjectId(userIdStr);
                List<Document> workouts = workoutCollection.find(Filters.eq("userId", userId))
                        .sort(Sorts.descending("timestamp"))
                        .into(new ArrayList<>());

                res.type("application/json");
                return gson.toJson(workouts);
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Error retrieving workout history.";
            }
        });
    }

    // Insert a hardcoded test sign-up code
    private static void insertTestSignUpCode() {
        String testCode = "TEST12345";
        if (signUpCodeCollection.find(Filters.eq("code", testCode)).first() == null) {
            Document signUpCodeDoc = new Document("code", testCode)
                    .append("redeemed", false);
            signUpCodeCollection.insertOne(signUpCodeDoc);
            System.out.println("Inserted test sign-up code: " + testCode);
        }
    }

    // Utility Methods
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static void saveUser(User user) {
        Document doc = new Document("username", user.getUsername())
                .append("passwordHash", user.getPasswordHash()) // Store hashed password directly
                .append("email", user.getEmail()) // Added email field to user document
                .append("strength", user.getStrength()) // Store strength attribute
                .append("stamina", user.getStamina())   // Store stamina attribute
                .append("cardioHealth", user.getCardioHealth()); // Store cardioHealth attribute

        ReplaceOptions options = new ReplaceOptions().upsert(true);
        userCollection.replaceOne(Filters.eq("username", user.getUsername()), doc, options);
    }
}
