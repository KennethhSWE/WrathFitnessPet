window.onload = async function() {
    const response = await fetch('/check-session', { method: 'GET' });
    if (response.ok) {
        const username = await response.text();
        document.getElementById('username-display').innerText = username;
        document.getElementById('registration-container').style.display = 'none';
        document.getElementById('login-section').style.display = 'none';
        document.getElementById('dashboard').style.display = 'block';
        fetchWorkoutHistory(); // Load workout history on dashboard load
    }
};

async function logWorkout() {
    const weight = document.getElementById('weight').value;
    const reps = document.getElementById('reps').value;
    const dailyGoal = document.getElementById('dailyGoal').checked;
    const streakBonus = document.getElementById('streakBonus').checked;

    const formData = new URLSearchParams();
    formData.append('weight', weight);
    formData.append('reps', reps);
    formData.append('dailyGoal', dailyGoal);
    formData.append('streakBonus', streakBonus);

    try {
        const response = await fetch('/logWorkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData.toString()
        });

        if (response.ok) {
            document.getElementById('workout-message').innerText = "Workout logged successfully!";
            fetchWorkoutHistory(); // Refresh workout history
        } else {
            document.getElementById('workout-message').innerText = "Failed to log workout. Try again.";
        }
    } catch (error) {
        console.error("Error logging workout:", error);
        document.getElementById('workout-message').innerText = "An error occurred. Try again.";
    }
}

async function fetchWorkoutHistory() {
    try {
        const response = await fetch('/getWorkoutHistory', { method: 'GET' });
        if (response.ok) {
            const workouts = await response.json();
            const workoutHistory = document.getElementById('workout-history');
            workoutHistory.innerHTML = ''; // Clear previous history

            workouts.forEach(workout => {
                const workoutEntry = document.createElement('p');
                workoutEntry.innerText = `Date: ${new Date(workout.timestamp).toLocaleString()}, 
                                          Weight: ${workout.weight} lbs, Reps: ${workout.reps}, 
                                          Daily Goal: ${workout.dailyGoal ? "Yes" : "No"}, Streak Bonus: ${workout.streakBonus ? "Yes" : "No"}`;
                workoutHistory.appendChild(workoutEntry);
            });
        } else {
            document.getElementById('workout-message').innerText = "Error retrieving workout history.";
        }
    } catch (error) {
        console.error("Error fetching workout history:", error);
        document.getElementById('workout-message').innerText = "An error occurred. Try again.";
    }
}
