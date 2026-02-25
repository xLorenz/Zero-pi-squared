package particles.types;

import java.awt.Color;

import particles.Particle;
import particles.ParticlePool;
import physics.process.BatchRenderer;
import physics.structures.Vector2;

public class PhysicsParticle extends Particle {

    // Per-class pool: tune initialSize and allowGrowth as needed.
    private static final ParticlePool<PhysicsParticle> POOL = new ParticlePool<>(1500, PhysicsParticle::new,
            false);

    private static Color defColor = new Color(200, 200, 200, 220);
    private Color color = new Color(200, 200, 200, 220);
    private int radius = 5;

    private PhysicsParticle() {
    }

    /** Static factory: obtains from the class pool and initializes it. */
    public static PhysicsParticle obtain(Vector2 pos, Vector2 vel, double size, double life, Color color) {
        PhysicsParticle p = POOL.obtain();
        if (p == null)
            return null; // pool exhausted and growth disallowed
        p.reset(pos, vel, size, life);
        p.color = color;
        return p;
    }

    public static void emit(Vector2 pos, Vector2 vel, double size, double life, Color color) {
        PhysicsParticle p = PhysicsParticle.obtain(pos, vel, size, life, color);
        handler.addParticle(p);
    }

    public static void emit(Vector2 pos) {
        Vector2 vel = new Vector2(rand.nextInt(100) - 50, rand.nextInt(20) - 5);
        double life = 1.0 + rand.nextInt(20) / 10;
        double size = 1.0 + rand.nextInt(20) / 10;

        PhysicsParticle p = PhysicsParticle.obtain(pos, vel, size, life, defColor);
        handler.addParticle(p);
    }

    public static void emitCircle(Vector2 center, int minRadius, int maxRadius, double speed, Color color, double size,
            double life,
            int ammount) {
        for (int i = 0; i < ammount; i++) {
            Vector2 p = Vector2.random(center, minRadius, maxRadius);
            PhysicsParticle.emit(p, p.sub(center).scale(speed), size, life, color);
        }
    }

    public static void emitCircleAway(Vector2 pointAway, Vector2 center, int minRadius, int maxRadius, double speed,
            Color color,
            double size,
            double life,
            int ammount) {

        for (int i = 0; i < ammount; i++) {
            Vector2 p = Vector2.random(center, minRadius, maxRadius);
            Vector2 diff = p.sub(pointAway);
            PhysicsParticle.emit(p, diff.scale(speed * 10000 / diff.lengthSquared()), size, life, color);
        }
    }

    public static void emitCircleAway(Vector2 pointAway, Vector2 center, int minRadius, int maxRadius, double speed,
            Color color,
            double minSize,
            double maxSize,
            double life,
            int ammount) {

        for (int i = 0; i < ammount; i++) {
            Vector2 p = Vector2.random(center, minRadius, maxRadius);
            Vector2 diff = p.sub(pointAway);
            PhysicsParticle.emit(p, diff.scale(speed * 10000 / diff.lengthSquared()),
                    minSize + rand.nextInt(1 + (int) Math.abs(maxSize - minSize)), life, color);
        }
    }

    @Override
    public void draw(BatchRenderer renderer) {
        // fade out
        double lifeFrac = Math.max(0f, Math.min(1f, life / 2f));
        int alpha = (int) (255 * lifeFrac);

        renderer.setFill(color, alpha);
        renderer.drawCircle(pos, (int) (radius * size));
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        vel.y += 980 * dt;
    }

    @Override
    public void free() {
        this.alive = false;
        POOL.free(this);
    }

    // helpers

    public static int poolFreeCount() {
        return POOL.freeCount();
    }

    public static int poolAllocations() {
        return POOL.allocations();
    }

    public static int poolDrops() {
        return POOL.drops();
    }
}
