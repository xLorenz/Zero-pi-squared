package player;

import java.awt.Color;
import java.util.Random;

import particles.types.*;
import physics.objects.PhysicsBall;
import physics.process.BatchRenderer;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.skills.*;
import player.weapons.Gun;

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

    public Gun gun;

    public Player(Vector2 pos, Color color, PhysicsHandler handler) {
        super(25, 0.05, 10.0, 0L);
        this.pos = pos;
        this.color = color;

        this.healthManager = new HealthManager(100, 1.0);
        this.skillsManager = new SkillsManager(this);
        skillsManager.addSkill(new Sprint(controller.keys.control));
        skillsManager.addSkill(new DoubleJump(controller.keys.space));
        skillsManager.addSkill(new PlaceBlock(controller.mouse.right, handler));
        skillsManager.addSkill(new Grenade(controller.keys.x, handler));
        skillsManager.addSkill(new BlockSield(controller.keys.c, handler));

        this.gun = new Gun(0.1, 10, controller.mouse.left, this, handler);

        this.forceAwake = true;
        this.friction = 0.0;

        handler.addObject(this);
        this.handler = handler;

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
        gun.handleInputs();
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
        gun.updateTimer(dt);
    }

    public void damage(int ammount) {
        if (healthManager.damage(ammount)) {
            for (int i = 0; i < 10 * ammount; i++) {
                TriangleParticle.emit(pos);
            }
        }
    }

}
