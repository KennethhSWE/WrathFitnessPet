package main;

public class Avatar {
    private String name;
    private int level;
    private int xp;
    private int strength;
    private int stamina;
    private int cardioHealth; 
    private int xpToNextLevel;

    public Avatar(String name) {
        this.name = name;
        this.level = 1;
        this.xp = 0;
        this.strength = 50;
        this.stamina = 50;
        this.cardioHealth = 50;
        this.xpToNextLevel = 100; // Starting XP required for level 1 to 2
    }

    public void completeWorkout(int weight, int reps, boolean hitDailyGoal, boolean hitStreakBonus ) {
        int woroutXp = (weight * reps) / 10; 
        if (hitDailyGoal) {
            workoutXp += 10;
        }
        if (hitStreakBonus) {
            workoutXp += 20;
        }
        xp += workoutXp;

        if (xp >= xpToNextLevel) {
            levelUp();
        }
        System.out.println(name + " gained" + workoutXp + " XP! Total XP: " + xp);
    }

    public void setAppearance(String outFitChoice) {
        System.out.print(name + " is now wearing " + outFitChoice);
    }

    public int getStrength(){ return strength;}
    public int getStamina(){ return stamina;}
    public int getCardioHealth(){ return cardioHealth;}

    // Level up and adjust all XP requirements
    private void levelUp() {
        level++;

        // Handle the extra XP carried over
        xp -= this.xpToNextLevel;

        // Increase the requirement to level to the next level
        xpToNextLevel += 50;
        strength += 5;
        stamina += 5; 
        cardioHealth += 5;
        System.out.println(name + " leveled up to level " + level + "!"); 
    }
}
