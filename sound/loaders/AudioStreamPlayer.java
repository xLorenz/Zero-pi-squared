package sound.loaders;

import java.net.URL;
import java.util.concurrent.locks.ReentrantLock;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

public class AudioStreamPlayer {

    private final ReentrantLock lineLock = new ReentrantLock();
    private SourceDataLine line;

    private AudioInputStream stream;

    private Thread streamThread;
    private Thread fadeThread;

    private volatile boolean running = false;
    private volatile boolean paused = false;
    private volatile boolean loop = false;

    private volatile double volume = 1.0; // target volume for fade
    private volatile double currentVolume = 1.0; // actual applied vol

    private volatile boolean lineReady = false;

    private URL currentUrl;

    public void play(URL url, boolean loop) {
        stop();

        this.currentUrl = url;
        this.loop = loop;
        this.running = true;
        this.paused = false;

        streamThread = new Thread(this::streamLoop, "audio-stream-thread");
        streamThread.start();
    }

    private void streamLoop() {
        do {
            SourceDataLine localLine = null;
            try {
                openStream(currentUrl);

                AudioFormat format = stream.getFormat();
                localLine = AudioSystem.getSourceDataLine(format);
                localLine.open(format);
                localLine.start();

                // publish localline
                lineLock.lock();
                try {
                    line = localLine;
                } finally {
                    lineLock.unlock();
                }

                lineReady = true;

                applyVolume();

                byte[] buffer = new byte[4096];
                int bytesRead;

                while (running && (bytesRead = stream.read(buffer, 0, buffer.length)) != -1) {
                    // handle pause
                    synchronized (this) {
                        while (paused) {
                            lineLock.lock();
                            try {
                                if (line != null)
                                    line.stop();
                            } finally {
                                lineLock.unlock();
                            }

                            wait();

                            if (!running)
                                return;
                            lineLock.lock();
                            try {
                                if (line != null)
                                    line.start();
                            } finally {
                                lineLock.unlock();
                            }
                        }
                    }
                    lineLock.lock();
                    try {
                        if (line != null && line.isOpen())
                            line.write(buffer, 0, bytesRead);
                    } finally {
                        lineLock.unlock();
                    }
                }
                lineLock.lock();
                try {
                    if (localLine != null && localLine.isOpen())
                        localLine.drain();
                } finally {
                    lineLock.unlock();
                }

                cleanupLine();

                lineLock.lock();
                try {
                    if (stream != null) {
                        stream.close();
                        stream = null;
                    }
                } finally {
                    lineLock.unlock();
                }

            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (running && loop);

        running = false;
        lineReady = false;
    }

    private void cleanupLine() {
        lineLock.lock();
        try {
            if (line != null) {
                try {
                    line.stop();
                } catch (Exception ignored) {
                }
                try {
                    line.close();
                } catch (Exception ignored) {
                }
                line = null;
            }
        } finally {
            lineLock.unlock();
        }
    }

    private void openStream(URL url) throws Exception {
        AudioInputStream original = AudioSystem.getAudioInputStream(url);

        AudioFormat base = original.getFormat();
        AudioFormat decoded = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                base.getSampleRate(),
                16,
                base.getChannels(),
                base.getChannels() * 2,
                base.getSampleRate(),
                false);

        lineLock.lock();
        try {
            stream = AudioSystem.getAudioInputStream(decoded, original);
        } finally {
            lineLock.unlock();
        }
    }

    public void stop() {
        running = false;
        paused = false;
        lineReady = false;

        stopFadeThread();

        synchronized (this) {
            notifyAll();
        }

        cleanupLine();

        lineLock.lock();
        try {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception ignored) {
                }
                stream = null;
            }
        } finally {
            lineLock.unlock();
        }

        if (streamThread != null) {
            streamThread.interrupt();
            try {
                streamThread.join(2000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void pause() {
        if (!running)
            return;
        paused = true;
    }

    public synchronized void resume() {
        if (!running)
            return;
        paused = false;
        notifyAll();
    }

    public void setVolume(double volume) {
        this.volume = volume;
        this.currentVolume = volume;
        applyVolume();
    }

    public double getVolume() {
        return volume;
    }

    public void applyVolume() {
        lineLock.lock();
        try {
            if (line != null && line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);

                float dB;
                if (currentVolume <= 0.0001) {
                    dB = gain.getMinimum();
                } else {
                    dB = (float) (20.0 * Math.log10(currentVolume));
                }
                dB = Math.max(gain.getMinimum(), Math.min(dB, gain.getMaximum()));

                gain.setValue(dB);
            }
        } finally {
            lineLock.unlock();
        }

    }

    public void fadeIn(double targetVolume, int durationMs) {
        stopFadeThread();

        fadeThread = new Thread(() -> {
            while (!lineReady && running) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }

            if (!running)
                return;

            currentVolume = 0.0;
            applyVolume();

            int steps = 50;
            long sleep = Math.max(1, durationMs / steps);

            for (int i = 1; i <= steps && running; i++) {
                double t = i / (double) steps;
                currentVolume = targetVolume * (t * t * (3 - 2 * t)); // smooth step
                applyVolume();

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ignored) {
                    return;
                }
            }

            currentVolume = targetVolume;
            applyVolume();

        }, "fade-in-thread");

        fadeThread.start();
    }

    public void fadeOut(int durationMs) {
        stopFadeThread();

        fadeThread = new Thread(() -> {

            while (!lineReady && running) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    return;
                }
            }
            if (!running)
                return;

            double start = currentVolume;

            int steps = 50;
            long sleep = Math.max(1, durationMs / steps);

            for (int i = 1; i <= steps && running; i++) {
                double t = i / (double) steps;
                currentVolume = start * (1 - (t * t * (3 - 2 * t))); // smooth step
                applyVolume();

                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException ignored) {
                    return;
                }
            }

            currentVolume = 0.0;

            applyVolume();

            running = false;
            synchronized (AudioStreamPlayer.this) {
                notifyAll();
            }

            cleanupLine();

        }, "fade-out-thread");

        fadeThread.start();
    }

    private void stopFadeThread() {
        if (fadeThread != null && fadeThread.isAlive()) {
            fadeThread.interrupt();
        }
    }

    public boolean isRunning() {
        return running;
    }

}
