package player;

import java.awt.Color;
import java.util.Random;

import particles.types.*;
import physics.objects.AreaCircle;
import physics.objects.AreaRect;
import physics.objects.PhysicsBall;
import physics.process.BatchRenderer;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import physics.structures.Manifold;
import physics.objects.PhysicsRect;
import physics.collisions.Collision;
import player.skills.*;

public class Player extends PhysicsBall {

    public PhysicsHandler handler;
    private Random rand = new Random();

    public Color color;

    public HealthManager healthManager;
    public SkillsManager skillsManager;

    public int baseSpeed = 1000;
    public double speedMultiplier = 1.0;
    public int baseMaxSpeed = 250;

    public int baseJumpStrength = 600;

    public Vector2 direction = new Vector2(1, 0);

    public Controller controller = new Controller();

    public double airBorneTimer = 0.0;

    private AreaRect area;
    private AreaCircle area2;

    public Player(Vector2 pos, Color color, PhysicsHandler handler) {
        super(25, 0.05, 10.0, 0L);
        this.pos = pos;
        this.color = color;

        this.healthManager = new HealthManager(100, 1.0);
        this.skillsManager = new SkillsManager(this);
        skillsManager.addSkill(new Sprint());
        skillsManager.addSkill(new DoubleJump());
        skillsManager.addSkill(new PlaceBlock(handler));
        skillsManager.addSkill(new Grenade(handler));

        this.forceAwake = true;
        this.friction = 0.0;

        handler.addObject(this);
        this.handler = handler;

        area = new AreaRect(new Vector2(800, 300), 100, 20);
        handler.addObject(area);
        area2 = new AreaCircle(new Vector2(0, -150), 100);
        handler.addObject(area2);

    }

    @Override
    public void draw(BatchRenderer renderer) {
        Vector2[] points = {
                pos.add(direction.rotate(120 * 0).scale(radius * 1.5)),
                pos.add(direction.rotate(120 * 1).scale(radius * 1.5).sub(vel.scale(radius * 1.5 / 1000))),
                pos.add(direction.rotate(120 * 2).scale(radius * 1.5).sub(vel.scale(radius * 1.5 / 1000))),
        };

        if (healthManager.vulnerable) {
            renderer.setFill(color, 255);
        } else {
            renderer.setFill(color.darker(), 255);
        }
        renderer.drawPolygon(points, 3);
    }

    @Override
    public void update(double dt) {

        updateTimers(dt);

        if (supported) {
            vel.x *= 0.99;
        } else {
            // TriangleParticle.emit(pos);
        }

        direction.set((handler.display.getMapPos(controller.mouse.pos).sub(pos)));
        direction.normalizeLocal();

        skillsManager.updateSkills(dt);

        // bg particles
        if (rand.nextInt(200) == 1)
            SimpleBackgroundParticle.emit(pos.add(Vector2.random(-1000, 1000, -1000, 1000)));

        if (area.getCollisions().contains(this)) {
            // SimpleParticle.emitCircle(area.pos, 10, 11, 10, color, 2, 1.0, 20);
            ExplosionParticle.emit(area.pos, 2, Color.gray);
            ExplosionParticle.emit(area.pos, 1, Color.orange);
            SimpleParticle.emitCircle(area.pos, 10, 30, 40, Color.gray, 2, 1.0, 5);
            SimpleParticle.emitCircle(area.pos, 10, 30, 20, Color.gray, 3, 1.5, 10);
            SimpleParticle.emitCircle(area.pos, 10, 30, 5, Color.gray, 4, 1.5, 10);
            SimpleParticle.emitCircle(area.pos, 10, 30, 2, Color.orange, 7, 0.5, 5);

            vel.y = -baseJumpStrength * 2;
        }

        if (area2.getCollisions().contains(this)) {
            SimpleParticle.emitCircle(area2.pos, 99, 100, 1, color, 1, 1.5, 5);
        }
    }

    public void handleInputs(double dt) {
        // jump

        if (supported || airBorneTimer <= 0.2) {
            if (controller.keys.space.singlePress()) {
                // must be on the ground or in coyote timer
                vel.set(new Vector2(vel.x, -baseJumpStrength));

                for (int i = 0; i < 20; i++) {
                    SimpleParticle.emit(new Vector2(pos.x, pos.y + radius));
                }
                // airBorneTimer = 0.3;
            }
        }

        // walk left
        if (controller.keys.a.pressed) {
            if (vel.x > -baseMaxSpeed * speedMultiplier) {
                vel.x -= baseSpeed * speedMultiplier * dt;
            }
        }
        // walk right
        if (controller.keys.d.pressed) {
            if (vel.x < baseMaxSpeed * speedMultiplier) {
                vel.x += baseSpeed * speedMultiplier * dt;
            }
        }

        // skills
        skillsManager.handleInputs(controller);
        controller.update(dt);
    }

    public void updateTimers(double dt) {
        if (!supported) {
            airBorneTimer += dt;
        } else {
            airBorneTimer = 0.0;
        }
        healthManager.updateTimers(dt);
        skillsManager.updateTimers(dt);
    }

    public void damage(int ammount) {
        if (healthManager.damage(ammount)) {
            for (int i = 0; i < 10 * ammount; i++) {
                TriangleParticle.emit(pos);
            }
        }
    }

    @Override
    public Manifold collideWithRect(PhysicsRect rect) {
        // landing particles
        Manifold m = Collision.circleRect(this, rect); // get collision with rect (terrain)
        if (m != null) { // make sure it collided
            Boolean c = false;
            for (Vector2 con : m.contacts) {
                if (con.y > pos.y) // make sure it collided under player
                    c = true;
            }
            if (vel.y > 0 && c) { // if the player has been falling
                for (int i = 0; i < vel.y / 20; i++) {
                    SimpleParticle.emit(new Vector2(pos.x, pos.y + radius)); // emit particles
                }
            }
        }
        return m; // return Manifold
    }

}
