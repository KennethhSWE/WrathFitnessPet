// Check if user is already logged in on page load
window.onload = async function() {
    const response = await fetch('/check-session', { method: 'GET' });
    if (response.ok) {
        const username = await response.text();
        document.getElementById('username-display').innerText = username;
        document.getElementById('registration-container').style.display = 'none';
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('dashboard').style.display = 'block';
    }
};

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

function showLogin() {
    document.getElementById('registration-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'block';
}

async function login() {
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    const response = await fetch('/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ username, password })
    });

    if (response.ok) {
        document.getElementById('username-display').innerText = username;
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('dashboard').style.display = 'block';
    } else {
        const message = await response.text();
        document.getElementById('login-message').innerText = message;
    }
}

async function logout() {
    const response = await fetch('/logout', { method: 'POST' });
    if (response.ok) {
        alert("You have been logged out.");
        document.getElementById('dashboard').style.display = 'none';
        document.getElementById('login-section').style.display = 'block';
    } else {
        alert("Logout failed, please try again.");
    }
}
