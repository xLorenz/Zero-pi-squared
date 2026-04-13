package player.weapons.shotgun;

import physics.objects.PhysicsObject;
import physics.structures.Vector2;
import player.weapons.Bullet;

public class ShotgunBullet extends Bullet {

    public ShotgunBullet(Vector2 pos, Vector2 direction, double damage, PhysicsObject shooter) {
        super(pos, direction, damage, shooter);
        this.maxDistanceMult = 0.2;
    }

}
