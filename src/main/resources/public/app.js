// Sign-Up Function for New Members
async function signUp() {
    const name = document.getElementById('name').value;
    const age = document.getElementById('age').value;
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    const fitnessGoal = document.getElementById('fitness-goal').value;

    // Basic validation
    if (password !== confirmPassword) {
        alert("Passwords do not match. Please re-enter your password.");
        return;
    }

    const response = await fetch('/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, age, username, email, password, fitnessGoal })
    });

    if (response.ok) {
        alert("Account created successfully! Please log in.");
        showLogin(); // Redirect to login screen
    } else {
        alert('Sign-up failed, please try again.');
    }
}

// Function to show the login screen (to be created after account setup)
function showLogin() {
    document.getElementById('registration-container').style.display = 'none';
    document.getElementById('login-section').style.display = 'block';
}
