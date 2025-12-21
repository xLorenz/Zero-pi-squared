package enemies;

import java.awt.Color;

import physics.Vector2;

public class Speedster extends Enemy {
    public Speedster(Vector2 pos) {
        super(pos, Color.green, 15);
        this.health = 10;
        this.damage = 1;

        setAngle(30);
        this.maxJumpStrength = 1000;
        this.jumpCooldown = 0.1;
    }
}
