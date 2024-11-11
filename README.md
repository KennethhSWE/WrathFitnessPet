Hero Academy Gym
Hero Academy Gym is a fitness-oriented app designed to make workouts engaging and motivating by allowing gym members to level up a virtual character based on their real-world fitness activities. This app integrates RPG-style elements, where users can build stats and skills for their in-app avatar by consistently working out, logging their progress, and hitting specific fitness goals.

Integrated with the Strava API for outdoor activities and storing fitness data in MongoDB, Hero Academy Gym aims to create an immersive, gamified fitness experience exclusive to members of a partnered gym. Members can scan QR codes on gym equipment, log exercises, and track achievements, while the app calculates bonuses based on consistency, streaks, and off-gym activities.

Features
Core Features
Strava OAuth Integration: Securely connect to Strava to track outdoor activities, such as running and cycling, and log fitness data in real-time.
Character Progression System: Each user is assigned an RPG-style character that mirrors their real-world stats. As users meet fitness goals, their avatar gains experience points (XP), levels up, and develops stats such as strength, stamina, and cardio health.
Workout Logging & Stat Building: Log gym workouts by scanning QR codes on equipment. Track weights, reps, and other metrics to increase avatar stats.
Dashboard with RPG Stat Sheet: The main dashboard shows a professional RPG-style stat sheet with the user's avatar, level, and skills, giving members a visual way to see their progress.
Avatar Battle Mode (Future Feature): Members can battle their avatars against others in the gym. An "equalizer mode" allows for balanced fights based on each user’s gym tenure, making it fair for newcomers.
Game Mechanics
Buffs & Debuffs: Buffs are awarded for consecutive workouts, hitting goals, and completing off-gym activities tracked by Strava. Debuffs apply if users miss gym sessions or go inactive.
Avatar Preview and Customization: Members can view an animated 3D avatar, customized to reflect their progress. Future customization features will allow users to modify their avatar’s appearance and select roles like warrior, mage, or rogue.
Progress Tracking and Leaderboards: Track progress over time with a workout history and see how you rank among other gym members.
Achievements and Goals: Set and achieve fitness goals (e.g., weight loss, muscle gain), and unlock rewards and achievements to keep users motivated.
Getting Started
Prerequisites
Before you begin, ensure you have the following setup:

Java 21+ installed on your machine.
MongoDB Atlas account with a created database cluster.
Strava API Client ID and Secret for authenticating users and retrieving fitness data.
Installation
1. Clone the Repository:
bash
Copy code
git clone https://github.com/[YourGitHubUsername]/HeroAcademyGym.git
cd HeroAcademyGym
2. Install Dependencies
This project uses Gradle to manage dependencies.

bash
Copy code
./gradlew clean build
3. Set Environment Variables
Set the following environment variables in a .env file or as system variables:

MONGO_DB_URI: Your MongoDB URI.
STRAVA_CLIENT_ID: Your Strava Client ID.
STRAVA_CLIENT_SECRET: Your Strava Client Secret.
Example .env file:

bash
Copy code
MONGO_DB_URI="mongodb+srv://<username>:<password>@cluster.mongodb.net/<database>?retryWrites=true&w=majority"
STRAVA_CLIENT_ID="your_strava_client_id"
STRAVA_CLIENT_SECRET="your_strava_client_secret"
4. Run the App
Launch the server and authenticate through Strava:

bash
Copy code
./gradlew run
Project Structure
plaintext
Copy code
HeroAcademyGym/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── main/
│   │   │   │   ├── Main.java                 # Main entry point for the app
│   │   │   │   ├── User.java                 # User profile with attributes and stats
│   │   │   │   ├── StravaAuth.java           # Strava OAuth integration and token management
│   │   ├── resources/
│   │   │   ├── public/
│   │   │   │   ├── assets/                   # Contains images, avatars, and models
│   │   │   │   │   └── models/
│   │   │   │   │       └── Idle.fbx          # 3D character model for user avatars
│   ├── app.js                                # Frontend functionality and Three.js setup for avatar animation
├── build.gradle                              # Gradle build configuration
├── README.md                                 # This README file
└── .gitattributes                            # Git LFS tracking configuration for large files
Avatar and Animation Integration
The Hero Academy Gym app utilizes Three.js to render and animate the user's 3D avatar. The avatar is currently set to display an animated .fbx model (idle animation) located in the assets/models directory. Future improvements will allow users to customize their avatar's appearance and roles in a more interactive RPG-style dashboard.

Future Plans
Avatar Customization: Expand options to customize avatars with different outfits, roles (warrior, mage, etc.), and animations.
Mini-Games and Battles: Create a competitive mode where members can battle avatars against each other or participate in mini-games to earn rewards.
Achievements & Leaderboards: Add daily, weekly, and long-term goal tracking, with rewards and leaderboards for top performers.
Gym-Specific Bonuses: Introduce unique bonuses for users who frequently visit the gym or reach milestones in their workout consistency.
Mobile App Support: Develop a companion mobile app for both Android and iOS to enhance accessibility and convenience.
Contributing
Contributions are welcome! Please fork the repository and create a pull request for major features or bug fixes. For large changes, open an issue first to discuss the proposed feature.

License
This project is licensed under the MIT License. See the LICENSE file for details.

Contact
For questions, suggestions, or collaboration requests, feel free to reach out on GitHub or open an issue in this repository.

This README reflects the Hero Academy Gym app's current features and future goals, giving a comprehensive overview for users and contributors. Let me know if any additional details should be added!



