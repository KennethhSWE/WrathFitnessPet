// JavaScript to handle the Hero Academy Gym app

// Check if user is already logged in on page load
window.onload = async function() {
    try {
        const response = await fetch('/check-session', { method: 'GET' });
        if (response.ok) {
            const username = await response.text();
            document.getElementById('username-display').innerText = username;
            showDashboard();
            fetchWorkoutHistory();
            fetchUserStats(); // Load user stats and XP progress on dashboard load
        } else {
            showRegistration();
        }
    } catch (error) {
        console.error("Error checking session:", error);
        showRegistration();
    }
};

// Show the dashboard and hide other sections
function showDashboard() {
    document.getElementById('registration-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('dashboard').style.display = 'block';
}

// Show the login screen and hide other sections
function showLogin() {
    document.getElementById('registration-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'block';
    document.getElementById('dashboard').style.display = 'none';
    document.getElementById('forgot-password-container').style.display = 'none';
}

// Show the registration screen and hide other sections
function showRegistration() {
    document.getElementById('registration-container').style.display = 'block';
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('dashboard').style.display = 'none';
}

// Show the forgot password form
function showForgotPassword() {
    document.getElementById('forgot-password-container').style.display = 'block';
    document.getElementById('login-section').style.display = 'none';
}

// Fetch user stats including level and XP to dynamically update the dashboard
async function fetchUserStats() {
    try {
        const response = await fetch('/user-stats', { method: 'GET' });
        if (response.ok) {
            const { level, xp, xpToNextLevel } = await response.json();
            updateXPProgress(level, xp, xpToNextLevel);
        } else {
            console.error("Failed to fetch user stats");
        }
    } catch (error) {
        console.error("Error fetching user stats:", error);
    }
}

// Update XP Progress UI dynamically
function updateXPProgress(level, xp, xpToNextLevel) {
    document.getElementById('avatar-level').innerText = level;
    document.getElementById('avatar-xp').innerText = xp;
    document.getElementById('avatar-xp-next').innerText = xpToNextLevel;
    
    const progressPercentage = (xp / xpToNextLevel) * 100;
    document.getElementById('xp-progress-bar').style.width = `${progressPercentage}%`;
    document.getElementById('xp-progress-text').innerText = `${progressPercentage.toFixed(0)}% to next level`;
}

// Enhanced sign-up function with code validation
async function signUp() {
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const email = document.getElementById('register-email').value;
    const signUpCode = prompt("Please enter your sign-up code:");

    // Basic input validation
    if (!username || !password || !email || !signUpCode) {
        document.getElementById('registration-message').innerText = "Please fill in all fields and enter the sign-up code.";
        return;
    }

    if (password !== confirmPassword) {
        document.getElementById('password-error').innerText = "Passwords do not match.";
        return;
    } else {
        document.getElementById('password-error').innerText = "";
    }

    try {
        const response = await fetch('/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, email, signUpCode })
        });

        if (response.ok) {
            document.getElementById('registration-message').innerText = "Account created successfully!";
            showLogin();
        } else {
            const message = await response.text();
            document.getElementById('registration-message').innerText = message;
        }
    } catch (error) {
        console.error("Error during sign-up:", error);
        document.getElementById('registration-message').innerText = "An error occurred. Please try again.";
    }
}

// Enhanced login function with error handling
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    if (!username || !password) {
        document.getElementById('login-message').innerText = "Please fill in all fields.";
        return;
    }

    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ username, password })
        });

        if (response.ok) {
            document.getElementById('username-display').innerText = username;
            showDashboard();
            fetchWorkoutHistory();
            fetchUserStats();
        } else {
            const message = await response.text();
            document.getElementById('login-message').innerText = message;
        }
    } catch (error) {
        console.error("Error during login:", error);
        document.getElementById('login-message').innerText = "An error occurred. Please try again.";
    }
}

// Log workout function with error handling and feedback
async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const dailyGoal = document.getElementById('dailyGoal').checked;
    const streakBonus = document.getElementById('streakBonus').checked;

    if (!weight || !reps) {
        document.getElementById('workout-message').innerText = "Please fill in the weight and reps.";
        return;
    }

    try {
        const response = await fetch('/logWorkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ weight, reps, dailyGoal, streakBonus })
        });

        if (response.ok) {
            document.getElementById('workout-message').innerText = "Workout logged successfully!";
            fetchWorkoutHistory();
        } else {
            const message = await response.text();
            document.getElementById('workout-message').innerText = message;
        }
    } catch (error) {
        console.error("Error logging workout:", error);
        document.getElementById('workout-message').innerText = "An error occurred. Please try again.";
    }
}

// Fetch workout history
async function fetchWorkoutHistory() {
    try {
        const response = await fetch('/getWorkoutHistory', { method: 'GET' });
        if (response.ok) {
            const workoutHistory = await response.json();
            const historyElement = document.getElementById('workout-history');
            historyElement.innerHTML = workoutHistory.map(workout => {
                return `<p>${new Date(workout.timestamp).toLocaleString()} - Weight: ${workout.weight} lbs, Reps: ${workout.reps}</p>`;
            }).join("");
        } else {
            console.error("Failed to fetch workout history");
        }
    } catch (error) {
        console.error("Error fetching workout history:", error);
    }
}

// Request Password Reset
async function requestPasswordReset() {
    const email = document.getElementById('reset-email').value;

    if (!email) {
        document.getElementById('reset-message').innerText = "Please enter your email.";
        return;
    }

    try {
        const response = await fetch('/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        if (response.ok) {
            document.getElementById('reset-message').innerText = "Password reset link sent!";
        } else {
            const message = await response.text();
            document.getElementById('reset-message').innerText = message;
        }
    } catch (error) {
        console.error("Error requesting password reset:", error);
        document.getElementById('reset-message').innerText = "An error occurred. Please try again.";
    }
}

// Logout function
async function logout() {
    try {
        const response = await fetch('/logout', { method: 'POST' });
        if (response.ok) {
            showRegistration();
        } else {
            console.error("Failed to log out");
        }
    } catch (error) {
        console.error("Error during logout:", error);
    }
}
