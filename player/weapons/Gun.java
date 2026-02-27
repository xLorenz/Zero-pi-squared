package player.weapons;

import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Player;
import player.Controller.Key;

public class Gun {

    public PhysicsHandler handler;

    public double coolDown;
    public double cooldownTime = 0;
    public double bulletDamage;
    public Key triggerKey;
    public Player owner;

    public Gun(double coolDown, double bulletDamage, Key triggerKey, Player owner, PhysicsHandler handler) {
        this.coolDown = coolDown;
        this.bulletDamage = bulletDamage;
        this.triggerKey = triggerKey;
        this.owner = owner;
        this.handler = handler;
        Bullet.handler = handler;
    }

    public void shoot() {
        cooldownTime = coolDown;
        handler.addObject(new Bullet(new Vector2(owner.pos.x, owner.pos.y),
                new Vector2(owner.direction.x, owner.direction.y), bulletDamage, owner));
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