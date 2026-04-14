package player;

public class HealthManager {
    public int health;
    private int maxHealth;

    private double baseInvulnerabilityTime;
    private double invulnerabilityTimeMultiplier = 1.0;
    private double vulnerabilityTimer = 0;
    public boolean vulnerable = true;

    public HealthManager(int maxHealth, double baseInvulnerabilityTime) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.baseInvulnerabilityTime = baseInvulnerabilityTime;
    }

    public HealthManager(int maxHealth, double baseInvulnerabilityTime, double invulnerabilityTimeMultiplier) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.baseInvulnerabilityTime = baseInvulnerabilityTime;
        this.invulnerabilityTimeMultiplier = invulnerabilityTimeMultiplier;
    }

    public void setInvulnerabilityTimeMultiplier(double invulnerabilityTimeMultiplier) {
        this.invulnerabilityTimeMultiplier = invulnerabilityTimeMultiplier;
    }

    public void updateTimers(double dt) {
        if (vulnerabilityTimer > 0) {
            vulnerabilityTimer -= dt;
        } else {
            vulnerabilityTimer = 0;
            vulnerable = true;
        }
    }

    public boolean damage(int ammount) {
        if (vulnerable && health > 0) {

            setInvulnerability();

            health -= ammount;
            if (health < 0)
                health = 0;
            return true;
        }
        return false;
    }

    public void setInvulnerability() {
        vulnerable = false;
        vulnerabilityTimer = baseInvulnerabilityTime * invulnerabilityTimeMultiplier;
    }

    public void setInvulnerability(double time) {
        vulnerable = false;
        vulnerabilityTimer = time;
    }

    public double getPercent() {
        return health / maxHealth;
    }

    public void regenerateHealth() {
        health = maxHealth;
    }

    public void regenerateHealth(int ammount) {
        health += ammount;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }
}
