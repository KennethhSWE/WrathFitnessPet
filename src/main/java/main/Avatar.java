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

    public void completeWorkout(int weight, int reps, boolean hitDailyGoal, boolean hitStreakBonus) {
        int workoutXp = (weight * reps) / 10; 
        if (hitDailyGoal) {
            workoutXp += 10;
        }
        if (hitStreakBonus) {
            workoutXp += 20;
        }
        xp += workoutXp;

        System.out.println(name + " gained " + workoutXp + " XP! Total XP: " + xp);
        checkLevelUp();
    }

    // Check if the XP earned is enough for leveling up
    private void checkLevelUp() {
        while (xp >= xpToNextLevel) {
            levelUp();
        }
    }

    // Level up and adjust XP requirements and stats
    private void levelUp() {
        level++;
        xp -= xpToNextLevel; // Retain remaining XP after leveling up
        xpToNextLevel = 100 * level; // Scale XP requirement with each level
        strength += 5;
        stamina += 5;
        cardioHealth += 5;

        System.out.println(name + " leveled up to level " + level + "! New stats - Strength: " + strength + ", Stamina: " + stamina + ", Cardio Health: " + cardioHealth);
    }

    public void setAppearance(String outfitChoice) {
        System.out.println(name + " is now wearing " + outfitChoice);
    }

    // Getters for avatar stats
    public int getStrength() { return strength; }
    public int getStamina() { return stamina; }
    public int getCardioHealth() { return cardioHealth; }
    public String getName() { return name; }
    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public int getXpToNextLevel() { return xpToNextLevel; }
}
