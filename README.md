WrathFitnessPet--
WrathFitnessPet is a fitness-oriented app where users can nurture a virtual pet through physical activities like walking, running, or exercising. As users achieve their fitness goals, the virtual pet gains experience points, levels up, and evolves, making fitness engaging and interactive. Integrated with the Strava API, the app tracks users' physical activities and stores fitness data securely in MongoDB.

Features
Strava OAuth Integration: Securely authenticate with Strava to track and log fitness activities.
Virtual Pet Mechanism: Feed, hydrate, and help your pet grow by meeting fitness goals.
MongoDB Data Storage: Store and manage users' fitness data and pet statistics.
Backend First Development: Strong focus on reliable backend with Strava API, MongoDB integration, and fitness tracking.
Extensible Design: Future expansions planned for user goals, statistics, progress tracking, and mini-games.
Getting Started
Prerequisites
Before you begin, ensure you have met the following requirements:

Java 21+ is installed on your machine.
MongoDB Atlas account set up and a database cluster is created.
Strava API Client ID and Secret are required to authenticate users and pull fitness data.
Installation
Clone the Repository:

bash
Copy code
git clone https://github.com/[YourGitHubUsername]/WrathFitnessPet.git
cd WrathFitnessPet
Install Dependencies: This project uses Gradle to manage dependencies.

bash
Copy code
./gradlew clean build
Set Environment Variables: Set the following environment variables for MongoDB and Strava API in a .env file or system variables.

MONGO_DB_URI: Your MongoDB URI.
STRAVA_CLIENT_ID: Your Strava Client ID.
STRAVA_CLIENT_SECRET: Your Strava Client Secret.
Example:

bash
Copy code
export MONGO_DB_URI="mongodb+srv://<username>:<password>@<cluster-url>/"
export STRAVA_CLIENT_ID="<your-client-id>"
export STRAVA_CLIENT_SECRET="<your-client-secret>"
Run the App: Launch the server and authenticate through Strava:

bash
Copy code
./gradlew run
Strava API Integration
WrathFitnessPet uses the Strava API to retrieve users' fitness data. Once authenticated, Strava sends an authorization code, which the backend exchanges for an access token. The app then logs fitness data (steps, calories, etc.) to level up the virtual pet.

MongoDB Integration
The app uses MongoDB Atlas to store user data securely:

Fitness activity data (steps, calories, etc.)
Pet evolution, XP, and user progress tracking
Ensure your MongoDB cluster is properly configured with the necessary collections before running the app.

Project Structure
bash
Copy code
WrathFitnessPet/
│
├── src/main/java/main/
│   ├── Main.java               # Main entry point for the app
│   ├── Pet.java                # Class representing the user's virtual pet
│   └── StravaAuth.java         # Handles Strava OAuth and token management
│
├── build.gradle                # Gradle build configuration
├── README.md                   # This readme file
├── .env.example                # Example for environment variables
└── LICENSE                     # MIT License
Future Plans
Fitness Goals & Achievements: Add daily, weekly, and long-term fitness goal tracking.
UI Development: Build a sleek and modern Android user interface.
Mini-games & Challenges: Allow users to battle pets or complete challenges using their pet’s stats.
Social Sharing: Enable users to share their progress and compete with friends.
Contributing
Contributions are welcome! Please fork the repository and create a pull request for any major feature or bug fix.

License
This project is licensed under the MIT License – see the LICENSE file for details.

Contact
For any questions, suggestions, or collaboration requests, reach out to me on github.
