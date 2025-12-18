package enemies;

import physics.*;
import player.*;
import java.awt.Color;

public abstract class Enemy extends PhysicsBall {

    public static PhysicsHandler handler;

    public int health;
    public int damage;

    public Vector2 target = new Vector2();

    public Player player;

    public Enemy(Vector2 pos, Color color, int radius) {
        super(radius, 0.8, 5.0, 0L);
        this.pos = pos;
        this.displayColor = color;

        handler.addBall(this);

    }

    @Override
    public void update(double gravity, double dt) {
        vel.y += gravity * dt;
        pos.addLocal(vel.scale(dt));

    }

    public void pathToPlayer() {

    }

    public void jumpTowardsTarget() {

    }

    public void damage(int damage) {

    }

    public void kill() {
        // rewards and remove()
    }

    public void remove() {
        // remove from Enemy list and handler objects
    }
}
