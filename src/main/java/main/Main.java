package main;

import static spark.Spark.*;

public class Main {
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

        System.out.println("Welcome to Wrath Fitness Pet!");

        // Define root route for Spark
        get("/", (req, res) -> "Welcome to Wrath Fitness Pet");

        // Start OAuth server (registers additional routes)
        StravaAuth.startOAuthServer();

        // Start the token refresh task
        TokenRefresher.startTokenRefreshTask();

        // Create a new pet
        Pet myPet = new Pet("Fury");

        // Interact with the pet
        myPet.feed();
        myPet.drink();
    }
}
