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
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('workout-section').style.display = 'block';
        document.getElementById('avatar-section').style.display = 'block';
    }
}

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

async function viewAvatar() {
    const response = await fetch('/view-avatar');
    const stats = await response.text();
    document.getElementById('avatar-stats').innerText = stats;
}
