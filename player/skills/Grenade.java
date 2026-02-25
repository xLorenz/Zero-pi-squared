package player.skills;

import java.awt.Color;
import java.util.ArrayList;

import particles.types.SimpleParticle;
import physics.objects.PhysicsBall;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;
import player.Controller;
import player.Player;
import world.effects.Explosion;

public class Grenade extends Skill {

    private ArrayList<Nade> grenades = new ArrayList<>();
    private PhysicsHandler handler;

    private class Nade extends PhysicsBall {
        public double fuse = 2.5;

        public Nade(int radius, Color color) {
            super(radius, 0.5, 0.1, 0);
            displayColor = color;
        }
    }

    public Grenade(PhysicsHandler handler) {
        this.handler = handler;

        coolDownTime = 0.5;
        coolDown = 0.0;

        active = false;
        ready = false;
    }

    @Override
    public void update(double dt, Player player) {
        if (active) {
            Nade n = new Nade(10, player.color);
            n.pos.set(player.pos.add(player.direction.scale(player.radius)));
            n.vel.set(player.direction.scale(500));
            handler.addObject(n);
            grenades.add(n);

            coolDown = coolDownTime;
            ready = false;
        }
        active = false;
        // update blocks
        ArrayList<Nade> removeQueue = new ArrayList<>();
        for (Nade n : grenades) {
            SimpleParticle.emit(n.pos, Vector2.random(-10, 10, -100, 0), 2.0, 1.0, Color.black);
            n.fuse -= dt;
            if (n.fuse <= 0) {
                removeQueue.add(n);
                handler.removeObject(n);
                Explosion.emit(n.pos, 200, 100, 5.0);
            }
        }
        for (Nade n : removeQueue) {
            if (grenades.contains(n))
                grenades.remove(n);
        }

    }

    @Override
    public void handleInputs(Controller c) {
        if (ready && c.mouse.left.pressed) {
            active = true;
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
