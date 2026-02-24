package src;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import player.*;
import world.effects.Explosion;
import enemies.*;
import particles.ParticleHandler;
import physics.process.PhysicsHandler;
import physics.structures.Vector2;

public class GameCanvas extends Canvas implements Runnable {

    private int fps;
    private int frames;
    private long fpsTimer = System.nanoTime();

    private boolean running = true;
    private Dimension size = new Dimension(1000, 800);

    private Vector2 mousePos = new Vector2();

    private PhysicsHandler handler = new PhysicsHandler();
    private ParticleHandler particleHandler = new ParticleHandler(handler);
    private Player player = new Player(new Vector2(0, 0), Color.cyan, handler);

    private Thread updaterThread;
    private Thread gameThread;

    private Adapters adapters = new Adapters(player, handler, mousePos);

    private static final RenderingHints HINTS = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

    static {
        HINTS.put(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_NORMALIZE);
    }

    @Override
    public void addNotify() {
        super.addNotify(); // IMPORTANT

        if (getBufferStrategy() == null) {
            createBufferStrategy(2);
        }

        handler.beginUpdaterThread();

        if (updaterThread == null || !updaterThread.isAlive()) {
            updaterThread = new Thread(particleHandler.getUpdater(), "Particle-Updater");
            updaterThread.setDaemon(true);
            updaterThread.start();
        }
        if (!Thread.currentThread().getName().equals("AWT-EventQueue-0")) {
            // typically you start your game loop on your own; if you want to start here:
            gameThread = new Thread(this, "GameLoop");
            gameThread.setDaemon(true);
            gameThread.start();
        }
    }

    @Override
    public void removeNotify() {
        if (particleHandler.getUpdater() != null) {
            particleHandler.getUpdater().stop();
        }
        handler.stopUpdaterThread();

        super.removeNotify();
    }

    public GameCanvas() {

        this.setPreferredSize(size);
        this.setIgnoreRepaint(true);
        this.setBackground(new Color(12, 13, 20));
        this.setFocusable(true);

        this.addKeyListener(adapters.keyAdapter);
        this.addMouseListener(adapters.mouseAdapter);
        this.addMouseMotionListener(adapters.mouseMotionAdapter);

        setUpGame();
    }

    private void setUpGame() {
        handler.display.scale = 0.5;
        handler.display.followRadius = 100;
        handler.display.offsetAccel = 0.5;
        handler.display.offsetFriction = 0.95;
        handler.display.mainObject = player;
        handler.display.setScreenCenter(new Vector2(size.width / 2, size.height / 2));
        Enemy.handler = handler;
        Enemy.player = player;
        Explosion.handler = handler;

        // spawn test enemies
        for (int i = 0; i < 20; i++) {
            new Normie(Vector2.random(-2000, 2000, -1000, -900));
        }
        for (int i = 0; i < 20; i++) {
            new Speedster(Vector2.random(-2000, 2000, -1000, -900));
        }
        for (int i = 0; i < 20; i++) {
            new Jumper(Vector2.random(-2000, 2000, -1000, -900));
        }

        // add "terrain"
        handler.addRect(new Vector2(size.width / 2, size.height + 150), size.width * 10, 500); // bottom

        handler.addRect(new Vector2(-size.width * 4, 0), 20, size.height * 2); // walls
        handler.addRect(new Vector2(size.width * 5, 0), 20, size.height * 2);

        handler.addRect(new Vector2(size.width / 2 - 100, size.height - 200), 500, 50);
        handler.addRect(new Vector2(size.width / 2 + 150, size.height - 275), 100, 100);
        handler.addRect(new Vector2(size.width / 2 + 300, size.height - 375), 250, 100);
        handler.addRect(new Vector2(size.width / 2 - 300, size.height - 400), 300, 25);
        handler.addRect(new Vector2(0, size.height - 712), 300, 25);
        handler.addRect(new Vector2(0, size.height - 850), 300, 25);

    }

    @Override
    public void run() {
        long last = System.nanoTime();

        while (running) {
            long now = System.nanoTime();
            float dt = (now - last) * 1e-9f;
            last = now;

            update(dt);
            render();

            frames++;
            if (now - fpsTimer >= 1_000_000_000L) {
                fps = frames;
                frames = 0;
                fpsTimer = now;

                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
                frame.setTitle("Project | FPS: " + fps);
            }
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            // create if missing (defensive), but prefer create in addNotify()
            createBufferStrategy(2);
            bs = getBufferStrategy();
            if (bs == null)
                return;
        }

        do {
            do {
                Graphics2D g = (Graphics2D) bs.getDrawGraphics();
                try {
                    g.addRenderingHints(HINTS);

                    g.setColor(getBackground());
                    g.fillRect(0, 0, size.width, size.height);

                    // draw game

                    particleHandler.renderBgParticles(g);

                    // handler.displayChunkBorders(g, size.width, size.height);
                    // handler.drawRecordedChunks(g);
                    handler.render(g);
                    // collision debug overlay
                    // handler.displayCollisionDebug(g);
                    particleHandler.renderFgParticles(g);

                } finally {
                    g.dispose();
                }

            } while (bs.contentsRestored());

            bs.show();
            Toolkit.getDefaultToolkit().sync();

        } while (bs.contentsLost());

    }

    private void update(float dt) {
        handler.display.update(dt);
        player.handleInputs(dt);
    }

}
