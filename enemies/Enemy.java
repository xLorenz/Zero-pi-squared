package enemies;

import physics.objects.PhysicsBall;
import physics.process.BatchRenderer;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import physics.structures.Contact;
import player.Player;
import java.awt.Color;
import java.util.ArrayList;

import particles.types.PhysicsParticle;
import particles.types.SimpleParticle;

public abstract class Enemy extends PhysicsBall {

    public static PhysicsHandler handler;
    public static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public static Player player;

    public Vector2 target = new Vector2();

    public int health;
    public int damage;

    public double jumpCooldown;
    public double jumpTimer = 0;

    public int maxJumpStrength;
    public int jumpAngleDegrees;
    public double jumpAngleRadians = 0.0;

    public Enemy(Vector2 pos, Color color, int radius) {
        super(radius, 0.8, 5.0, 0L);
        this.pos = pos;
        this.displayColor = color;
        this.forceAwake = true;
        handler.addObject(this);
        enemies.add(this);
    }

    public void setAngle(int angleDegrees) {
        jumpAngleDegrees = angleDegrees;
        jumpAngleRadians = Math.toRadians(angleDegrees);

    }

    @Override
    public void draw(BatchRenderer renderer) {
        double diameter = radius * 2.0;

        // Tune these values
        double stretchFactor = 0.04; // how sensitive stretch is to velocity
        double maxStretch = radius; // max extra height (in world units)

        // Compute vertical stretch
        double stretch = vel.y * stretchFactor;
        stretch = Math.max(-maxStretch, Math.min(maxStretch, stretch));

        double width = diameter;
        double height = diameter + Math.abs(stretch);

        // Anchor logic:
        // - Falling (vel.y > 0): stretch downward
        // - Rising (vel.y < 0): stretch upward
        double yOffset = (vel.y > 0) ? -stretch : 0;

        int x = (int) ((pos.x));
        int y = (int) ((pos.y - yOffset));

        renderer.setFill(displayColor, 255);
        renderer.drawOval(new Vector2(x, y), width, height);
    }

    @Override
    public void update(double dt) {

        updateTimers(dt);

        if (jumpTimer == 0 && supported) {

            jumpTowardsTarget(pathToPlayer());
            jumpTimer = jumpCooldown;
        }

        // hit player
        for (Contact c : contacts) {
            if (c.other == player) {
                player.damage(damage);
            }
        }
    }

    public void updateTimers(double dt) {
        if (jumpTimer > 0) {
            if (supported)
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
            jumpVelocity = Math.sqrt(Math.abs((handler.gravity.y * dx * dx) / (2 * cos * cos * difference)));
        }
        // cap velocity
        jumpVelocity = jumpVelocity > maxJumpStrength ? maxJumpStrength : jumpVelocity;

        vel.set(cos, sin);
        vel.scaleLocal(jumpVelocity);
        SimpleParticle.emitCircle(pos, 1, radius / 2, 2, displayColor, 1, 0.5, 2);
        supported = false;
    }

    @Override
    public void damage(double ammount) {

        health -= ammount;
        if (health < 0) {
            health = 0;
            kill();
        }
    }

    @Override
    public void damage(double ammount, Vector2 damageOrigin) {
        health -= ammount;
        if (health < 0) {
            health = 0;
            kill(damageOrigin);
        }

    }

    public void kill() {
        // rewards and remove()
        remove();
        PhysicsParticle.emitCircle(pos, 1, radius, 10, displayColor, radius / 15, 2, radius / 2);
    }

    public void kill(Vector2 damageOrigin) {
        remove();
        PhysicsParticle.emitCircleAway(damageOrigin, pos, 1, radius, 10, displayColor, radius / 15, radius / 10, 2,
                radius / 2);
    }

    public void remove() {
        // remove from Enemy list and handler objects
        handler.removeObject(this);
        enemies.remove(this);
    }

}
