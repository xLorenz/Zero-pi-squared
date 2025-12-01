package src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import physics.*;

public class Player extends PhysicsBall {

    public Color color;
    public int baseSpeed = 55;
    public double baseSprintModifier = 2;
    public int baseMaxSpeed = 250;
    public int baseJumpHeight = 550;

    public Vector2 direction = new Vector2(1, 0);
    public float size = 30;

    public Keys keys = new Keys();
    public Mouse mouse = new Mouse();

    public boolean airBorne = false;
    public float airBorneTimer = 0;

    public boolean sprinting = false;

    public Player(Vector2 pos, Color color, PhysicsHandler handler) {
        super(25, 0.0, 5.0, 0L);
        this.pos = pos;
        this.color = color;
        this.setListener(new LandingListener());

        handler.addBall(this);

    }

    public class Keys {
        public List<Key> list = new ArrayList<>();
        Key space = new Key(KeyEvent.VK_SPACE);
        Key w = new Key(KeyEvent.VK_W);
        Key a = new Key(KeyEvent.VK_A);
        Key s = new Key(KeyEvent.VK_S);
        Key d = new Key(KeyEvent.VK_D);
        Key q = new Key(KeyEvent.VK_Q);
        Key e = new Key(KeyEvent.VK_E);
        Key x = new Key(KeyEvent.VK_X);
        Key control = new Key(KeyEvent.VK_CONTROL);
        Key shift = new Key(KeyEvent.VK_SHIFT);

        public Keys() {
            this.list.add(space);
            this.list.add(w);
            this.list.add(a);
            this.list.add(s);
            this.list.add(d);
            this.list.add(q);
            this.list.add(e);
            this.list.add(x);
            this.list.add(control);
            this.list.add(shift);
        }

    }

    public class Key {
        int code;
        boolean pressed = false;

        public Key(int code) {
            this.code = code;
        }
    }

    public class Mouse {
        Vector2 pos = new Vector2();
        List<Key> list = new ArrayList<>();
        Key left = new Key(1);
        Key middle = new Key(2);
        Key right = new Key(3);

        Mouse() {
            this.list.add(left);
            this.list.add(middle);
            this.list.add(right);
        }
    }

    @Override
    public void draw(Graphics g) {
        Polygon shape = new Polygon();

        Vector2 v1 = pos.add(direction.rotate(120 * 0).scale(size));
        Vector2 v2 = pos.add(direction.rotate(120 * 1).scale(size).sub(vel.scale(size / 1000)));
        Vector2 v3 = pos.add(direction.rotate(120 * 2).scale(size).sub(vel.scale(size / 1000)));

        shape.addPoint((int) (v1.x), (int) (v1.y));
        shape.addPoint((int) (v2.x), (int) (v2.y));
        shape.addPoint((int) (v3.x), (int) (v3.y));

        Color old = g.getColor();
        g.setColor(color);
        g.fillPolygon(shape);
        g.setColor(old);
    }

    public void keyPress(int keyCode) {
        for (Key k : keys.list) {
            if (k.code == keyCode) {
                k.pressed = true;
            }
        }
    }

    public void keyRelease(int keyCode) {
        for (Key k : keys.list) {
            if (k.code == keyCode) {
                k.pressed = false;
            }
        }
    }

    public void mousePress(int mouseCode) {
        for (Key k : mouse.list) {
            if (k.code == mouseCode) {
                k.pressed = true;
            }
        }
    }

    public void mouseRelease(int mouseCode) {
        for (Key k : mouse.list) {
            if (k.code == mouseCode) {
                k.pressed = false;
            }
        }
    }

    public void handleInputs() {
        if (keys.space.pressed && !airBorne) { // must be on the ground or in coyote timer
            // jump
            vel.set(new Vector2(0, -baseJumpHeight));
            airBorne = true;
        }

        // walk left
        if (keys.a.pressed) {
            if (vel.x > (sprinting ? -baseMaxSpeed * baseSprintModifier : -baseMaxSpeed)) {
                vel.x -= sprinting ? baseSpeed * baseSprintModifier : baseSpeed;
            }
        }
        // walk right
        if (keys.d.pressed) {
            if (vel.x < (sprinting ? baseMaxSpeed * baseSprintModifier : baseMaxSpeed)) {
                vel.x += sprinting ? baseSpeed * baseSprintModifier : baseSpeed;
            }
        }

        sprinting = keys.control.pressed;
    }

    public void updateTimers(double dt) {
        if (airBorne) {
            airBorneTimer += dt;
        }
        // falling and coyote time
        if (vel.y > 0.0) {
            airBorneTimer += dt;
            if (airBorneTimer > 0.3) {
                airBorne = true;
            }
        }
    }

    @Override
    public void update(double gravity, double dt) {
        vel.y += gravity * dt;
        pos.addLocal(vel.scale(dt));
        // friction
        if (airBorne) {
            vel.x *= 0.9;
        } else {
            vel.x *= 0.85;
        }
        updateTimers(dt);

        direction.set(mouse.pos.sub(pos));
        direction.normalizeLocal();
    }

    public class LandingListener extends CollisionListener {
        public LandingListener() {
            super();
        }

        // get collision below the player hitbox
        @Override
        public void action(PhysicsObject o, Manifold m) {
            // collision with rect, which top is below the triangle
            for (Vector2 c : m.contacts) {
                if (c.y > pos.y) {
                    airBorne = false; // reset airborne
                    airBorneTimer = 0; // reset airborne timer
                }
            }
        }
    }

}
