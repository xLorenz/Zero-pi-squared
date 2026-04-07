package world.terrain;

import java.awt.Color;
import java.util.ArrayList;

import physics.collisions.Collision;
import physics.objects.PhysicsBall;
import physics.objects.PhysicsObject;
import physics.objects.PhysicsRect;
import physics.process.BatchRenderer;
import physics.structures.Manifold;

public class NoCollisionBlock extends Block {
    public static Color COLOR = new Color(0, 255, 0);

    private ArrayList<PhysicsObject> collisions = new ArrayList<>();

    public NoCollisionBlock(int x, int y, int r, int g, int b, int chunkDimension) {
        super(x, y, r, g, b, chunkDimension);
    }

    public ArrayList<PhysicsObject> getCollisions() {
        synchronized (collisions) {
            return new ArrayList<>(collisions);
        }
    }

    @Override
    public void update(double dt) {
        if (!getCollisions().isEmpty()) {
            for (PhysicsObject o : getCollisions())
                if (!handler.getUpdateObjectsSnapshot().contains(o)) {
                    if (collisions.contains(o))
                        collisions.remove(o);
                }
        }
    }

    @Override
    public void drawDebug(BatchRenderer renderer) {
        if (collisions.isEmpty()) {
            renderer.setFill(Color.darkGray.darker(), 255);
        } else {
            renderer.setFill(Color.gray, 255);
        }
        renderer.drawRectOutline(pos, width, height);
    }

    @Override
    public Manifold collide(PhysicsObject other) {
        Manifold m = other.collideWithRect(this);
        updateCollision(other, m);
        return null;
    }

    // hooks for double dispatch
    @Override
    public Manifold collideWithCircle(PhysicsBall b) {
        Manifold m = Collision.circleRect(b, this);
        updateCollision(b, m);
        return null;
    }

    @Override
    public Manifold collideWithRect(PhysicsRect rect) {
        Manifold m = Collision.rectRect(rect, this);
        updateCollision(rect, m);
        return null;
    }

    public void updateCollision(PhysicsObject o, Manifold m) {
        if (m != null) {
            if (!collisions.contains(o))
                collisions.add(o);
        } else {
            if (collisions.contains(o))
                collisions.remove(o);
        }
    }

}
