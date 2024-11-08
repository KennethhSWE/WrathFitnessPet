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

// Sign-up function (assumed to be the same as in previous code)

// Enhanced login function with error handling
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

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
    }
}

// Log workout function with error handling and feedback
async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const dailyGoal = document.getElementById('dailyGoal').checked;
    const streakBonus = document.getElementById('streakBonus').checked;

    if (!weight || !reps) {
        document.getElementById('workout-message').innerText = "Please enter both weight and reps.";
        return;
    }

    const formData = new URLSearchParams();
    formData.append('weight', weight);
    formData.append('reps', reps);
    formData.append('dailyGoal', dailyGoal);
    formData.append('streakBonus', streakBonus);

    try {
        const response = await fetch('/logWorkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        if (response.ok) {
            document.getElementById('workout-message').innerText = "Workout logged successfully!";
            fetchWorkoutHistory();
            fetchUserStats(); // Refresh stats to reflect XP gain and level up if applicable
        } else {
            document.getElementById('workout-message').innerText = "Failed to log workout. Please try again.";
        }
    } catch (error) {
        console.error("Error logging workout:", error);
        document.getElementById('workout-message').innerText = "An error occurred. Try again.";
    }
}

// Fetch workout history
async function fetchWorkoutHistory() {
    try {
        const response = await fetch('/getWorkoutHistory', { method: 'GET' });
        if (response.ok) {
            const workouts = await response.json();
            const workoutHistory = document.getElementById('workout-history');
            workoutHistory.innerHTML = ''; // Clear previous history

            workouts.forEach(workout => {
                const workoutEntry = document.createElement('p');
                workoutEntry.innerText = `Date: ${new Date(workout.timestamp).toLocaleString()}, 
                                          Weight: ${workout.weight} lbs, Reps: ${workout.reps}, 
                                          Daily Goal: ${workout.dailyGoal ? "Yes" : "No"}, Streak Bonus: ${workout.streakBonus ? "Yes" : "No"}`;
                workoutHistory.appendChild(workoutEntry);
            });
        } else {
            document.getElementById('workout-message').innerText = "Error retrieving workout history.";
        }
    } catch (error) {
        console.error("Error fetching workout history:", error);
        document.getElementById('workout-message').innerText = "An error occurred. Try again.";
    }
}

// Request Password Reset
async function requestPasswordReset() {
    const email = document.getElementById('reset-email').value;

    if (!email) {
        document.getElementById('reset-message').innerText = "Please enter your email address.";
        return;
    }

    try {
        const response = await fetch('/reset-password', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ email })
        });

        if (response.ok) {
            document.getElementById('reset-message').innerText = "A reset link has been sent to your email.";
        } else {
            document.getElementById('reset-message').innerText = "Failed to send reset link. Please try again.";
        }
    } catch (error) {
        console.error("Error requesting password reset:", error);
        document.getElementById('reset-message').innerText = "An error occurred. Try again.";
    }
}

// Logout function
async function logout() {
    try {
        const response = await fetch('/logout', { method: 'POST' });
        if (response.ok) {
            alert("You have been logged out.");
            showLogin();
        } else {
            alert("Logout failed, please try again.");
        }
    } catch (error) {
        console.error("Error during logout:", error);
    }
}
