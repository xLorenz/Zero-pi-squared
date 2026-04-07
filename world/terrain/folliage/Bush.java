package world.terrain.folliage;

import java.util.ArrayList;
import java.util.Random;

import physics.objects.PhysicsObject;
import physics.process.BatchRenderer;
import physics.structures.Vector2;
import world.terrain.NoCollisionBlock;

public class Bush extends NoCollisionBlock {

    private static double stretchAccel = 10.0;

    private ArrayList<Vector2> leaves = new ArrayList<>();
    private int defaultSize;

    private double widthStretchVel = 0.0;
    private double heightStretchVel = 0.0;

    public Bush(int x, int y, int r, int g, int b, int chunkDimension) {
        super(x, y, r, g, b, chunkDimension);

        defaultSize = chunkDimension;

        for (int i = 0; i < new Random().nextInt(20); i++) {
            Vector2 v = Vector2.random(new Vector2(), 1, width);
            leaves.add(v);
        }
    }

    @Override
    public void draw(BatchRenderer renderer) {
        super.draw(renderer);
        renderer.setFill(displayColor.darker().darker(), 200);
        for (Vector2 v : leaves) {
            renderer.drawCircle(pos.add(new Vector2(v.x * width / defaultSize, v.y * height / defaultSize)),
                    defaultSize / 2);
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

}
