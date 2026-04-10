package player.weapons;

import particles.types.PhysicsParticle;
import particles.types.SimpleParticle;
import physics.objects.PhysicsBall;
import physics.objects.PhysicsObject;
import physics.process.PhysicsHandler;
import physics.structures.Contact;
import physics.structures.Vector2;

public class Bullet extends PhysicsBall {

    private static PhysicsHandler handler;
    public static double speed = 2000; // direction multiplier
    public static double maxDistanceSquared = 0;
    double damage;
    double maxDistanceMult;
    double distanceSquaredTraveled = 0;
    boolean collided = false;
    PhysicsObject shooter;

    public static void setHandler(PhysicsHandler handler) {
        Bullet.handler = handler;
        Bullet.maxDistanceSquared = Math.pow(750 * handler.chunkDimension, 2);
    }

    public Bullet(Vector2 pos, Vector2 direction, double damage, PhysicsObject shooter) {
        super(5, 0, 0.01, 0);
        this.pos = pos;
        this.vel = direction.scale(speed);
        this.damage = damage;
        this.shooter = shooter;
        this.maxDistanceMult = 1.0;
    }

    public Bullet(Vector2 pos, Vector2 direction, double damage, double maxDistanceMult, PhysicsObject shooter) {
        super(5, 0, 0.01, 0);
        this.pos = pos;
        this.vel = direction.scale(speed);
        this.damage = damage;
        this.shooter = shooter;
        this.maxDistanceMult = maxDistanceMult;
    }

    @Override
    public void update(double dt) {
        SimpleParticle.emit(pos, new Vector2(), 1, 0.2, this.displayColor);

        if (checkDistance()) {
            PhysicsParticle.emitCircleAway(pos, pos, 1, radius,
                    0.1, displayColor, 1, 0.5, 5);

            collided = true;
            handler.removeObject(this);
            return;
        }
        if (handler != null && !collided) {
            for (Contact c : this.contacts)
                if (c.other != shooter && c.other != this) {
                    c.other.damage(damage, pos.sub(vel.scale(0.2)));
                    PhysicsParticle.emitCircleAway(pos.add(c.normal.scale(radius)), pos, 1, radius,
                            0.1, c.other.displayColor, 1, 0.5, 10);
                    PhysicsParticle.emitCircleAway(pos.add(c.normal.scale(radius)), pos, 1, radius,
                            0.1, displayColor, 1, 0.5, 5);

                    collided = true;
                    handler.removeObject(this);
                }
        }
    }

    public boolean checkDistance() {
        distanceSquaredTraveled += vel.lengthSquared();
        if (distanceSquaredTraveled > maxDistanceSquared * maxDistanceMult)
            return true;
        else
            return false;
    }
}
