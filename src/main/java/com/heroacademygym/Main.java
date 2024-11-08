package com.heroacademygym;

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
    private static MongoClient mongoClient;
    private static MongoCollection<Document> userCollection;
    private static MongoCollection<Document> workoutCollection;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        // Set up port and database connections
        String portEnv = System.getenv("PORT");
        if (!isNullOrEmpty(portEnv)) {
            port(Integer.parseInt(portEnv));
        } else {
            port(8080); // Default port for local testing
        }

        ipAddress("0.0.0.0");
        staticFiles.location("/public");

        // Initialize mongoClient
        System.out.println("MongoDB URI: " + System.getenv("MONGO_DB_URI"));
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
            try {
                String username = req.queryParams("username");
                String password = req.queryParams("password");

                if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                    res.status(400);
                    return "Error: Username and password are required.";
                }

                username = username.trim();
                password = password.trim();

                Document userDoc = userCollection.find(Filters.eq("username", username)).first();
                if (userDoc == null || !BCrypt.checkpw(password, userDoc.getString("password"))) {
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
                String username = req.queryParams("username");
                String password = req.queryParams("password");
                String name = req.queryParams("name");
                String age = req.queryParams("age");
                String email = req.queryParams("email");
                String fitnessGoal = req.queryParams("fitnessGoal");

                // Debugging output to see the values of username and password
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);

                if (isNullOrEmpty(username) || isNullOrEmpty(password) || isNullOrEmpty(name) || isNullOrEmpty(age) || isNullOrEmpty(email) || isNullOrEmpty(fitnessGoal)) {
                    res.status(400);
                    return "Error: All fields are required.";
                }

                username = username.trim();
                password = password.trim();

                if (userCollection.find(Filters.eq("username", username)).first() != null) {
                    res.status(409);
                    return "Error: Username already exists.";
                }

                // Hash the password
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                // Create a new user document and save it
                Document newUser = new Document("username", username)
                        .append("password", hashedPassword)
                        .append("name", name)
                        .append("age", Integer.parseInt(age))
                        .append("email", email)
                        .append("fitnessGoal", fitnessGoal)
                        .append("strength", 50)
                        .append("stamina", 50)
                        .append("cardioHealth", 50);
                
                userCollection.insertOne(newUser);

                return "Account created successfully for " + username;
            } catch (NullPointerException e) {
                e.printStackTrace();
                res.status(400);
                return "Error: Missing required fields.";
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Server error during sign-up.";
            }
        });

        // CHECK SESSION ENDPOINT
        get("/check-session", (req, res) -> {
            String userIdStr = req.session().attribute("userId");
            if (isNullOrEmpty(userIdStr)) {
                res.status(401); // No active session
                return "No active session";
            }

            Document userDoc = userCollection.find(Filters.eq("_id", new ObjectId(userIdStr))).first();
            if (userDoc != null) {
                res.status(200);
                return userDoc.getString("username");
            } else {
                res.status(404);
                return "User not found";
            }
        });

        // LOGOUT ENDPOINT
        post("/logout", (req, res) -> {
            req.session().invalidate();
            return "You have been logged out.";
        });

        // GET AVATAR STATS ENDPOINT
        get("/getAvatar", (req, res) -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
                return "Server error while retrieving avatar stats.";
            }
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
