package world.effects;

import java.awt.Color;
import java.util.ArrayList;

import particles.types.ExplosionParticle;
import particles.types.SimpleParticle;
import physics.objects.AreaCircle;
import physics.objects.PhysicsObject;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;

public class Explosion extends AreaCircle {
    public static PhysicsHandler handler;

    double damage;
    double knockBack;
    boolean exploded = false;

    public static void emit(Vector2 pos, int radius, double damage, double knockBack) {
        Explosion boom = new Explosion(pos, radius, damage, knockBack);
        if (handler != null) {
            handler.addObject(boom);
        }
    }

    private Explosion(Vector2 pos, int radius, double damage, double knockBack) {
        super(pos, radius);
        this.pos = pos;
        this.radius = radius;
        this.damage = damage;
        this.knockBack = knockBack;
    }

    private void emitParticles() {
        SimpleParticle.emitCircle(pos, 10, 30, 20, Color.gray, 3, 1.5, 10);
        SimpleParticle.emitCircle(pos, 10, 30, 5, Color.gray, 4, 1.5, 10);
        SimpleParticle.emitCircle(pos, 10, 30, 2, Color.orange, 7, 0.5, 5);
        ExplosionParticle.emit(pos, 2, Color.gray);
        ExplosionParticle.emit(pos, 1, Color.orange);
        SimpleParticle.emitCircle(pos, 10, 30, 40, Color.gray, 2, 1.0, 5);
    }

    private void explode() {
        ArrayList<PhysicsObject> list = getCollisions();

        for (PhysicsObject o : list) {
            if (!o.stationary) {
                Vector2 distance = o.pos.sub(pos);
                o.vel.addLocal(distance.scale(knockBack * 100 / distance.length()));
            }
            o.damage(damage, pos);

        }
        ScreenShake.create(2.0, damage * 5);
    }

    @Override
    public void update(double dt) {
        if (!exploded) {
            explode();
            emitParticles();
            exploded = true;
        }
        handler.removeObject(this);
    }

}
