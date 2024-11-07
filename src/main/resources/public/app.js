// Sign-Up Function for New Members
async function signUp() {
    const email = document.getElementById('email').value;
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const name = document.getElementById('name').value;
    const age = document.getElementById('age').value;
    const fitnessGoal = document.getElementById('fitness-goal').value;

    const response = await fetch('/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, username, password, name, age, fitnessGoal })
    });

    if (response.ok) {
        alert("Account created successfully! Please log in.");
        showLogin(); // Navigate to the login screen
    } else {
        alert('Sign-up failed, please try again.');
    }
}

// Login Function for Returning Members
async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    const response = await fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    });

    const message = await response.text();
    document.getElementById('login-message').innerText = message;

    if (response.ok) {
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('avatar-preview').style.display = 'block';
    } else {
        alert('Login failed, please check your credentials.');
    }
}

// Function to show the login screen after account creation
function showLogin() {
    document.getElementById('onboarding-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'block';
}
