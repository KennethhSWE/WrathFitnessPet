package main;

public class Pet {
    private String name;
    private int level;
    private int xp;
    private int hunger;
    private int thirst;
    private int xpToNextLevel;

    public Pet(String name) {
        this.name = name;
        this.level = 1;
        this.xp = 0;
        this.hunger = 100;
        this.thirst = 100;
        this.xpToNextLevel = 100; // Starting XP required for level 1 to 2
    }

    public void feed() {
        hunger = Math.min(hunger + 10, 100);
        System.out.println(name + " has been fed! Hunger: " + hunger);
    }

    public void drink() {
        thirst = Math.min(thirst + 10, 100);
        System.out.println(name + " has had some water! Thirst: " + thirst);
    }

    // Add XP based on steps, calories, and bonuses
    public void addXp(int steps, int calories, boolean hitDailyGoal, boolean hitStreakBonus) {
        // Base calculation for XP
        int stepXp = steps / 100; // 1 xp per 100 steps taken
        int calorieXp = calories / 100; // 1 xp per 100 calories burned

        // Bonus XP for daily goals being hit
        int bonusXp = 0;
        if (hitDailyGoal) {
            bonusXp += 100; // Adds an extra 100 XP for hitting the daily goals
        }

        // Bonus added for hitting a multi-day streak
        if (hitStreakBonus) {
            bonusXp += 50; // Add 50 XP for multiple day streak
        }

        // Total XP gained
        int totalXp = stepXp + calorieXp + bonusXp;
        this.xp += totalXp;

        System.out.println(name + " gained " + totalXp + " XP!");

        checkLevelUp(); // Check if the pet can level up after gaining XP
    }

    // Check if the pet can level up
    private void checkLevelUp() {
        if (this.xp >= this.xpToNextLevel) {
            levelUp();
        }
    }

    // Level up the pet and adjust all XP requirements
    private void levelUp() {
        this.level++;
        System.out.println(name + " has leveled up to: " + this.level + "!");

        // Handle the extra XP carried over
        this.xp -= this.xpToNextLevel;

        // Increase the requirement to level to the next level
        this.xpToNextLevel = 100 * (this.level * this.level);
    }
}
