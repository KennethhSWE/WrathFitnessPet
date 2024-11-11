package com.heroacademygym.models;

import org.bson.types.ObjectId;
import java.util.Date;

public class SignUpCode {
    private ObjectId id;            // MongoDB document ID
    private String code;            // The unique sign-up code
    private boolean redeemed;       // Tracks if the code has been used
    private Date createdAt;         // Creation timestamp
    private ObjectId redeemedBy;    // Reference to the user who redeemed it (optional)

    // Constructor
    public SignUpCode(String code) {
        this.id = new ObjectId();    // Automatically generate a new ObjectId for new codes
        this.code = code;
        this.redeemed = false;       // Initially, the code is not redeemed
        this.createdAt = new Date(); // Set creation date to current date
        this.redeemedBy = null;      // Initially, no user has redeemed the code
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public void setRedeemed(boolean redeemed) {
        this.redeemed = redeemed;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public ObjectId getRedeemedBy() {
        return redeemedBy;
    }

    public void setRedeemedBy(ObjectId redeemedBy) {
        this.redeemedBy = redeemedBy;
    }

    @Override
    public String toString() {
        return "SignUpCode{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", redeemed=" + redeemed +
                ", createdAt=" + createdAt +
                ", redeemedBy=" + redeemedBy +
                '}';
    }

    // Utility method to mark the code as redeemed by a specific user
    public void redeem(ObjectId userId) {
        this.redeemed = true;
        this.redeemedBy = userId;
    }
}
