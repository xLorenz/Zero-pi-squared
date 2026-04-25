package player.weapons;

import physics.objects.AreaCircle;
import physics.objects.PhysicsBall;
import physics.objects.PhysicsObject;
import physics.objects.PhysicsRect;
import physics.process.PhysicsHandler;
import physics.structures.Manifold;
import physics.structures.Vector2;

public class MeleeHit extends AreaCircle {

    static PhysicsHandler handler;

    public PhysicsObject shooter;

    public double damage;

    public MeleeHit(Vector2 pos, double damage, PhysicsObject shooter) {
        super(pos, 50);
        this.shooter = shooter;
        this.damage = damage;
    }

    public static void setHandler(PhysicsHandler handler) {
        MeleeHit.handler = handler;
    }

    @Override
    public void onColisionWithCircle(PhysicsBall b, Manifold m) {
        if (b != shooter)
            b.damage(damage, pos);
    }

    @Override
    public void onColisionWithRect(PhysicsRect r, Manifold m) {
        if (r != shooter)
            r.damage(damage, pos);
    }

    @Override
    public void update(double dt) {
        // instantly deletes itself
        handler.removeObject(this);
    }

}
