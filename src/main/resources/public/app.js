<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Cinzel:wght@600;700&family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <title>Hero Academy Gym - Welcome</title>
    <style>
        body {
            font-family: 'Open Sans', sans-serif;
            background-image: url("assets/Backgrounds/HeroAcademy_BackGroud.png");
            background-size: cover;
            background-position: center;
            color: #f0f0f0;
        }
        .background-overlay {
            background-color: rgba(0, 0, 0, 0.7);
            padding: 20px;
            min-height: 100vh;
        }
        .header {
            text-align: center;
            margin-bottom: 40px;
        }
        .logo {
            height: 150px;
        }
        h1 {
            font-family: 'Cinzel', serif;
            font-size: 2.5em;
            color: #FFD700;
            margin-bottom: 0;
        }
        .card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0px 10px 20px rgba(0, 0, 0, 0.5);
            max-width: 500px;
            margin: 20px auto;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
        }
        input, select {
            width: calc(100% - 10px);
            padding: 10px;
            border: none;
            border-radius: 5px;
            font-size: 1em;
            background: #333;
            color: #fff;
        }
        button {
            background-color: #ff4500;
            color: #fff;
            padding: 12px 20px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: bold;
            width: 100%;
            font-size: 1em;
            margin-top: 10px;
            transition: background-color 0.3s;
        }
        button:hover {
            background-color: #ff6a00;
        }
        .alt-button {
            background: none;
            color: #FFD700;
            border: 2px solid #FFD700;
            margin-top: 20px;
        }
        footer {
            text-align: center;
            margin-top: 50px;
            color: #ddd;
        }
        .error-message {
            color: #ff6347;
            font-size: 0.9em;
            margin-top: -10px;
        }
        @media (min-width: 768px) {
            .header {
                margin-bottom: 60px;
            }
            .card {
                max-width: 600px;
            }
        }
    </style>
</head>

<body>
    <div class="background-overlay">
        <header class="header">
            <img src="assets/logos/200x300_GymLogo.png" alt="Hero Academy Gym Logo" class="logo">
            <h1>Hero Academy Gym</h1>
        </header>

        <!-- Background Feature (e.g., Character or Scene) -->
        <div class="background-feature"></div>

        <!-- Registration Section for New Members -->
        <div class="main-container" id="registration-container">
            <div class="card">
                <h2>Welcome to Hero Academy Gym!</h2>
                <p>Join us and start your journey to a stronger you!</p>

                <h3>Set Up Your Profile</h3>
                <form id="signupForm">
                    <div class="form-group">
                        <label for="name"><i class="fas fa-user"></i> Full Name</label>
                        <input type="text" id="name" placeholder="Name" required>
                    </div>
                    <div class="form-group">
                        <label for="age"><i class="fas fa-birthday-cake"></i> Age</label>
                        <input type="number" id="age" placeholder="Age" required>
                    </div>
                    <div class="form-group">
                        <label for="register-username"><i class="fas fa-user"></i> Username</label>
                        <input type="text" id="register-username" placeholder="Username" required>
                    </div>
                    <div class="form-group">
                        <label for="register-email"><i class="fas fa-envelope"></i> Email</label>
                        <input type="email" id="register-email" placeholder="Email" required>
                    </div>
                    <div class="form-group">
                        <label for="register-password"><i class="fas fa-lock"></i> Password</label>
                        <input type="password" id="register-password" placeholder="Password" required>
                    </div>
                    <div class="form-group">
                        <label for="confirm-password"><i class="fas fa-lock"></i> Confirm Password</label>
                        <input type="password" id="confirm-password" placeholder="Confirm Password" required>
                        <p id="password-error" class="error-message"></p>
                    </div>
                    <div class="form-group">
                        <label for="fitness-goal"><i class="fas fa-dumbbell"></i> Select Your Fitness Goal</label>
                        <select id="fitness-goal" required>
                            <option value="lose-weight">Lose Weight</option>
                            <option value="build-muscle">Build Muscle</option>
                            <option value="top-leaderboard">Top the Leaderboard</option>
                            <option value="be-healthier">Be Healthier</option>
                        </select>
                    </div>
                    <button type="button" class="onboarding-button" onclick="signUp()">Create Account</button>
                    <p id="registration-message" class="error-message"></p> <!-- New feedback message element -->
                </form>
                <button class="alt-button" onclick="showLogin()">Already a Member?</button>
            </div>
        </div>

        <!-- Login Section -->
        <div class="main-container" id="login-section" style="display: none;">
            <div class="card">
                <h2>Login</h2>
                <form id="loginForm">
                    <div class="form-group">
                        <label for="login-username"><i class="fas fa-user"></i> Username</label>
                        <input type="text" id="login-username" placeholder="Username" required>
                    </div>
                    <div class="form-group">
                        <label for="login-password"><i class="fas fa-lock"></i> Password</label>
                        <input type="password" id="login-password" placeholder="Password" required>
                    </div>
                    <button type="button" onclick="login()">Log In</button>
                    <p id="login-message" class="error-message"></p>
                </form>

                <!-- Forgot Password Link -->
                <p class="forgot-password" onclick="showForgotPassword()">Forgot Password?</p>

                <!-- Forgot Password Form -->
                <div id="forgot-password-container" style="display: none;">
                    <h3>Reset Your Password</h3>
                    <form id="resetPasswordForm">
                        <div class="form-group">
                            <label for="reset-email"><i class="fas fa-envelope"></i> Enter your email</label>
                            <input type="email" id="reset-email" placeholder="Email" required>
                        </div>
                        <button type="button" onclick="requestPasswordReset()">Send Reset Link</button>
                        <p id="reset-message" class="message"></p>
                    </form>
                </div>
            </div>
        </div>

        <!-- Main Dashboard -->
        <div class="main-container" id="dashboard" style="display: none;">
            <div class="card">
                <h2>Welcome, <span id="username-display"></span>!</h2>
                <button onclick="logout()">Log Out</button>
                
                <div class="dashboard-grid">
                    <!-- Avatar Card -->
                    <div id="avatar-section" class="dashboard-card">
                        <h3>Your Avatar</h3>
                        <p><strong>Name:</strong> <span id="avatar-name"></span></p>
                        <p><strong>Level:</strong> <span id="avatar-level">1</span></p>
                        
                        <!-- XP Progress Bar -->
                        <div class="xp-progress-container">
                            <span id="current-level">1</span>
                            <div class="xp-progress-bar-container">
                                <div class="xp-progress-bar" id="xp-progress-bar"></div>
                            </div>
                            <span id="next-level">2</span>
                        </div>
                        <p><span id="xp-progress-text">0% to next level</span></p>
                        
                        <p><strong>Strength:</strong> <span id="avatar-strength">50</span></p>
                        <p><strong>Stamina:</strong> <span id="avatar-stamina">50</span></p>
                        <p><strong>Cardio Health:</strong> <span id="avatar-cardio">50</span></p>
                    </div>

                    <!-- Workout Logging Card -->
                    <div id="workout-section" class="dashboard-card">
                        <h3>Log Your Workout</h3>
                        <form id="logWorkoutForm">
                            <div class="form-group">
                                <label for="weight"><i class="fas fa-weight"></i> Weight (lbs):</label>
                                <input type="number" id="weight" placeholder="Enter weight used" required>
                            </div>
                            <div class="form-group">
                                <label for="reps"><i class="fas fa-redo"></i> Reps:</label>
                                <input type="number" id="reps" placeholder="Enter reps" required>
                            </div>
                            <div class="form-group">
                                <label for="dailyGoal"><i class="fas fa-check"></i> Hit Daily Goal:</label>
                                <input type="checkbox" id="dailyGoal">
                            </div>
                            <div class="form-group">
                                <label for="streakBonus"><i class="fas fa-bolt"></i> Hit Streak Bonus:</label>
                                <input type="checkbox" id="streakBonus">
                            </div>
                            <button type="button" onclick="logWorkout()">Submit Workout</button>
                            <p id="workout-message"></p>
                        </form>
                    </div>

                    <!-- Workout History Card -->
                    <div id="workout-history-section" class="dashboard-card">
                        <h3>Workout History</h3>
                        <div id="workout-history">
                            <!-- Past workouts will be dynamically inserted here -->
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <footer>
            <p>&copy; 2024 Hero Academy Gym. All rights reserved.</p>
        </footer>
    </div>

    <!-- JavaScript -->
    <script src="app.js"></script>
</body>

</html>
