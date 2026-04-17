package sound.loaders;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class SfxChannel {

    private Clip clip;
    private boolean busy;

    private int pausedFrame = 0;
    private boolean wasPlaying = false;

    double volume = 1.0;

    public void play() {
        stop();
        clip.setFramePosition(0);
        busy = true;
        clip.start();

        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) {
                clip.close();
                busy = false;
                wasPlaying = false;
            }
        });

    }

    public void applyVolume(double volume) {
        this.volume = volume;
        if (clip != null)
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

                float db = (float) (20.0 * Math.log10(Math.max(volume, 0.0001)));

                gain.setValue(db);
            }
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }

    public boolean isBusy() {
        return busy;
    }

    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
            clip.close();
        }
        busy = false;
        wasPlaying = false;
    }

    public void pause() {
        if (clip.isRunning()) {
            pausedFrame = clip.getFramePosition();
            clip.stop();
            wasPlaying = true;
        } else {
            wasPlaying = false;
        }
    }

    public void resume() {
        if (wasPlaying) {
            clip.setFramePosition(pausedFrame);
            clip.start();
        }
    }

}
