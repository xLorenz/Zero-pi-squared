package enemies;

import java.awt.Color;
import physics.*;

public class Normie extends Enemy {

    public Normie(Vector2 pos) {
        super(pos, Color.red, 20);
        this.health = 10;
        this.damage = 1;

        setAngle(45);
        this.maxJumpStrength = 500;
        this.jumpCooldown = 0.5;
    }

}
