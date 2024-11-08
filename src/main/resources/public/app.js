// Check if user is already logged in on page load
window.onload = async function() {
    try {
        const response = await fetch('/check-session', { method: 'GET' });
        if (response.ok) {
            const username = await response.text();
            document.getElementById('username-display').innerText = username;
            showDashboard();
            fetchWorkoutHistory(); // Load workout history on dashboard load
            updateXpProgress(); // Initialize XP bar on page load
        } else {
            // If not logged in, show registration screen by default
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
}

// Show the registration screen and hide other sections
function showRegistration() {
    document.getElementById('registration-container').style.display = 'block';
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('dashboard').style.display = 'none';
}

// Sign-up function
async function signUp() {
    const name = document.getElementById('name').value;
    const age = document.getElementById('age').value;
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const fitnessGoal = document.getElementById('fitness-goal').value;

    if (password !== confirmPassword) {
        document.getElementById('password-error').innerText = "Passwords do not match. Please re-enter.";
        return;
    }

    const formData = new URLSearchParams();
    formData.append('name', name);
    formData.append('age', age);
    formData.append('username', username);
    formData.append('email', email);
    formData.append('password', password);
    formData.append('fitnessGoal', fitnessGoal);

    try {
        const response = await fetch('/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        if (response.ok) {
            alert("Account created successfully! Redirecting to login page.");
            showLogin();
        } else if (response.status === 409) {
            alert("Username already exists. Please choose a different one.");
        } else {
            alert('Sign-up failed, please try again.');
        }
    } catch (error) {
        console.error("Error during sign-up:", error);
        alert("An error occurred. Please try again later.");
    }
}

// Login function
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
            fetchWorkoutHistory(); // Load workout history on login
            updateXpProgress(); // Update XP progress on login
        } else {
            const message = await response.text();
            document.getElementById('login-message').innerText = message;
        }
    } catch (error) {
        console.error("Error during login:", error);
    }
}

// Logout function
async function logout() {
    const response = await fetch('/logout', { method: 'POST' });
    if (response.ok) {
        alert("You have been logged out.");
        showLogin();
    } else {
        alert("Logout failed, please try again.");
    }
}

// Log workout function
async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const dailyGoal = document.getElementById('dailyGoal').checked;
    const streakBonus = document.getElementById('streakBonus').checked;

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
            fetchWorkoutHistory(); // Refresh workout history
            gainXp(10); // Example: Gain 10 XP for each logged workout
        } else {
            document.getElementById('workout-message').innerText = "Failed to log workout. Try again.";
        }
    } catch (error) {
        console.error("Error logging workout:", error);
        document.getElementById('workout-message').innerText = "An error occurred. Try again.";
    }
}

// XP and Level System
let currentXp = 0;
let currentLevel = 1;
const xpToNextLevel = 100;

function gainXp(amount) {
    currentXp += amount;
    if (currentXp >= xpToNextLevel) {
        levelUp();
    }
    updateXpProgress();
}

function levelUp() {
    currentXp = currentXp - xpToNextLevel;
    currentLevel++;
    document.getElementById('avatar-level').innerText = currentLevel;
}

function updateXpProgress() {
    const progressPercentage = (currentXp / xpToNextLevel) * 100;
    document.getElementById('progress-bar').style.width = `${progressPercentage}%`;
    document.getElementById('progress-percentage').innerText = `${Math.floor(progressPercentage)}% Complete`;
    document.getElementById('current-level').innerText = currentLevel;
    document.getElementById('next-level').innerText = currentLevel + 1;
}

// Fetch workout history function
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
