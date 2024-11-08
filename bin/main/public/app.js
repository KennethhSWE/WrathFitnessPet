// Sign-Up Function for New Members
async function signUp() {
    const name = document.getElementById('name').value;
    const age = document.getElementById('age').value;
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const fitnessGoal = document.getElementById('fitness-goal').value;

    // Password validation
    if (password !== confirmPassword) {
        document.getElementById('password-error').innerText = "Passwords do not match. Please re-enter.";
        return;
    } else {
        document.getElementById('password-error').innerText = ""; // Clear any previous error
    }

    const response = await fetch('/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, age, username, email, password, fitnessGoal })
    });

    if (response.ok) {
        alert("Account created successfully! Please log in.");
        showLogin(); // Redirect to login screen
    } else if (response.status === 409) {
        alert("Username already exists. Please choose a different one.");
    } else {
        alert('Sign-up failed, please try again.');
    }
}

// Function to show the login screen
function showLogin() {
    document.getElementById('registration-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'block';
}

// Login Function
async function login() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const response = await fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ username, password })
    });

    const message = await response.text();
    document.getElementById('login-message').innerText = message;

    if (response.ok) {
        // Hide login and registration sections
        document.getElementById('registration-container').style.display = 'none';
        document.getElementById('login-section').style.display = 'none';

        // Show dashboard sections
        document.getElementById('dashboard').style.display = 'block';
        document.getElementById('avatar-section').style.display = 'block';
        document.getElementById('workout-section').style.display = 'block';
        document.getElementById('workout-history-section').style.display = 'block';

        // Fetch and display avatar data
        await displayAvatarData();
        await viewWorkoutHistory(); // Load workout history on successful login
    } else {
        alert("Login failed: " + message);
    }
}

// Logout Function
async function logout() {
    const response = await fetch('/logout', { method: 'POST' });
    if (response.ok) {
        alert("You have been logged out.");
        document.getElementById('login-section').style.display = 'block';
        document.getElementById('dashboard').style.display = 'none'; // Hide dashboard on logout
        document.getElementById('login-message').innerText = ""; // Clear any login message
    } else {
        alert("Logout failed, please try again.");
    }
}

// Display Avatar Data Function
async function displayAvatarData() {
    const response = await fetch('/getAvatar');

    if (response.ok) {
        const avatar = await response.json();
        document.getElementById('avatar-name').innerText = avatar.name;
        document.getElementById('avatar-level').innerText = avatar.level;
        document.getElementById('avatar-xp').innerText = avatar.xp;
        document.getElementById('avatar-xp-next').innerText = avatar.xpToNextLevel;
        document.getElementById('avatar-strength').innerText = avatar.strength;
        document.getElementById('avatar-stamina').innerText = avatar.stamina;
        document.getElementById('avatar-cardio').innerText = avatar.cardioHealth;
    } else {
        alert("Failed to load avatar data.");
    }
}

// Log Workout Function
async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const hitDailyGoal = document.getElementById('dailyGoal').checked;
    const hitStreakBonus = document.getElementById('streakBonus').checked;

    const response = await fetch('/logWorkout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ weight, reps, hitDailyGoal, hitStreakBonus })
    });

    const message = await response.text();
    document.getElementById('workout-message').innerText = message;

    if (response.ok) {
        await displayAvatarData(); // Refresh avatar stats after workout
        await viewWorkoutHistory(); // Refresh workout history
    } else {
        alert("Failed to log workout: " + message);
    }
}

// View Workout History Function
async function viewWorkoutHistory() {
    const response = await fetch('/view-workouts');
    const workoutHistoryDiv = document.getElementById('workout-history');
    workoutHistoryDiv.innerHTML = ''; // Clear previous history

    if (response.ok) {
        const workoutHistoryText = await response.text();
        const workouts = workoutHistoryText.split("\n");
        
        workouts.forEach(entry => {
            if (entry.trim() !== "") { // Only append non-empty entries
                const p = document.createElement('p');
                p.textContent = entry;
                workoutHistoryDiv.appendChild(p);
            }
        });
    } else {
        alert("Failed to load workout history.");
    }
}

// Set Avatar Appearance Function
async function setAvatarAppearance() {
    const outfitChoice = document.getElementById('outfit-choice').value;

    const response = await fetch('/set-avatar-appearance', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ outfitChoice })
    });

    if (response.ok) {
        alert("Avatar appearance updated!");
        await displayAvatarData(); // Refresh avatar data after customization
    } else {
        alert("Failed to update avatar appearance.");
    }
}
