package player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import physics.*;

public class Player extends PhysicsBall {

    public Color color;
    public int health;
    public int baseHealth = 100;

    public int baseSpeed = 55;
    public double baseSprintModifier = 2;
    public int baseMaxSpeed = 250;

    public int baseJumpHeight = 550;

    public Vector2 direction = new Vector2(1, 0);

    public Controller controller = new Controller();

    public boolean airBorne = false;
    public double airBorneTimer = 0.0;

    public boolean invulnerable = false;
    public double invulnerableTimer = 0.0;

    public boolean sprinting = false;

    public Player(Vector2 pos, Color color, PhysicsHandler handler) {
        super(25, 0.0, 5.0, 0L);
        this.pos = pos;
        this.color = color;
        this.setListener(new LandingListener());
        this.health = baseHealth;

        handler.addBall(this);

    }

    @Override
    public void draw(Graphics g) {
        Polygon shape = new Polygon();

        Vector2 v1 = pos.add(direction.rotate(120 * 0).scale(radius * 1.5));
        Vector2 v2 = pos.add(direction.rotate(120 * 1).scale(radius * 1.5).sub(vel.scale(radius * 1.5 / 1000)));
        Vector2 v3 = pos.add(direction.rotate(120 * 2).scale(radius * 1.5).sub(vel.scale(radius * 1.5 / 1000)));

        shape.addPoint((int) (v1.x), (int) (v1.y));
        shape.addPoint((int) (v2.x), (int) (v2.y));
        shape.addPoint((int) (v3.x), (int) (v3.y));

        Color old = g.getColor();
        g.setColor(color);
        g.fillPolygon(shape);
        g.setColor(old);
    }

    @Override
    public void update(double gravity, double dt) {
        vel.y += gravity * dt;
        pos.addLocal(vel.scale(dt));
        // friction
        if (airBorne) {
            vel.x *= 0.9;
        } else {
            vel.x *= 0.85;
        }
        updateTimers(dt);

        direction.set(controller.mouse.pos.sub(pos));
        direction.normalizeLocal();
    }

    public void handleInputs() {
        // jump
        if (controller.keys.space.pressed && !airBorne) { // must be on the ground or in coyote timer
            vel.set(new Vector2(vel.x, -baseJumpHeight));
            airBorne = true;
        }

        // walk left
        if (controller.keys.a.pressed) {
            if (vel.x > (sprinting ? -baseMaxSpeed * baseSprintModifier : -baseMaxSpeed)) {
                vel.x -= sprinting ? baseSpeed * baseSprintModifier : baseSpeed;
            }
        }
        // walk right
        if (controller.keys.d.pressed) {
            if (vel.x < (sprinting ? baseMaxSpeed * baseSprintModifier : baseMaxSpeed)) {
                vel.x += sprinting ? baseSpeed * baseSprintModifier : baseSpeed;
            }
        }
        // sprint
        sprinting = controller.keys.control.pressed;
    }

    public void updateTimers(double dt) {
        if (airBorne) {
            airBorneTimer += dt;
        }
        // falling and coyote time
        if (vel.y > 0.0) {
            airBorneTimer += dt;
            if (airBorneTimer > 0.3) {
                airBorne = true;
            }
        }
        // damage invulnerability window
        if (invulnerable && invulnerableTimer < 0) {
            invulnerableTimer -= dt;
        } else {
            invulnerable = false;
            invulnerableTimer = 0.0;
        }
    }

    public void damage(int damage) {
        if (!invulnerable) {
            health -= damage;
            if (health < 0)
                health = 0;
            invulnerable = true;
            invulnerableTimer = 1.0; // 1s
            // todo: add vfx and sfx
        }
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
                    airBorneTimer = 0; // reset airborne timer
                }
            }
        }
    }

}
