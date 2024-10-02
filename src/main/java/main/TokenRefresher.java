package main;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TokenRefresher {
    private static final Logger logger = LoggerFactory.getLogger(TokenRefresher.class);

    private static final long REFRESH_INTERVAL = 5 * 60 * 1000L; // Check every 5 minutes

    public static void startTokenRefreshTask() {
        Timer timer = new Timer(true); // Daemon thread
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                refreshTokensForAllUsers();
            }
        }, 0, REFRESH_INTERVAL);
}

    private static void refreshTokensForAllUsers() {
        FindIterable<Document> users = StravaAuth.collection.find();
        long currentTime = System.currentTimeMillis() / 1000L;

        for (Document userToken : users) {
            long expiresAt = userToken.getLong("expires_at");
            String athleteId = userToken.getString("athlete_id");

            if (expiresAt <= currentTime + (5 * 60)) { // Token expires in next 5 minutes
                try {
                    logger.info("Proactively refreshing token for athlete_id: " + athleteId);
                    StravaAuth.refreshAccessToken(athleteId);
                } catch (IOException e) {
                    logger.error("Failed to refresh token for athlete_id: " + athleteId, e);
                }
            }
        }
    }
}