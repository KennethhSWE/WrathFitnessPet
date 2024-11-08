package com.heroacademygym;

import java.util.Date;
import static spark.Spark.*;
import com.heroacademygym.models.User;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
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

                System.out.println("Login attempt with username: " + username); // Debug log

                if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                    res.status(400);
                    return "Error: Username and password are required.";
                }

                username = username.trim();
                password = password.trim();

                Document userDoc = userCollection.find(Filters.eq("username", username)).first();

                // Check if the user document was found
                if (userDoc == null) {
                    System.out.println("User not found for username: " + username);
                    res.status(401);
                    return "Error: Invalid username or password.";
                }

                System.out.println("User found: " + userDoc.toJson()); // Debug log for user data

                // Verify password using BCrypt
                boolean passwordMatch = BCrypt.checkpw(password, userDoc.getString("password"));
                if (!passwordMatch) {
                    System.out.println("Invalid password for username: " + username);
                    res.status(401);
                    return "Error: Invalid username or password.";
                }

                // Create session
                ObjectId userId = userDoc.getObjectId("_id");
                req.session(true).attribute("userId", userId.toHexString());
                System.out.println("Login successful for user " + username);
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

                // Debugging output to see the values of username and password
                System.out.println("Username: " + username);
                System.out.println("Password: " + password);

                if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
                    res.status(400);
                    return "Error: Username and password are required.";
                }

                username = username.trim();
                password = password.trim();

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

        // Other endpoints follow the same pattern with additional error handling if needed
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
        // Check if a user with the same username already exists
        Document existingUser = userCollection.find(Filters.eq("username", user.getUsername())).first();

        if (existingUser != null) {
            System.out.println("User with username " + user.getUsername() + " already exists.");
            return; // Exit the function to avoid overwriting the existing user
        }

        // Create a new document for the user with unique fields
        Document doc = new Document("username", user.getUsername())
                .append("password", user.getPassword())
                .append("strength", user.getAvatar().getStrength())
                .append("stamina", user.getAvatar().getStamina())
                .append("cardioHealth", user.getAvatar().getCardioHealth());

        // Insert the new user document into the database
        userCollection.insertOne(doc);
        System.out.println("New user " + user.getUsername() + " added successfully.");
    }
}
