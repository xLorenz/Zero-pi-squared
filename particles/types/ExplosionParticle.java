package particles.types;

import java.awt.Color;

import particles.Particle;
import particles.ParticlePool;
import physics.process.BatchRenderer;
import physics.structures.Vector2;

public class ExplosionParticle extends Particle {

    // Per-class pool: tune initialSize and allowGrowth as needed.
    private static final ParticlePool<ExplosionParticle> POOL = new ParticlePool<>(200, ExplosionParticle::new,
            false);

    private Color color = Color.orange;
    private double rotAng = rand.nextInt(360);

    private Vector2[] defPoints = new Vector2[20];
    private Vector2[] points = new Vector2[defPoints.length];

    {
        for (int i = 0; i < defPoints.length; i++) {
            if (i % 2 == 0) {
                defPoints[i] = new Vector2(1, 0).rotate(rotAng + (i * 360.0 / defPoints.length))
                        .scaleLocal(100 + rand.nextInt(100));
            } else {
                defPoints[i] = new Vector2(1, 0).rotate(rotAng + (i * 360.0 / defPoints.length))
                        .scaleLocal(50 + rand.nextInt(50));
            }
            points[i] = new Vector2(defPoints[i].x, defPoints[i].y);
        }
    }

    private ExplosionParticle() {
    }

    /** Static factory: obtains from the class pool and initializes it. */
    public static ExplosionParticle obtain(Vector2 pos, Vector2 vel, double size, double life, Color color) {
        ExplosionParticle p = POOL.obtain();
        if (p == null)
            return null; // pool exhausted and growth disallowed
        p.reset(pos, vel, size, life);
        p.color = color;

        for (int i = 0; i < p.points.length; i++) {
            p.points[i].set(p.defPoints[i].scale(size));
        }
        return p;
    }

    public static void emit(Vector2 pos, Vector2 vel, double size, double life, Color color) {
        ExplosionParticle p = ExplosionParticle.obtain(pos, vel, size, life, color);
        handler.addParticle(p);
    }

    public static void emit(Vector2 pos, double size, Color color) {
        Vector2 vel = new Vector2(0, 0);
        double life = 0.5;

        ExplosionParticle p = ExplosionParticle.obtain(pos, vel, size, life, color);
        handler.addParticle(p);
    }

    @Override
    public void draw(BatchRenderer renderer) {
        // fade out
        double lifeFrac = Math.max(0f, Math.min(1f, life / 2f));
        int alpha = (int) (255 * lifeFrac);

        Vector2[] ps = new Vector2[points.length];

        for (int i = 0; i < points.length; i++) {
            Vector2 p = points[i].add(pos);
            ps[i] = new Vector2(p.x, p.y);
        }

        renderer.setFill(color, alpha);
        renderer.drawPolygon(ps, points.length);

        for (int i = 0; i < points.length; i++) {
            ps[i].set(points[i].scale(0.5).add(pos));
        }

        renderer.setFill(color.brighter(), alpha);
        renderer.drawPolygon(ps, points.length);
    }

    @Override
    public void update(double dt) {
        super.update(dt);
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
