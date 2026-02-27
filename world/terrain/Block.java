package world.terrain;

import java.awt.Color;

import physics.objects.PhysicsRect;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;

public class Block extends PhysicsRect {
    public static PhysicsHandler handler;

    public double health = 50;

    public Block(int x, int y, int r, int g, int b, int chunkDimension) {
        super(chunkDimension - 1, chunkDimension - 1, 0, 0);
        this.stationary = true;
        this.sleepFrames = 1;

        this.pos.set(new Vector2(x * chunkDimension + (int) (chunkDimension / 2),
                y * chunkDimension + (int) (chunkDimension / 2)));

        this.displayColor = new Color(r, g, b);
        this.elasticity = 0;
    }

    @Override
    public void damage(double ammount, Vector2 pos) {
        health -= ammount;
        if (health <= 0) {
            health = 0;
            if (handler != null)
                handler.removeObject(this);
        }
    }

}
