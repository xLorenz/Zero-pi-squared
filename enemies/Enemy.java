package enemies;

import physics.*;
import player.*;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public abstract class Enemy extends PhysicsBall {

    public static PhysicsHandler handler;
    public static List<Enemy> enemies = new ArrayList<Enemy>();
    public static Player player;

    public Vector2 target = new Vector2();

    public int health;
    public int damage;

    public boolean airBorne = false;
    public double jumpCooldown;
    public double jumpTimer = 0;

    public int maxJumpStrength;
    public int jumpAngleDegrees;
    public double jumpAngleRadians = 0.0;

    public Enemy(Vector2 pos, Color color, int radius) {
        super(radius, 0.8, 5.0, 0L);
        this.pos = pos;
        this.displayColor = color;

        this.setListener(new LandingListener());

        handler.addBall(this);
        enemies.add(this);
    }

    public void setAngle(int angleDegrees) {
        jumpAngleDegrees = angleDegrees;
        jumpAngleRadians = Math.toRadians(angleDegrees);

    }

    @Override
    public void draw(Graphics g) {
        g.setColor(displayColor);
        g.fillOval((int) (pos.x - radius), (int) (pos.y - radius), radius * 2,
                (int) ((radius * 2) - Math.min(vel.y * 0.02, radius)));
    }

    @Override
    public void update(double gravity, double dt) {
        vel.y += gravity * dt;
        pos.addLocal(vel.scale(dt));

        updateTimers(dt);

        if (jumpTimer == 0 && !airBorne) {

            jumpTowardsTarget(pathToPlayer());
            jumpTimer = jumpCooldown;
        }
        airBorne = true;
    }

    public void updateTimers(double dt) {
        if (jumpTimer > 0) {
            if (!airBorne)
                jumpTimer -= dt;
        } else {
            jumpTimer = 0;
        }
    }

    public double pathToPlayer() {
        target.set(player.pos);
        if (target.x < pos.x) {
            return Math.PI + jumpAngleRadians;
        } else {
            return -jumpAngleRadians;
        }
    }

    public void jumpTowardsTarget(double angleTowardsTargetRadians) {
        double dx = target.x - pos.x;
        double dy = target.y - pos.y;

        double cos = Math.cos(angleTowardsTargetRadians);
        double sin = Math.sin(angleTowardsTargetRadians);

        double difference = (dx * Math.tan(angleTowardsTargetRadians) - dy);
        double jumpVelocity = maxJumpStrength;

        if (difference != 0 && cos != 0) {
            // parabolic throw formula
            jumpVelocity = Math.sqrt(Math.abs((handler.gravity * dx * dx) / (2 * cos * cos * difference)));
        }
        // cap velocity
        jumpVelocity = jumpVelocity > maxJumpStrength ? maxJumpStrength : jumpVelocity;

        vel.set(cos, sin);
        vel.scaleLocal(jumpVelocity);
        airBorne = true;
    }

    public void damage(int damage) {

    }

    public void kill() {
        // rewards and remove()
    }

    public void remove() {
        // remove from Enemy list and handler objects
    }

    public class LandingListener extends CollisionListener {
        public LandingListener() {
            super();
        }

        // get collision below the player hitbox
        @Override
        public void action(PhysicsObject o, Manifold m) {
            // collision with rect, which top is below the triangle
            for (Vector2 c : m.contacts) {
                if (c.y > pos.y) {
                    airBorne = false; // reset airborne
                    vel.x *= 0.85; // friction
                }
            }
        }
    }
}
