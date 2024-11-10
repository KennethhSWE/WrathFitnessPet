package com.heroacademygym.models;

import org.mindrot.jbcrypt.BCrypt;

public class User {
    private String username;
    private String passwordHash; // Store hashed password
    private String email;
    private int strength;      // Added directly to the User class
    private int stamina;       // Added directly to the User class
    private int cardioHealth;  // Added directly to the User class

    // Constructor that includes email and attributes
    public User(String username, String passwordHash, String email) {
        this.username = username;
        this.passwordHash = passwordHash; // Store hashed password directly
        this.email = email;

        // Initialize default attributes for new users
        this.strength = 50;       // Default value
        this.stamina = 50;        // Default value
        this.cardioHealth = 50;   // Default value
    }

    // Password hashing method
    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Method to verify password
    public boolean verifyPassword(String plainPassword) {
        return BCrypt.checkpw(plainPassword, this.passwordHash);
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public int getStrength() {
        return strength;
    }

    public int getStamina() {
        return stamina;
    }

    public int getCardioHealth() {
        return cardioHealth;
    }

    // Setters for attributes if needed
    public void setStrength(int strength) {
        this.strength = strength;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void setCardioHealth(int cardioHealth) {
        this.cardioHealth = cardioHealth;
    }

    // Setters for sensitive data (e.g., password)
    public void setPassword(String newPassword) {
        this.passwordHash = hashPassword(newPassword);
    }
}
