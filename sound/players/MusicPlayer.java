package sound.players;

import java.net.URL;

import sound.AudioAssetCache;
import sound.loaders.AudioStreamPlayer;

public class MusicPlayer extends AudioPlayer {

    private AudioStreamPlayer streamPlayer = new AudioStreamPlayer();

    private String currentId;

    private boolean loop = false;
    private boolean paused = false;

    private Thread crossfadeThread;
    private AudioStreamPlayer pendingOutgoing = null;

    private MusicEventListener musicEventListener = null;

    public MusicPlayer() {
    }

    public void setMusicEventListener(MusicEventListener musicEventListener) {
        this.musicEventListener = musicEventListener;
    }

    public void play(String id, boolean loop) {
        // stop current music
        stopCrossfade();
        stop();

        URL url = AudioAssetCache.getMusic(id);
        if (url == null) {
            System.err.println("MusicPlayer: unknown track id: " + id);
            return;
        }

        this.currentId = id;
        this.loop = loop;
        this.paused = false;

        streamPlayer.setVolume(volume);
        streamPlayer.play(url, loop);

        if(musicEventListener != null)
            musicEventListener.onMusicStart(id);
    }

    public void play(String id, boolean loop, int fadeMs) {
        play(id, loop);
        streamPlayer.fadeIn(this.volume, fadeMs);
    }

    public void play(String id, boolean loop, int fadeInMs, int fadeOutCurrentMs) {
        stopCrossfade();

        URL url = AudioAssetCache.getMusic(id);
        if (url == null) {
            System.err.println("MusicPlayer: unknown track id: " + id);
            return;
        }

        AudioStreamPlayer outgoing = this.streamPlayer;
        double targetVolume = this.volume;

        AudioStreamPlayer incoming = new AudioStreamPlayer();
        incoming.setVolume(0.0);
        incoming.play(url, loop);

        this.streamPlayer = incoming;
        this.currentId = id;
        this.loop = loop;
        this.paused = false;

        pendingOutgoing = outgoing;

        crossfadeThread = new Thread(() -> {

            outgoing.fadeOut(fadeOutCurrentMs);
            incoming.fadeIn(targetVolume, fadeOutCurrentMs);

            try {
                Thread.sleep(fadeOutCurrentMs + 200);
            } catch (InterruptedException ignored) {
            }

            outgoing.stop();
            pendingOutgoing = null;
        }, "music-crossfade-out");
        crossfadeThread.setDaemon(true);
        crossfadeThread.start();

        if(musicEventListener != null)
            musicEventListener.onMusicStart(id);

    }

    @Override
    public void stop() {
        streamPlayer.stop();

        if(musicEventListener != null)
            musicEventListener.onMusicEnd(currentId);

        currentId = null;
        paused = false;
    }

    public void stop(int fadeMS) {
        if (!streamPlayer.isRunning())
            return;

        stopCrossfade();

        streamPlayer.fadeOut(fadeMS);
        if(musicEventListener != null)
            musicEventListener.onMusicEnd(currentId);

        Thread cleanup = new Thread(() -> {
            try {
                Thread.sleep(fadeMS + 100);
            } catch (InterruptedException ignored) {
            }
            currentId = null;
            paused = false;
        }, "music-fadeout-cleanup");

        cleanup.setDaemon(true);
        cleanup.start();
    }

    @Override
    public void pause() {
        if (streamPlayer.isRunning() && !paused) {
            streamPlayer.pause();
            paused = true;
        }
    }

    @Override
    public void resume() {
        if (paused) {
            streamPlayer.resume();
            paused = false;
        }
    }

    @Override
    public void setVolume(double volume) {
        this.volume = volume;
        streamPlayer.setVolume(volume);
    }

    public boolean isPlaying() {
        return streamPlayer.isRunning() && !paused;
    }

    public boolean isPaused() {
        return paused;
    }

    public String getCurrentTrack() {
        return currentId;
    }

    public boolean isLooping() {
        return loop;
    }

    private void stopCrossfade() {
        if (crossfadeThread != null && crossfadeThread.isAlive()) {
            crossfadeThread.interrupt();
        }

        if (pendingOutgoing != null) {
            pendingOutgoing.stop();
            pendingOutgoing = null;
        }
    }

}
