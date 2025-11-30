package src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

import physics.*;

public class Player extends PhysicsBall {

    public Color color;
    public int baseSpeed = 10;
    public int baseJumpHeight = 50;
    public Vector2 direction = new Vector2(1, 0);
    public float size = 30;

    Player(Vector2 pos, Color color, PhysicsHandler handler) {
        super(25, 0.0, 10.0, 0L);
        this.pos = pos;
        this.color = color;

        handler.addBall(this);
    }

    @Override
    public void draw(Graphics g) {
        Polygon shape = new Polygon();

        Vector2 v1 = pos.add(direction.rotate(120 * 0).scale(size));
        Vector2 v2 = pos.add(direction.rotate(120 * 1).scale(size));
        Vector2 v3 = pos.add(direction.rotate(120 * 2).scale(size));

        shape.addPoint((int) (v1.x), (int) (v1.y));
        shape.addPoint((int) (v2.x), (int) (v2.y));
        shape.addPoint((int) (v3.x), (int) (v3.y));

        Color old = g.getColor();
        g.setColor(color);
        g.fillPolygon(shape);
        g.setColor(old);
    }

}
