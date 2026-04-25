package player;

import java.util.List;
import java.util.Random;

import particles.types.*;
import physics.objects.PhysicsBall;
import physics.process.BatchRenderer;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.skills.*;
import player.weapons.Gun;
import player.weapons.Melee;
import sound.AudioLoader;
import sound.AudioManager;

public class Player extends PhysicsBall {

    public PhysicsHandler handler;
    private Random rand = new Random();

    public Controller controller = new Controller();
    public PlayerAttributes attributes = new PlayerAttributes();
    public HealthManager healthManager;
    public SkillsManager skillsManager;
    public Gun gun;

    public Vector2 direction = new Vector2(1, 0);

    public Player(Vector2 pos, PhysicsHandler handler) {
        super(PlayerAttributes.hitBoxRadius, PlayerAttributes.baseElasticity, PlayerAttributes.baseMass, 0L);

        this.forceAwake = true;
        this.friction = attributes.baseFriction;
        this.pos = pos;
        setDisplayColor(attributes.baseColor);

        healthManager = attributes.createBaseHealthManager();

        skillsManager = attributes.createSkillsManager(this, List.of(
                new Sprint(controller.keys.control),
                new DoubleJump(controller.keys.space),
                new PlaceBlock(controller.mouse.right, handler),
                new Grenade(controller.keys.x, handler),
                new BlockSield(controller.keys.c, handler)));

        this.gun = new Melee(controller.mouse.left, this, handler);

        this.handler = handler;

        handler.addObject(this);
    }

    @Override
    public void draw(BatchRenderer renderer) {
        Vector2[] points = {
                pos.add(direction.rotate(120 * 0).scale(radius * 1.5)),
                pos.add(direction.rotate(120 * 1).scale(radius * 1.5).sub(vel.scale(radius * 1.5 / 1000))),
                pos.add(direction.rotate(120 * 2).scale(radius * 1.5).sub(vel.scale(radius * 1.5 / 1000))),
        };

        if (healthManager.vulnerable) {
            renderer.setFill(displayColor, 255);
        } else {
            renderer.setFill(displayColorDarker, 255);
        }
        renderer.drawPolygon(points, 3);
    }

    @Override
    public void update(double dt) {
        AudioManager.setListenerPosition(pos);

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

        if (supported || attributes.airBorneTimer <= 0.2) {
            if (controller.keys.space.singlePress()) {
                // must be on the ground or in coyote timer
                vel.set(new Vector2(vel.x, -attributes.baseJumpStrength));

                for (int i = 0; i < 20; i++) {
                    SimpleParticle.emit(new Vector2(pos.x, pos.y + radius));
                }
                supported = false;
                AudioManager.playSfx(AudioLoader.Clips.JUMP);
                // airBorneTimer = 0.3;
            }
        }

        // walk left
        if (controller.keys.a.pressed) {
            if (vel.x > -attributes.baseMaxSpeed * attributes.speedMultiplier) {
                vel.x -= attributes.baseSpeed * attributes.speedMultiplier * dt;
            }
        }
        // walk right
        if (controller.keys.d.pressed) {
            if (vel.x < attributes.baseMaxSpeed * attributes.speedMultiplier) {
                vel.x += attributes.baseSpeed * attributes.speedMultiplier * dt;
            }
        }

        // skills
        skillsManager.handleInputs(controller);
        gun.handleInputs();
        controller.update(dt);
    }

    public void updateTimers(double dt) {
        if (!supported) {
            attributes.airBorneTimer += dt;
        } else {
            attributes.airBorneTimer = 0.0;
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
