// Sign-Up Function for New Members
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

// Show the login screen
function showLogin() {
    document.getElementById('registration-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'block';
    document.getElementById('dashboard').style.display = 'none';
}

// Login Function
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    const formData = new URLSearchParams();
    formData.append('username', username);
    formData.append('password', password);

    try {
        const response = await fetch('/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        const message = await response.text();
        if (response.ok) {
            alert("Login successful! Redirecting to dashboard.");
            document.getElementById('login-section').style.display = 'none';
            showDashboard(username);
        } else {
            document.getElementById('login-message').innerText = message;
        }
    } catch (error) {
        console.error("Error during login:", error);
        alert("An error occurred. Please try again later.");
    }
}

// Show Dashboard Function
function showDashboard(username) {
    document.getElementById('username-display').innerText = username;
    document.getElementById('dashboard').style.display = 'block';
    document.getElementById('login-section').style.display = 'none';
    document.getElementById('registration-container').style.display = 'none';

    // Fetch and display avatar data
    displayAvatarData();
    viewWorkoutHistory();
}

// Display Avatar Data Function
async function displayAvatarData() {
    try {
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
    } catch (error) {
        console.error("Error fetching avatar data:", error);
        alert("An error occurred. Please try again later.");
    }
}

// Log Workout Function
async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const hitDailyGoal = document.getElementById('dailyGoal').checked;
    const hitStreakBonus = document.getElementById('streakBonus').checked;

    const workoutData = {
        weight: weight,
        reps: reps,
        hitDailyGoal: hitDailyGoal,
        hitStreakBonus: hitStreakBonus
    };

    try {
        const response = await fetch('/logWorkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(workoutData)
        });

        const message = await response.text();
        if (response.ok) {
            document.getElementById('workout-message').innerText = "Workout logged successfully!";
            displayAvatarData(); // Refresh avatar stats after workout
            viewWorkoutHistory(); // Refresh workout history
        } else {
            alert("Failed to log workout: " + message);
        }
    } catch (error) {
        console.error("Error logging workout:", error);
        alert("An error occurred. Please try again later.");
    }
}

// View Workout History Function
async function viewWorkoutHistory() {
    try {
        const response = await fetch('/view-workouts');
        const workoutHistoryDiv = document.getElementById('workout-history');
        workoutHistoryDiv.innerHTML = ''; // Clear previous history

        if (response.ok) {
            const workoutHistoryText = await response.text();
            const workouts = workoutHistoryText.split("\n");

            workouts.forEach(entry => {
                if (entry.trim() !== "") {
                    const p = document.createElement('p');
                    p.textContent = entry;
                    workoutHistoryDiv.appendChild(p);
                }
            });
        } else {
            alert("Failed to load workout history.");
        }
    } catch (error) {
        console.error("Error fetching workout history:", error);
        alert("An error occurred. Please try again later.");
    }
}

// Set Avatar Appearance Function
async function setAvatarAppearance() {
    const outfitChoice = document.getElementById('outfit-choice').value;

    try {
        const response = await fetch('/set-avatar-appearance', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ outfitChoice })
        });

        if (response.ok) {
            alert("Avatar appearance updated!");
            displayAvatarData(); // Refresh avatar data after customization
        } else {
            alert("Failed to update avatar appearance.");
        }
    } catch (error) {
        console.error("Error updating avatar appearance:", error);
        alert("An error occurred. Please try again later.");
    }
}

// Logout Function
async function logout() {
    try {
        const response = await fetch('/logout', {
            method: 'POST'
        });

        if (response.ok) {
            alert("You have been logged out.");
            document.getElementById('dashboard').style.display = 'none';
            showLogin();
        } else {
            alert("Logout failed, please try again.");
        }
    } catch (error) {
        console.error("Error during logout:", error);
        alert("An error occurred. Please try again later.");
    }
}
