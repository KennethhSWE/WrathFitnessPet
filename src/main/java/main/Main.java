package main;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Wrath Fitness Pet!");

        // OAuth flow for Strava API use
        System.out.println("Authorize the app by visiting : " + StravaAuth.getAuthUrl());
        StravaAuth.startOAuthServer();

        // Create a new pet
        Pet myPet = new Pet("Fury");

        // Interact with the pet
        myPet.feed();
        myPet.drink();
    }
}
