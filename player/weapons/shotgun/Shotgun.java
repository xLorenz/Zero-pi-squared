package player.weapons.shotgun;

import java.util.Random;

import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Controller.Key;
import player.Player;
import player.weapons.Gun;
import world.effects.ScreenShake;

public class Shotgun extends Gun {

    static Random rand = new Random();
    static int spreadMaxAngleDegrees = 10;

    public Shotgun(Key triggerKey, Player owner, PhysicsHandler handler) {
        super(triggerKey, owner, handler);
        this.coolDown = 0.6;
        this.bulletDamage = 10;
    }

    public void shoot() {
        cooldownTime = coolDown;
        for (int i = 0; i < 5; i++)
            handler.addObject(new ShotgunBullet(
                    new Vector2(owner.pos.x, owner.pos.y),
                    new Vector2(
                            owner.direction.rotate(spreadMaxAngleDegrees - (rand.nextInt(spreadMaxAngleDegrees) * 2))),
                    bulletDamage,
                    owner));
        ScreenShake.create(0.5, bulletDamage * 5);
    }

}
