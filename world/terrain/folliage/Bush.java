package world.terrain.folliage;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import particles.types.SimpleParticle;
import physics.objects.PhysicsObject;
import physics.process.BatchRenderer;
import physics.structures.Manifold;
import physics.structures.Vector2;
import player.Player;
import world.terrain.NoCollisionBlock;

public class Bush extends NoCollisionBlock {

    private static double stretchAccel = 10.0;
    private static Random rand = new Random();

    private ArrayList<Leaf> leaves = new ArrayList<>();
    private int defaultSize;

    private double widthStretchVel = 0.0;
    private double heightStretchVel = 0.0;

    private class Leaf {
        Vector2 v;
        int radius;
        Color color;

        private Leaf(Vector2 v, int radius, Color color) {
            this.v = v;
            this.radius = radius;
            this.color = color;
            if (rand.nextBoolean())
                this.color = color.darker();
        }
    }

    public Bush(int x, int y, int r, int g, int b, int chunkDimension) {
        super(x, y, r, g, b, chunkDimension);

        defaultSize = chunkDimension;

        for (int i = 0; i < 2 + rand.nextInt(5); i++) {
            Vector2 v = Vector2.random(new Vector2(), 1, width);
            leaves.add(new Leaf(v, defaultSize / 3 + rand.nextInt(defaultSize / 2), displayColor.darker().darker()));
        }
    }

    @Override
    public void draw(BatchRenderer renderer) {
        for (Leaf l : leaves) {
            renderer.setFill(l.color, 200);
            renderer.drawCircle(pos.add(new Vector2(l.v.x * width / defaultSize / 2, l.v.y * height / defaultSize / 2)),
                    l.radius);
        }
    }

    @Override
    public void update(double dt) {
        super.update(dt);
        ArrayList<PhysicsObject> list = getCollisions();
        if (!list.isEmpty()) {
            for (PhysicsObject o : list) {
                if (Math.abs(o.vel.x) > Math.abs(o.vel.y)) {
                    heightStretchVel = Math.abs(o.vel.x) / 100;
                } else {
                    widthStretchVel = Math.abs(o.vel.y) / 100;
                }
            }
        }
        updateStretchVelocities(dt);
    }

    private void updateStretchVelocities(double dt) {

        // widht stretch
        width += widthStretchVel;
        // height stretch
        height += heightStretchVel;
        // pos.y = defaultPos.y - (height - defaultSize) / 2;

        if (width > defaultSize * 2) {
            width = defaultSize * 2;
            widthStretchVel = 0;
        }
        if (height > defaultSize * 2) {
            height = defaultSize * 2;
            heightStretchVel = 0;
        }

        if (width > defaultSize) {
            widthStretchVel -= (width - defaultSize) * stretchAccel * dt;
        } else {
            widthStretchVel += (defaultSize - width) * stretchAccel * dt;
        }

        if (height > defaultSize) {
            heightStretchVel -= (height - defaultSize) * stretchAccel * dt;
        } else {
            heightStretchVel += (defaultSize - height) * stretchAccel * dt;
        }

        // friction
        widthStretchVel *= 0.8;
        heightStretchVel *= 0.8;
    }

    @Override
    public void emitContactSmokeParticles(PhysicsObject b, Manifold colision) {
        if (colision != null && b instanceof Player) {
            if (b.vel.lengthSquared() > 10.0) {
                // if (colision.penetration > 1.0) {
                SimpleParticle.emitCircleAway(
                        b.pos.sub(b.vel.normalize().scale(5)),
                        b.pos,
                        1, 2,
                        b.vel.length() * 0.7,
                        new Color(displayColor.getRed(), displayColor.getGreen(), displayColor.getBlue(), 50).darker()
                                .darker(),
                        2.5,
                        3.0, rand.nextInt(1 + (int) (Math.abs(b.vel.length()) / 400)));
            }
        }
    }

    @Override
    public Manifold collide(PhysicsObject other) {
        Manifold m = other.collideWithRect(this);
        updateCollision(other, m);
        emitContactSmokeParticles(other, m);
        return null;
    }

}
