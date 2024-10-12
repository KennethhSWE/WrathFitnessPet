package main;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import okhttp3.*;
import static spark.Spark.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StravaAuth {

    private static final Logger logger = LoggerFactory.getLogger(StravaAuth.class);

    private static final String clientId = System.getenv("STRAVA_CLIENT_ID");
    private static final String clientSecret = System.getenv("STRAVA_CLIENT_SECRET");
    private static final String redirectUri = "https://wrathfitnesspet.onrender.com/exchange_token";
    private static final OkHttpClient client = new OkHttpClient();

    // MongoDB connection using the connection string
    private static final MongoClient mongoClient = MongoClients.create(System.getenv("MONGO_DB_URI"));

    static MongoDatabase database = mongoClient.getDatabase("StravaUsers");  // Your DB name
    static MongoCollection<Document> collection = database.getCollection("StravaTokens");

    // Function to generate the Strava authorization URL
    public static String getAuthUrl() {
        String encodedRedirectUri = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return "https://www.strava.com/oauth/authorize?" +
                "client_id=" + clientId +
                "&redirect_uri=" + encodedRedirectUri +
                "&response_type=code" +
                "&scope=read,activity:read_all";
    }

    // Method to set up the OAuth flow
    public static void startOAuthServer() {
        // Do not set the port here; it's set in Main.java

        get("/exchange_token", (req, res) -> {
            logger.info("Received request at /exchange_token");
            String authCode = req.queryParams("code");
            if (authCode != null) {
                try {
                    exchangeToken(authCode);
                    return "Authorization successful! You can close this window.";
                } catch (IOException e) {
                    logger.error("Error during token exchange", e);
                    return "Error: Token exchange failed!";
                }
            }
            return "Error: Authorization code missing!";
        });

        logger.info("Authorize the app by visiting: " + getAuthUrl());
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
                logger.error("HTTP error: " + response.code());
                logger.error("Error Body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }

            // Get the token data
            String responseData = response.body().string();
            logger.info("Response data: " + responseData);

            // Parse and store tokens
            parseAndStoreTokens(responseData);
        }
    }

    // Function to parse and store tokens in MongoDB
    public static void parseAndStoreTokens(String responseData) {
        JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
        
        // Declaring athleteId 
        String athleteId = jsonObject.getAsJsonObject("athlete").get("id").getAsString();

        try {
            // Encrypting the tokens 
            String accessToken = jsonObject.get("access_token").getAsString(); 
            String refreshToken = jsonObject.get("refresh_token").getAsString();
            String encryptedAccessToken = EncryptionUtil.encrypt(accessToken);
            String encryptedRefreshToken = EncryptionUtil.encrypt(refreshToken);

            long expiresAt;
            try {
                expiresAt = jsonObject.get("expires_at").getAsLong();
            } catch (Exception e) {
                logger.error("Error getting expires_at for athlete_id: " + athleteId, e);
                return; // Exit the function if expires_at is missing.
            }
        
            // Create the token document
            Document tokenDocument = new Document("athlete_id", athleteId)
                    .append("access_token", encryptedAccessToken)
                    .append("refresh_token", encryptedRefreshToken)
                    .append("expires_at", expiresAt);

            // Update or insert the document in MongoDB
            collection.updateOne(
                    Filters.eq("athlete_id", athleteId),
                    new Document("$set", tokenDocument),
                    new UpdateOptions().upsert(true)
            );

            logger.info("Tokens stored for athlete_id: " + athleteId);

        } catch (Exception e) {
            // Now athleteId is accessible here
            logger.error("Error encrypting tokens for athlete_id: " + athleteId, e);
        }
    }

    // Function to refresh the access token using the refresh token
    public static void refreshAccessToken(String athleteId) throws IOException {
        // Retrieve the user's refresh token from the database
        Document userToken = collection.find(Filters.eq("athlete_id", athleteId)).first();

        if (userToken != null) {
            String refreshToken;
            try {
                refreshToken = EncryptionUtil.decrypt(userToken.getString("refresh_token"));
            } catch (Exception e) {
                logger.error("Error decrypting refresh token for athlete_id: " + athleteId, e);
                throw new IOException("Error decrypting refresh token", e);
            }

            RequestBody formBody = new FormBody.Builder()
                    .add("client_id", clientId)
                    .add("client_secret", clientSecret)
                    .add("grant_type", "refresh_token")
                    .add("refresh_token", refreshToken)
                    .build();

            Request request = new Request.Builder()
                    .url("https://www.strava.com/oauth/token")
                    .post(formBody)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("HTTP error during token refresh: " + response.code());
                    logger.error("Error Body: " + response.body().string());
                    throw new IOException("Unexpected code " + response);
                }

                String responseData = response.body().string();
                logger.info("Token refresh response data: " + responseData);

                // Update the stored tokens with the new ones
                parseAndStoreTokens(responseData);
            }
        } else {
            logger.error("No token found for athlete_id: " + athleteId);
            throw new IOException("No token found for athlete_id: " + athleteId);
        }
    }

    // Function to get a valid access token, refreshing if necessary
    public static String getValidAccessToken(String athleteId) throws IOException {
        Document userToken = collection.find(Filters.eq("athlete_id", athleteId)).first();

        if (userToken != null) {
            long expiresAt;
            try {
                expiresAt = userToken.getLong("expires_at");
            } catch (NullPointerException e) {
                logger.error("Missing expires_at for athlete_id: " + athleteId, e);
                throw new IOException("Missing expires_at for athlete_id: " + athleteId, e);
            }

            long currentTime = System.currentTimeMillis() / 1000L; // Current time in seconds

            if (expiresAt <= currentTime) {
                // Token has expired; refresh it
                logger.info("Access token expired for athlete_id: " + athleteId + ". Refreshing token.");
                refreshAccessToken(athleteId);
                // Retrieve the updated token
                userToken = collection.find(Filters.eq("athlete_id", athleteId)).first();
            }

            try {
                // Decrypt access token before returning
                return EncryptionUtil.decrypt(userToken.getString("access_token")); // Decryption added
            } catch (Exception e) {
                logger.error("Error decrypting access token for athlete_id: " + athleteId, e); // Handle decryption error
                throw new IOException("Error decrypting access token", e);
            }
        } else {
            logger.error("No token found for athlete_id: " + athleteId);
            throw new IOException("No token found for athlete_id: " + athleteId);
        }
    }

    // Example method to get athlete activities
    public static String getAthleteActivities(String athleteId) throws IOException {
        String accessToken = getValidAccessToken(athleteId);

        Request request = new Request.Builder()
                .url("https://www.strava.com/api/v3/athlete/activities")
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                logger.error("HTTP error when fetching activities: " + response.code());
                logger.error("Error Body: " + response.body().string());
                throw new IOException("Unexpected code " + response);
            }

            String responseData = response.body().string();
            logger.info("Activities data: " + responseData);
            return responseData;
        }
    }
}

