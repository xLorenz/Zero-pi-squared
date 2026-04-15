package world.terrain;

import java.awt.Color;

import particles.types.SimpleParticle;
import physics.objects.PhysicsBall;
import physics.objects.PhysicsObject;
import physics.objects.PhysicsRect;
import physics.process.PhysicsHandler;
import physics.structures.Manifold;
import physics.structures.Vector2;
import player.Player;

public class Block extends PhysicsRect {
    public static PhysicsHandler handler;

    public Block folliage;

    public double health = 50;

    public Block(int x, int y, int r, int g, int b, int chunkDimension) {
        super(chunkDimension, chunkDimension, 0, 0);
        this.stationary = true;
        this.sleepFrames = 1;

        this.pos.set(new Vector2(x * chunkDimension + (int) (chunkDimension / 2),
                y * chunkDimension + (int) (chunkDimension / 2)));

        this.displayColor = new Color(r, g, b);
        this.elasticity = 0.2;
        this.friction = 0.2;
    }

    public void setFolliage(Block folliage) {
        this.folliage = folliage;
        folliage.health = health;
    }

    @Override
    public void damage(double ammount, Vector2 pos) {
        if (folliage != null) {
            folliage.damage(ammount, pos);
        }
        health -= ammount;
        if (health <= 0) {
            health = 0;
            if (handler != null) {
                handler.removeObject(this);
            }

        }
    }

    @Override
    public void onColisionWithCircle(PhysicsBall b, Manifold m) {
        emitContactSmokeParticles(b, m);
    }

    public void emitContactSmokeParticles(PhysicsObject b, Manifold colision) {

        if (colision != null && b instanceof Player) {
            // if (b.vel.lengthSquared() > 200000.0) {
            if (colision.penetration > 1.0) {
                SimpleParticle.emitCircleAway(
                        pos.add(colision.normal.scale(0.5 * height * 0.9)),
                        pos.add(colision.normal.scale(0.5 * height)),
                        1, 2,
                        b.vel.length() / 10,
                        new Color(displayColor.getRed(), displayColor.getGreen(), displayColor.getBlue(), 50),
                        2.5,
                        2.0, (int) (b.vel.length() / 100));
            }
        }
    }

}
