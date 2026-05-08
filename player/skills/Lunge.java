package player.skills;

import particles.types.SimpleParticle;
import player.Controller;
import player.Controller.Key;
import player.Player;

public class Lunge extends Skill {

    public Lunge(Key triggerKey) {
        super(triggerKey);
        this.coolDownTime = 3.0;
    }

    @Override
    public void updateTimer(double dt) {
        if (coolDown > 0.0) {
            coolDown -= dt;
            ready = false;
        } else {
            coolDown = 0.0;
            ready = true;
        }
    }

    @Override
    public void update(double dt, Player player) {
        if (active) {
            SimpleParticle.emitCircle(player.pos, 1, player.radius, 2, player.displayColor, 1, 2.5, 25);

            if (player.controller.keys.w.pressed) // up
                player.vel.set(player.vel.x, -player.attributes.baseSpeed * 0.75);

            if (player.controller.keys.s.pressed) // down
                player.vel.set(player.vel.x, player.attributes.baseSpeed * 0.75);

            if (player.controller.keys.a.pressed) // left
                player.vel.set(-player.attributes.baseSpeed * 0.75, player.vel.y);

            if (player.controller.keys.d.pressed) // right
                player.vel.set(player.attributes.baseSpeed * 0.75, player.vel.y);

            active = false;
        }
    }

    @Override
    public void handleInputs(Controller c) {
        if (triggerKey.pressed && ready) {
            active = true;
            coolDown = coolDownTime;
        }
    }

}
