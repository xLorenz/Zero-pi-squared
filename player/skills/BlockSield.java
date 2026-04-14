package player.skills;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import particles.types.SimpleParticle;
import physics.objects.PhysicsObject;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Controller;
import player.Controller.Key;
import player.Player;
import world.effects.ScreenShake;
import world.terrain.Block;

public class BlockSield extends Skill {

    PhysicsHandler handler;
    ArrayList<Rect> blocks = new ArrayList<>();
    static int radius = 3;
    static List<Vector2> tiles = getCircleTiles(radius);

    private class Rect extends Block {
        public double life = 10.0; // seconds

        public Rect(int x, int y, Color color, int chunkDimension) {
            super(x, y, color.getRed(), color.getGreen(), color.getBlue(), chunkDimension);
        }
    }

    public BlockSield(Key triggerKey, PhysicsHandler handler) {
        super(triggerKey);
        this.handler = handler;
        coolDownTime = 10.0;
        coolDown = 0.0;

        active = false;
        ready = false;
    }

    public static List<Vector2> getCircleTiles(int radiusTiles) {
        List<Vector2> result = new ArrayList<>();

        int r2 = radiusTiles * radiusTiles;

        for (int x = -radiusTiles; x <= radiusTiles; x++) {
            for (int y = -radiusTiles; y <= radiusTiles; y++) {
                int dist2 = x * x + y * y;

                if (Math.abs(dist2 - r2) <= radiusTiles) {
                    result.add(new Vector2(x, y));
                }
            }
        }

        return result;
    }

    @Override
    public void update(double dt, Player player) {
        if (active) {
            if (placeBlocks(player)) {
                coolDown = coolDownTime;

                ready = false;
            }
        }
        active = false;
        // update blocks
        ArrayList<Rect> removeQueue = new ArrayList<>();
        for (Rect r : blocks) {
            r.life -= dt;
            if (r.life <= 0) {
                removeQueue.add(r);
                handler.removeObject(r);
                SimpleParticle.emitCircle(r.pos, r.width, r.width + 1, 2, r.displayColor, 1, 1, 10);
            }
            if (r.health <= 0) {
                removeQueue.add(r);
                handler.removeObject(r);
                SimpleParticle.emitCircle(r.pos, r.width, r.width + 1, 2, r.displayColor, 1, 1, 10);

            }
        }
        for (Rect r : removeQueue) {
            if (blocks.contains(r))
                blocks.remove(r);
        }

    }

    private boolean placeBlocks(Player player) {
        int cd = handler.chunkDimension;
        for (Vector2 v : tiles) {
            boolean allowed = true;

            int x = (int) ((player.cx + v.x) * cd + cd / 2);
            int y = (int) ((player.cy + v.y) * cd + cd / 2);

            for (PhysicsObject o : handler.getObjectsInChunk((int) (player.cx + v.x), (int) (player.cy + v.y))) {
                if (o.pos.x == x && o.pos.y == y) {
                    allowed = false;
                }
            }
            if (allowed) {
                Rect rect = new Rect((int) (x / cd), (int) (y / cd), player.displayColor, cd);
                ScreenShake.create(0.5, 100);
                SimpleParticle.emitCircle(rect.pos, cd / 2, cd, 2, rect.displayColor, 1.0, 1.0, 5);

                blocks.add(rect);
                handler.addObject(rect);
            }
        }
        return true;
    }

    @Override
    public void updateTimer(double dt) {
        if (coolDown <= 0) {
            coolDown = 0;
            ready = true;
        } else {
            coolDown -= dt;
        }
    }

    @Override
    public void handleInputs(Controller c) {
        if (ready && triggerKey.singlePress) {
            active = true;
        }
    }

}
