package particles.types;

import java.awt.Color;

import particles.Particle;
import particles.ParticlePool;
import physics.process.BatchRenderer;
import physics.structures.Vector2;

public class SimpleParticle extends Particle {

    // Per-class pool: tune initialSize and allowGrowth as needed.
    private static final ParticlePool<SimpleParticle> POOL = new ParticlePool<>(1500, SimpleParticle::new,
            false);

    private static Color defColor = new Color(200, 200, 200, 220);
    private Color color = new Color(200, 200, 200, 220);
    private int radius = 5;

    private SimpleParticle() {
    }

    /** Static factory: obtains from the class pool and initializes it. */
    public static SimpleParticle obtain(Vector2 pos, Vector2 vel, double size, double life, Color color) {
        SimpleParticle p = POOL.obtain();
        if (p == null)
            return null; // pool exhausted and growth disallowed
        p.reset(pos, vel, size, life);
        p.color = color;
        return p;
    }

    public static void emit(Vector2 pos, Vector2 vel, double size, double life, Color color) {
        SimpleParticle p = SimpleParticle.obtain(pos, vel, size, life, color);
        handler.addParticle(p);
    }

    public static void emit(Vector2 pos) {
        Vector2 vel = Vector2.random(-50, 50, -15, 5);
        double life = 1.0 + rand.nextInt(20) / 10;
        double size = 1.0 + rand.nextInt(20) / 10;

        SimpleParticle p = SimpleParticle.obtain(pos, vel, size, life, defColor);
        handler.addParticle(p);
    }

    public static void emitCircle(Vector2 center, int minRadius, int maxRadius, double speed, Color color, double size,
            double life,
            int ammount) {
        for (int i = 0; i < ammount; i++) {
            Vector2 p = Vector2.random(center, minRadius, maxRadius);
            SimpleParticle.emit(p, p.sub(center).scale(speed), size, life, color);
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
