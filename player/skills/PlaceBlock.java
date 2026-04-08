package player.skills;

import java.awt.Color;
import java.util.ArrayList;

import particles.types.SimpleParticle;
import physics.objects.PhysicsObject;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Controller;
import player.Controller.Key;
import world.effects.ScreenShake;
import world.terrain.Block;
import player.Player;

public class PlaceBlock extends Skill {

    private ArrayList<Rect> blocks = new ArrayList<>();
    private Rect lastPlacedBlock;
    private PhysicsHandler handler;

    private class Rect extends Block {
        public double life = 10.0; // seconds

        public Rect(int x, int y, Color color, int chunkDimension) {
            super(x, y, color.getRed(), color.getGreen(), color.getBlue(), chunkDimension);
        }
    }

    public PlaceBlock(Key triggerKey, PhysicsHandler handler) {
        super(triggerKey);
        this.handler = handler;

        coolDownTime = 0.01;
        coolDown = 0.0;

        active = false;
        ready = false;
    }

    @Override
    public void update(double dt, Player player) {
        if (active) {
            if (placeBlocks(player)) {
                coolDown = coolDownTime;

                // ready = false;
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
        // get chunk tile positions
        // start
        double mx = handler.display.getMapPos(player.controller.mouse.lastPos).x;
        double my = handler.display.getMapPos(player.controller.mouse.lastPos).y;

        if (lastPlacedBlock != null) {
            mx = lastPlacedBlock.pos.x;
            my = lastPlacedBlock.pos.y;
        }

        int x0 = (int) Math.floor(mx / cd);
        int y0 = (int) Math.floor(my / cd);

        // end
        mx = handler.display.getMapPos(player.controller.mouse.pos).x;
        my = handler.display.getMapPos(player.controller.mouse.pos).y;

        int x1 = (int) Math.floor(mx / cd);
        int y1 = (int) Math.floor(my / cd);

        ArrayList<Vector2> list = Vector2.drawTileLine(x0, y0, x1, y1);
        for (Vector2 v : list) {
            boolean allowed = true;

            int x = (int) (v.x * cd + cd / 2);
            int y = (int) (v.y * cd + cd / 2);

            for (PhysicsObject o : handler.getObjectsInChunk((int) (v.x), (int) (v.y))) {
                if (o.pos.x == x && o.pos.y == y) {
                    allowed = false;
                }
            }
            if (allowed) {

                Rect rect = new Rect((int) (x / cd), (int) (y / cd), player.color, cd);
                ScreenShake.create(0.5, 100);
                SimpleParticle.emitCircle(rect.pos, cd / 2, cd, 2, rect.displayColor, 1.0, 1.0, 5);

                blocks.add(rect);
                handler.addObject(rect);

                lastPlacedBlock = rect;
            }
        }
        return !list.isEmpty();

    }

    @Override
    public void handleInputs(Controller c) {
        if (ready && triggerKey.pressed) {
            active = true;
        } else {
            lastPlacedBlock = null;
            // System.out.println("null");
        }
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
}
