package player.weapons;

import particles.types.SimpleParticle;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Controller.Key;
import player.Player;
import sound.AudioLoader;
import sound.AudioManager;
import world.effects.ScreenShake;

public class Melee extends Gun {

    public PhysicsHandler handler;

    public double hitDamage = 20;

    public int hitLoopStage = 1;

    public double lastHitTime = 0.0;
    public double comboHoldTime = 0.5;

    public Melee(Key triggerKey, Player owner, PhysicsHandler handler) {
        super(triggerKey, owner, handler);
        MeleeHit.setHandler(handler);
        this.handler = handler;
        this.coolDown = 0.35;
    }

    public void hit() {
        cooldownTime = coolDown;

        if (lastHitTime > comboHoldTime) {
            hitLoopStage = 1;
        }

        handler.addObject(new MeleeHit(
                new Vector2(owner.pos.add(owner.direction.scale(40))),
                hitLoopStage == 3 ? hitDamage * 2 : hitDamage,
                owner));

        ScreenShake.create(0.5, hitDamage * 10);
        AudioManager.playSfx(AudioLoader.BundledIds.BULLET);

        if (hitLoopStage == 3)
            cooldownTime = coolDown * 1.5;
        hitVfx();
        hitLoopStage++;
    }

    private void hitVfx() {
        switch (hitLoopStage) {
            case 1:
                for (int i = 0; i < 5; i++)
                    SimpleParticle.emit(
                            owner.pos.add(owner.direction.scale(60).rotate(-45 + (i * 90 / 4))),
                            new Vector2(owner.direction.rotate(90).scale(100)),
                            i + 1,
                            0.5,
                            owner.displayColor);
                break;
            case 2:
                for (int i = 0; i < 5; i++)
                    SimpleParticle.emit(
                            owner.pos.add(owner.direction.scale(60).rotate(-45 + (i * 90 / 4))),
                            new Vector2(owner.direction.rotate(-90).scale(100)),
                            5 - i,
                            0.5,
                            owner.displayColor);

                break;
            case 3:
                for (int i = 0; i < 5; i++)
                    SimpleParticle.emit(
                            owner.pos.add(owner.direction.scale(60).rotate(-45 + (i * 90 / 4))),
                            new Vector2(owner.direction.scale(100)),
                            (i == 1 || i == 2 || i == 3) ? 4 : 2,
                            0.5,
                            owner.displayColor);

                hitLoopStage = 0;
                break;

            default:
                break;
        }
    }

    public void updateTimer(double dt) {
        cooldownTime -= dt;
        if (cooldownTime <= 0) {
            cooldownTime = 0;
        }

        if (lastHitTime <= comboHoldTime) {
            lastHitTime += dt;
        }
    }

    public void handleInputs() {
        if (triggerKey.pressed && cooldownTime == 0) {
            hit();
            lastHitTime = 0;
        }
    }

}
