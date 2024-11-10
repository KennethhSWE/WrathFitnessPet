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

// Sign-up function
async function signUp() {
    const username = document.getElementById('register-username').value;
    const password = document.getElementById('register-password').value;
    const email = document.getElementById('register-email').value;

    // Basic input validation
    if (!username || !password || !email) {
        document.getElementById('registration-message').innerText = "Please fill in all fields.";
        return;
    }

    try {
        const response = await fetch('/create-user', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, email })
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
    // ... (No changes needed)
}

// Fetch workout history
async function fetchWorkoutHistory() {
    // ... (No changes needed)
}

// Request Password Reset
async function requestPasswordReset() {
    // ... (No changes needed)
}

// Logout function
async function logout() {
    // ... (No changes needed)
}

