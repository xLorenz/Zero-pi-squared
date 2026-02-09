package player.skills;

import particles.types.SimpleParticle;
import physics.structures.Vector2;
import player.Controller;
import player.Player;

public class DoubleJump extends Skill {

    double thresholdForActivation = 0.1;
    boolean used = false;

    public DoubleJump() {
        coolDownTime = 0;
        coolDown = coolDownTime;
        active = false;
        ready = true;
    }

    @Override
    public void update(double dt, Player player) {

        if (active) {
            player.vel.y = -player.baseJumpStrength;
            for (int i = 0; i < 10; i++) {
                SimpleParticle.emit(
                        player.pos,
                        Vector2.random(-50, 50, -50, 50),
                        1.2,
                        1.5,
                        player.color);
            }
            ready = false;
            active = false;
            used = true;
        }

        if (player.supported) {
            used = false;
        }

        if (!used)
            if (player.airBorneTimer > thresholdForActivation) {
                ready = true;
            } else {
                ready = false;
            }

    }

    @Override
    public void updateTimer(double dt) {

    }

    @Override
    public void handleInputs(Controller c) {
        if (c.keys.space.singlePress() && ready) {
            active = true;
        }
    }
}