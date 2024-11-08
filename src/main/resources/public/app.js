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
