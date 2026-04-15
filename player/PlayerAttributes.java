package player;

import java.awt.Color;
import java.util.List;

import player.skills.Skill;

public class PlayerAttributes {

    public static final int hitBoxRadius = 25;
    public static final double baseElasticity = 0.05;
    public static final double baseMass = 10.0;
    public final double baseFriction = 0.2;

    public final Color baseColor = Color.CYAN;

    public final int baseMaxHealth = 100;
    public double maxHealthMultiplier = 1.0;
    public final double baseInulnerabilityTime = 1.0;
    public double invulnerabilityTimeMultiplier = 1.0;

    public final int baseSpeed = 1000;
    public final int baseMaxSpeed = 250;
    public double speedMultiplier = 1.0;
    public double maxSpeedMultiplier = 1.0;

    public int baseJumpStrength = 600;
    public double jumpStrengthMultiplier = 1.0;

    public double airBorneTimer;

    public List<Skill> baseSkills = List.of();

    public HealthManager createBaseHealthManager() {
        return new HealthManager(baseMaxHealth, baseInulnerabilityTime, invulnerabilityTimeMultiplier);
    }

    public SkillsManager createBaseSkillsManager(Player p) {
        return createSkillsManager(p, baseSkills);
    }

    public SkillsManager createSkillsManager(Player p, List<Skill> skills) {
        SkillsManager m = new SkillsManager(p);
        m.addSkills(skills);
        return m;
    }
}
