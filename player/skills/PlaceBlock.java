package player.skills;

import java.awt.Color;
import java.util.ArrayList;

import particles.types.SimpleParticle;
import physics.objects.PhysicsObject;
import physics.process.PhysicsHandler;
import player.Controller;
import player.Controller.Key;
import world.terrain.Block;
import player.Player;

public class PlaceBlock extends Skill {

    private ArrayList<Rect> blocks = new ArrayList<>();
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
            boolean allowed = true;
            double mx = handler.display.getMapPos(player.controller.mouse.pos).x;
            double my = handler.display.getMapPos(player.controller.mouse.pos).y;

            int dx = (int) Math.floor(mx / handler.chunkDimension);
            int dy = (int) Math.floor(my / handler.chunkDimension);

            int x = (int) (dx * handler.chunkDimension + handler.chunkDimension / 2);
            int y = (int) (dy * handler.chunkDimension + handler.chunkDimension / 2);

            for (PhysicsObject o : handler.getUpdateObjectsSnapshot()) {
                if (o.pos.x == x && o.pos.y == y) {
                    allowed = false;
                }
            }
            if (allowed) {

                int cd = handler.chunkDimension;
                Rect rect = new Rect((int) (x / cd), (int) (y / cd), player.color, cd);

                blocks.add(rect);
                handler.addObject(rect);

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

    @Override
    public void handleInputs(Controller c) {
        if (ready && triggerKey.pressed) {
            active = true;
        }
    }

    @Override
    public void updateTimer(double dt) {
        if (coolDown < 0) {
            coolDown = 0;
            ready = true;
        } else {
            coolDown -= dt;
        }
    }
}
