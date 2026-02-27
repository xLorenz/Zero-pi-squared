package player.skills;

import java.awt.Color;
import java.util.ArrayList;

import particles.types.SimpleParticle;
import physics.objects.PhysicsRect;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Controller;
import player.Player;

public class PlaceBlock extends Skill {

    private ArrayList<Rect> blocks = new ArrayList<>();
    private PhysicsHandler handler;

    private class Rect extends PhysicsRect {
        public double life = 10.0;

        public Rect(int width, int height, Color color) {
            super(width, height, 0, 0);
            stationary = true;
            displayColor = color;
        }
    }

    public PlaceBlock(PhysicsHandler handler) {
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

            for (PhysicsRect o : blocks) {
                if (o.pos.x == x && o.pos.y == y) {
                    allowed = false;
                }
            }
            if (allowed) {

                Rect rect = new Rect(handler.chunkDimension - 1, handler.chunkDimension - 1, player.color);
                rect.pos = new Vector2(x, y);

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
        }
        for (Rect r : removeQueue) {
            if (blocks.contains(r))
                blocks.remove(r);
        }

    }

    @Override
    public void handleInputs(Controller c) {
        if (ready && c.mouse.right.pressed) {
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
