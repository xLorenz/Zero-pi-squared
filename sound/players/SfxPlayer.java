package sound.players;

import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.Clip;

import sound.AudioAssetCache;
import sound.loaders.SfxChannel;

public class SfxPlayer extends AudioPlayer {

    private final List<SfxChannel> channels;
    int maxChannels = 16;

    public SfxPlayer(int maxChannels) {
        this.maxChannels = maxChannels;

        ArrayList<SfxChannel> list = new ArrayList<>();
        for (int i = 0; i < maxChannels; i++) {
            list.add(new SfxChannel());
        }
        channels = List.copyOf(list);
    }

    public void play(String id) {
        // get free channel
        SfxChannel channel = findFreeChannel();

        // if none, fallback
        if (channel == null) {
            channel = stealChannel();
            if (channel == null) {
                return;
            }
        }

        // get audio data
        Clip clip = AudioAssetCache.getClip(id);
        if (clip == null)
            return;

        // assign to channel
        channel.setClip(clip);
        channel.applyVolume(volume);
        channel.play();

    }

    public void play(String id, double volumeMultiplier) {
        // get free channel
        SfxChannel channel = findFreeChannel();

        // if none, fallback
        if (channel == null) {
            channel = stealChannel();
            if (channel == null) {
                return;
            }
        }

        // get audio data
        Clip clip = AudioAssetCache.getClip(id);
        if (clip == null)
            return;

        // assign to channel
        channel.setClip(clip);
        channel.applyVolume(volume * volumeMultiplier);
        channel.play();

    }

    public void play(String id, double volumeMultiplier, double pan) {
        // get free channel
        SfxChannel channel = findFreeChannel();

        // if none, fallback
        if (channel == null) {
            channel = stealChannel();
            if (channel == null) {
                return;
            }
        }

        // get audio data
        Clip clip = AudioAssetCache.getClip(id);
        if (clip == null)
            return;

        // assign to channel
        channel.setClip(clip);
        channel.applyVolume(volume * volumeMultiplier);
        channel.applyPan(pan);
        channel.play();

    }

    private SfxChannel stealChannel() {
        for (SfxChannel c : channels) {
            if (c.isBusy()) {
                c.stop();
                return c;
            }
        }
        return null;
    }

    private SfxChannel findFreeChannel() {
        for (SfxChannel c : channels) {
            if (!c.isBusy()) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void stop() {
        for (SfxChannel c : channels) {
            c.stop();
        }
    }

    @Override
    public void pause() {
        for (SfxChannel c : channels) {
            c.pause();
        }
    }

    @Override
    public void resume() {
        for (SfxChannel c : channels) {
            c.resume();
        }
    }

    @Override
    public void setVolume(double volume) {
        this.volume = volume;
        updateVolumes();
    }

    private void updateVolumes() {

        for (SfxChannel c : channels) {
            c.applyVolume(volume);
        }
    }

}
