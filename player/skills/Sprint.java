package player.skills;

import java.util.Random;

import particles.types.SimpleParticle;
import physics.structures.Vector2;
import player.Controller;
import player.Player;
import player.Controller.Key;

public class Sprint extends Skill {

    Random rand = new Random();

    double thresholdForActivation = 1.5;

    public Sprint(Key triggerKey) {
        super(triggerKey);
        coolDownTime = 5;
        coolDown = coolDownTime;
        active = false;
        ready = true;
    }

    @Override
    public void update(double dt, Player player) {
        if (active) {
            player.speedMultiplier = 1.5;

            if (player.supported && player.vel.x != 0)
                SimpleParticle.emit(
                        new Vector2(player.pos.x, player.pos.y + player.radius),
                        new Vector2(-player.vel.x / 10, -50 + rand.nextInt(50)),
                        0.75 + rand.nextInt(20) / 10,
                        1,
                        player.color);
        } else {
            player.speedMultiplier = 1;
        }
    }

    @Override
    public void updateTimer(double dt) {
        if (coolDown > coolDownTime) {
            coolDown = coolDownTime;
        } else {
            if (coolDown <= 0) {
                active = false;
                coolDown = 0;
            }

            if (!active) {
                coolDown += dt;
            }
        }
        if (active && coolDown > 0) {
            coolDown -= dt;
        }
    }

    @Override
    public void handleInputs(Controller c) {
        if (triggerKey.pressed) {
            if (coolDown > thresholdForActivation || active) {
                active = true;
            }
        } else {
            active = false;
        }
    }
}
