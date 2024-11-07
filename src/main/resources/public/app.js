// Sign-Up Function for New Members
async function signUp() {
    const email = document.getElementById('email').value;
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const name = document.getElementById('name').value;
    const age = document.getElementById('age').value;
    const fitnessGoal = document.getElementById('fitness-goal').value;

    // Send sign-up data to server (replace '/signup' with actual API endpoint)
    const response = await fetch('/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, username, password, name, age, fitnessGoal })
    });

    if (response.ok) {
        // Direct to login screen or dashboard upon successful account creation
        alert("Account created successfully! Please log in.");
        nextScreen('login-section'); // Navigate to the login screen
    } else {
        alert('Sign-up failed, please try again.');
    }
}

// Login Function for Returning Members
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    // Authenticate with server
    const response = await fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });

    const message = await response.text();
    document.getElementById('login-message').innerText = message;

    if (response.ok) {
        // Hide login form and display avatar preview and stats on successful login
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('avatar-preview').style.display = 'block';
        // Optionally, load additional user data (e.g., workouts) here
    } else {
        alert('Login failed, please check your credentials.');
    }
}

// Log Workout Function
async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const hitDailyGoal = document.getElementById('dailyGoal').checked;
    const hitStreakBonus = document.getElementById('streakBonus').checked;

    const response = await fetch('/workout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ weight, reps, hitDailyGoal, hitStreakBonus })
    });

    const message = await response.text();
    document.getElementById('workout-message').innerText = message;
}

// View Avatar Stats Function
async function viewAvatar() {
    const response = await fetch('/view-avatar');
    const stats = await response.text();
    document.getElementById('avatar-stats').innerText = stats;
}

// Onboarding Navigation Functions
function nextScreen(nextScreenId) {
    // Hide the currently active screen
    const activeScreen = document.querySelector('.onboarding-active');
    if (activeScreen) {
        activeScreen.classList.remove('onboarding-active');
    }

    // Show the next screen
    const nextScreen = document.getElementById(nextScreenId);
    if (nextScreen) {
        nextScreen.classList.add('onboarding-active');
    }
}

function finishOnboarding() {
    // Hide the onboarding container
    document.getElementById('onboarding-container').style.display = 'none';

    // Show the login section for returning users
    document.getElementById('login-section').style.display = 'block';
}
