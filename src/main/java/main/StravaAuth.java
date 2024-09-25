package main;

import java.io.IOException;
import okhttp3.*;
import static spark.Spark.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class StravaAuth {

    private static final String clientId = System.getenv("STRAVA_CLIENT_ID"); // strava client id stored in the env file 
    private static final String clientSecret = System.getenv("STRAVA_CLIENT_SECRET");  // strava client secret stored in the env file 
    private static final String redirectUri = "http://localhost:8080/exchange_token";  // Local server endpoint to handle redirect
    private static final OkHttpClient client = new OkHttpClient();  // OkHttp Client for making HTTP requests

    // MongoDB connection using the connection string
    private static final MongoClient mongoClient = MongoClients.create(System.getenv("MONGO_DB_URI"));
    
    private static MongoDatabase database = mongoClient.getDatabase("StravaUsers");  // Your DB name
    private static MongoCollection<Document> collection = database.getCollection("StravaTokens");

    // Function to generate the Strava authorization URL
    public static String getAuthUrl() {
        return "https://www.strava.com/oauth/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&response_type=code" +
                "&scope=read,activity:read_all";
    }

    // Main method to set up the local server and handle the OAuth flow
    public static void startOAuthServer() {
        port(8080);  // Set the server to listen on port 8080

        get("/exchange_token", (req, res) -> {
            String authCode = req.queryParams("code");  // Get the authorization code from the URL
            if (authCode != null) {
                try {
                    // Exchange the authorization code for an access token
                    exchangeToken(authCode);
                    return "Authorization successful! You can close this window.";
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Error: Token exchange failed!";
                }
            }
            return "Error: Authorization code missing!";
        });

        // Print the URL to the console so you can open it in the browser for testing
        System.out.println("Authorize the app by visiting: " + getAuthUrl());
    }

    // Function to exchange the authorization code for an access token
    public static void exchangeToken(String authCode) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("code", authCode)
                .add("grant_type", "authorization_code")
                .build();

        Request request = new Request.Builder()
                .url("https://www.strava.com/oauth/token")
                .post(formBody)
                .build();

        // Execute the HTTP request synchronously
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                System.out.println("HTTP error: " + response.code());
                System.out.println("Error Body " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }

            // Print the token data (this will include the access token and refresh token)
            String responseData = response.body().string();
            System.out.println("Response data: " + responseData);

            // You can parse the responseData here to extract the access token and refresh token
            parseAndStoreTokens(responseData);
        }
    }

    // Function to parse and store tokens in MongoDB
    public static void parseAndStoreTokens(String responseData) {
        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
        String accessToken = jsonObject.get("access_token").getAsString();
        String refreshToken = jsonObject.get("refresh_token").getAsString();

        System.out.println("Access Token: " + accessToken);
        System.out.println("Refresh Token: " + refreshToken);

        // Store tokens in MongoDB
        Document tokenDocument = new Document("access_token", accessToken)
                .append("refresh_token", refreshToken);
        System.out.println("Inserting token into MongoDB");
        collection.insertOne(tokenDocument);
        System.out.println("Token inserted successfully");
    }
}
