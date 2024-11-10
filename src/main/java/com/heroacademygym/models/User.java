package com.heroacademygym.models;

public class User {
    private String username;
    private String password;
    private String email; // Added email field

    // Constructor that includes email
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Existing constructor with two parameters, in case it's needed elsewhere in your code
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() { // Added getEmail method
        return email;
    }

    // Setters if needed (optional)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
