package world.effects;

import physics.process.Display;
import physics.structures.Vector2;

public class ScreenShake {

    private static double time;
    private static double maxTime;
    private static double multiplier;
    private static Display display;

    public ScreenShake(Display screenDisplay) {
        display = screenDisplay;
    }

    public static void setDisplay(Display display) {
        ScreenShake.display = display;
    }

    public static void create(double t, double m) {
        if (t <= 0 || m <= 0)
            return;
        if (t > time) {
            time = t;
            maxTime = t;
        }
        if (m > multiplier) {
            multiplier = m;
        }
    }

    public static void updateTimer(double dt) {
        if (time > 0) {
            time -= dt;
        } else {
            time = 0;
        }
    }

    public static void update(double dt) {

        if (time <= 0)
            return;

        double diff = time / maxTime;
        multiplier *= diff; // cuadratic slow down

        display.offsetVel.addLocal(Vector2.random(new Vector2(), 1).scale(multiplier));

    }

}
