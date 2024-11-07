package com.heroacademygym.models; 

import org.bson.types.ObjectId;
import java.util.Date; 

public class SignUpCode {
    private ObjectId id;           // MongoDB document ID
    private String code;           // The unique sign-up code
    private boolean isRedeemed;    // Tracks if the code has been used
    private Date createdAt;        // Creation timestamp
    private ObjectId redeemedBy;   // Reference to the user who redeemed it (optional)

    public SignUpCode(String code) {
        this.code = code;
        this.isRedeemed = false;
        this.createdAt = new Date();
        this.redeemedBy = null;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public boolean isRedeemed() { return isRedeemed; }
    public void setRedeemed(boolean isRedeemed) { this.isRedeemed = isRedeemed; }
    public Date getCreatedAt() { return createdAt; }
    public ObjectId getRedeemedBy() { return redeemedBy; }
    public void setRedeemedBy(ObjectId redeemedBy) { this.redeemedBy = redeemedBy; }
}
    
