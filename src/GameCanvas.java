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
import sound.AudioLoader;
import sound.AudioManager;
import world.effects.Explosion;
import world.effects.ScreenShake;
import world.terrain.Generator;
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
    private Player player = new Player(new Vector2(0, 0), handler);

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

        AudioLoader.loadAudioFiles();

        setUpGame();
    }

    private void setUpGame() {
        handler.chunkDimension = 31;
        handler.display.scale = 0.5;
        handler.display.followRadius = 0;
        handler.display.offsetAccel = 2.0;
        handler.display.offsetFriction = 0.8;
        handler.display.renderDistance = 64;
        handler.display.mainObject = player;
        handler.display.setScreenCenter(new Vector2(size.width / 2, size.height / 2));
        Enemy.handler = handler;
        Enemy.player = player;
        Explosion.handler = handler;
        ScreenShake.setDisplay(handler.display);

        Generator generator = new Generator(handler);
        generator.loadMapImage("src/map.png");
        generator.generateMap();

        player.pos.set(new Vector2(125 * handler.chunkDimension, 125 *
                handler.chunkDimension));

        AudioManager.playMusic("menu_music_0", true);
        AudioManager.setMusicVolume(0.5);

        // spawn test enemies
        for (int i = 0; i < 10; i++) {
            new Normie(Vector2.random(100, 5000, 2500, 2700));
        }
        for (int i = 0; i < 10; i++) {
            new Speedster(Vector2.random(100, 5000, 2500, 2700));
        }
        for (int i = 0; i < 10; i++) {
            new Jumper(Vector2.random(100, 5000, 2500, 2700));
        }

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
                frame.setTitle("Project" +
                        " | FPS: " + fps +
                        " | TPS: " + handler.getUpdater().getUps() +
                        " | Objs: " + handler.getUpdateObjectsSnapshot().size() +
                        " | Part: " + particleHandler.getUpdateParticles().size());
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
                    // handler.drawRecordedChunks(g, size.width, size.height, true);
                    handler.render(g);
                    // handler.renderDebug(g);
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
        ScreenShake.updateTimer(dt);
        ScreenShake.update(dt);
    }

}
