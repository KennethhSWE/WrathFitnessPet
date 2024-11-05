package main;

import static spark.Spark.*;

public class Main {
    private static Avatar myAvatar; // Make this static so it's accessible in all routes

    public static void main(String[] args) {
        // Set the port from the environment variable or default to 8080
        String portEnv = System.getenv("PORT");
        if (portEnv != null) {
            port(Integer.parseInt(portEnv));
        } else {
            port(8080); // Default port for local testing
        }

        // Bind to all network interfaces
        ipAddress("0.0.0.0");

        System.out.println("Welcome to Hero Academy Gym!");

        // Define the root route for Spark
        get("/", (req, res) -> "Welcome to Hero Academy Gym");

        // Start OAuth server (registers additional routes)
        StravaAuth.startOAuthServer();

        // Start the token refresh task
        TokenRefresher.startTokenRefreshTask();

        // Route to create a new account and avatar
        post("/create-account", (req, res) -> {
            String name = req.queryParams("name");
            if (name == null || name.isEmpty()) {
                return "Error: Name is required to create an account.";
            }
            myAvatar = new Avatar(name);
            return "Account created for " + name + " with a new avatar!";
        });

        // Route to view the avatar's stats
        get("/view-avatar", (req, res) -> {
            if (myAvatar == null) {
                return "No avatar created yet. Please create an account first.";
            }
            return "Avatar Stats:\n" + 
                    "Name: " + myAvatar.getName() + "\n" +
                    "Strength: " + myAvatar.getStrength() + "\n" +
                    "Stamina: " + myAvatar.getStamina() + "\n" +
                    "Cardio Health: " + myAvatar.getCardioHealth(); 
        });

        // Route to simulate or test a workout
        post("/workout", (req, res) -> {
            if (myAvatar == null) {
                return "No avatar created yet. Please create an account first.";
            }

            int weight = Integer.parseInt(req.queryParams("weight"));
            int reps = Integer.parseInt(req.queryParams("reps"));
            boolean hitDailyGoal = Boolean.parseBoolean(req.queryParams("hitDailyGoal"));
            boolean hitStreakBonus = Boolean.parseBoolean(req.queryParams("hitStreakBonus"));

            myAvatar.completeWorkout(weight, reps, hitDailyGoal, hitStreakBonus);
            return "Workout complete for " + myAvatar.getName() + "!";
        });

        // Create a new avatar for initial testing
        myAvatar = new Avatar("Champion");

        // Testing avatar interaction code
        myAvatar.completeWorkout(100, 10, true, true);
        myAvatar.setAppearance("Warrior Gear");

        // Display stats after a workout
        System.out.println("Strength: " + myAvatar.getStrength());
        System.out.println("Stamina: " + myAvatar.getStamina());
        System.out.println("Cardio Health: " + myAvatar.getCardioHealth());
    }
}

