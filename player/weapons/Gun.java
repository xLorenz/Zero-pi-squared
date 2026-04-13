package player.weapons;

import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Player;
import player.Controller.Key;
import world.effects.ScreenShake;

public class Gun {

    public PhysicsHandler handler;

    public double coolDown = 0.1;
    public double cooldownTime = 0;
    public double bulletDamage = 10;
    public Key triggerKey;
    public Player owner;

    public Gun(Key triggerKey, Player owner, PhysicsHandler handler) {
        this.triggerKey = triggerKey;
        this.owner = owner;
        this.handler = handler;
        Bullet.setHandler(handler);
    }

    public void shoot() {
        cooldownTime = coolDown;
        handler.addObject(new Bullet(new Vector2(owner.pos.x, owner.pos.y),
                new Vector2(owner.direction.x, owner.direction.y), bulletDamage, owner));
        ScreenShake.create(0.5, bulletDamage * 5);
    }

    public void updateTimer(double dt) {
        cooldownTime -= dt;
        if (cooldownTime <= 0) {
            cooldownTime = 0;
        }
    }

    public void handleInputs() {
        if (triggerKey.pressed && cooldownTime == 0) {
            shoot();
        }
    }
}