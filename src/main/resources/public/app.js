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

    // Send data as form-urlencoded instead of JSON
    const response = await fetch('/signup', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({
            name: name,
            age: age,
            username: username,
            email: email,
            password: password,
            fitnessGoal: fitnessGoal
        }).toString()
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
