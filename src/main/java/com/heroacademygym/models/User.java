package com.heroacademygym.models;

public class User {
    private String username;
    private String password;
    private String email;
    private int strength;      // Added directly to the User class
    private int stamina;       // Added directly to the User class
    private int cardioHealth;  // Added directly to the User class

    // Constructor that includes email and attributes
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;

        // Initialize default attributes for new users
        this.strength = 50;       // Default value
        this.stamina = 50;        // Default value
        this.cardioHealth = 50;   // Default value
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
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
}
