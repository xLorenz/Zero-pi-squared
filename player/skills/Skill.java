package player.skills;

import player.Controller;
import player.Player;
import player.Controller.Key;

public abstract class Skill {

    protected Key triggerKey;

    protected double coolDownTime;
    protected double coolDown;

    protected boolean active;
    protected boolean ready;

    protected Skill(Key triggerKey) {
        this.triggerKey = triggerKey;
    }

    public void setTriggerKey(Key triggerKey) {
        this.triggerKey = triggerKey;
    }

    public abstract void updateTimer(double dt);

    public abstract void update(double dt, Player player);

    public abstract void handleInputs(Controller c);
}
