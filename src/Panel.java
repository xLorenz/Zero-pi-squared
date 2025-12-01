package src;

import javax.swing.*;

import physics.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class Panel extends JPanel implements ActionListener {

    // set display dimensions
    static final int SCR_WIDTH = 1000;
    static final int SCR_HEIGHT = 800;

    boolean running = false;
    Timer timer;
    Random random;

    long lastTime; // used to get delta time
    double dt; // delta time
    final double FIXED_STEP = 1.0 / 60.0; // 60 Hz physics
    double accumulator = 0.0;

    PhysicsHandler handler;
    Player player;

    Vector2 mousePos = new Vector2();
    int lastMouseButton;

    // constructor
    Panel() {
        random = new Random();
        handler = new PhysicsHandler(0, 0, SCR_WIDTH, SCR_HEIGHT);
        player = new Player(new Vector2(SCR_WIDTH / 2, SCR_HEIGHT / 2), Color.cyan, handler);
        handler.addRect(SCR_WIDTH / 2, SCR_HEIGHT - 100, SCR_WIDTH, 50);
        handler.addRect(SCR_WIDTH / 2 - 100, SCR_HEIGHT - 200, 500, 50);
        handler.addRect(SCR_WIDTH / 2 + 150, SCR_HEIGHT - 275, 100, 100);
        handler.addRect(SCR_WIDTH / 2 + 200, SCR_HEIGHT - 500, 250, 100);
        handler.addBall(SCR_WIDTH / 2 + 100, SCR_HEIGHT / 2 - 100, 40, 0.9);

        this.setPreferredSize(new Dimension(SCR_WIDTH, SCR_HEIGHT));
        this.setBackground(new Color(12, 13, 20));
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        this.addMouseListener(new MyMouseAdapter());
        this.addMouseMotionListener(new MyMouseMotionAdapter());

        start();
    }

    public void start() {
        running = true;
        lastTime = System.nanoTime();
        timer = new Timer(16, this);
        timer.start();

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // antialias
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // quality over speed, better gradients
        // g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        // RenderingHints.VALUE_RENDER_QUALITY);
        // sharper lines
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        // smoother images
        // g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        // RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        draw(g);
    }

    // ----------------------------------------//
    public void draw(Graphics g) {
        handler.displayChunkBorders(g, SCR_WIDTH, SCR_HEIGHT);
        handler.displayObjects(g);
        // collision debug overlay
        // handler.displayCollisionDebug(g);
    }
    // ----------------------------------------//

    public void update() {
        long now = System.nanoTime();
        dt = (now - lastTime) / 1_000_000_000.0; // seconds
        lastTime = now;
        dt = Math.min(dt, 0.25); // avoid huge jumps
        // accumulate and run fixed-step updates for deterministic physics
        accumulator += dt;
        while (accumulator >= FIXED_STEP) {
            handler.updatePhysics(FIXED_STEP); // implement physics update using fixed step
            accumulator -= FIXED_STEP;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            update(); // update physics and player timers
            player.handleInputs();

        }
        // render once per frame (use the remaining fractional time if needed)
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPress(e.getKeyCode());
        }

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyRelease(e.getKeyCode());
        }
    }

    public class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            player.mousePress(e.getButton());
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            player.mouseRelease(e.getButton());
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                // System.out.println("Double click at " + e.getX() + "," + e.getY());
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // optional
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // optional: you may want to reset coords
        }
    }

    public class MyMouseMotionAdapter extends MouseMotionAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            mouseMovedOrDragged(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            mouseMovedOrDragged(e);
        }
    }

    private void mouseMovedOrDragged(MouseEvent e) {
        mousePos.set(e.getX(), e.getY());
        player.mouse.pos.set(mousePos);
        // Optionally get global screen position:
        // Point screenPoint = e.getLocationOnScreen();
        // System.out.println("Screen pos: " + screenPoint.x + "," + screenPoint.y);
    }

}