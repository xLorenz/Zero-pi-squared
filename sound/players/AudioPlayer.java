package sound.players;

public abstract class AudioPlayer {
    protected double volume = 1.0;

    abstract void setVolume(double volume);

    abstract void stop();

    abstract void pause();

    abstract void resume();
}
