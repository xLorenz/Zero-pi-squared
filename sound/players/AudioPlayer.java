package sound.players;

public abstract class AudioPlayer {
    protected double volume = 1.0;
    protected boolean muted = false;

    abstract void setVolume(double volume);

    public void mute() {
        muted = true;
    }

    public void unmute() {
        muted = false;
    }

    abstract void stop();

    abstract void pause();

    abstract void resume();
}
