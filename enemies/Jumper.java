package enemies;

import java.awt.Color;
import physics.*;

public class Jumper extends Enemy {

    public Jumper(Vector2 pos) {
        super(pos, Color.blue, 25);
        this.health = 10;
        this.damage = 1;

        setAngle(70);
        this.maxJumpStrength = 1500;
        this.jumpCooldown = 0.75;
    }

}