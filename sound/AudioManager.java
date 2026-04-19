package sound;

import sound.players.MusicPlayer;
import sound.players.SfxPlayer;

public class AudioManager {

    private static SfxPlayer sfxPlayer = new SfxPlayer(16);
    private static MusicPlayer musicPlayer = new MusicPlayer();

    private static double masterVolume = 1.0;
    private static double sfxVolume = 1.0;
    private static double musicVolume = 1.0;

    private static boolean paused = false;

    public AudioManager() {
    }

    public static void playSfx(String id) {
        sfxPlayer.play(id);
    }

    public static void playMusic(String id, boolean loop) {
        musicPlayer.play(id, loop);
    }

    public static void playMusic(String id, boolean loop, int fadeInMs) {
        musicPlayer.play(id, loop, fadeInMs);
    }

    public static void playMusic(String id, boolean loop, int fadeInMs, int fadeOutCurrentMs) {
        musicPlayer.play(id, loop, fadeInMs, fadeOutCurrentMs);
    }

    public static void stopMusic() {
        musicPlayer.stop();
    }

    public static void stopMusic(int fadeOutMs) {
        musicPlayer.stop(fadeOutMs);
    }

    public static void pauseAll() {
        if (!paused) {
            sfxPlayer.pause();
            musicPlayer.pause();
            paused = true;
        }
    }

    public static void resumeAll() {
        if (paused) {
            sfxPlayer.resume();
            musicPlayer.resume();
            paused = false;
        }
    }

    public static void setVolumes(double masterVolume, double sfxVolume, double musicVolume) {
        setMasterVolume(masterVolume);
        setSfxVolume(sfxVolume);
        setMusicVolume(musicVolume);
    }

    private static void updateVolumes() {
        sfxPlayer.setVolume(sfxVolume * masterVolume);
        musicPlayer.setVolume(musicVolume * masterVolume);
    }

    public static void setMasterVolume(double masterVolume) {
        AudioManager.masterVolume = masterVolume;
        updateVolumes();
    }

    public double getMasterVolume() {
        return masterVolume;
    }

    public static void setSfxVolume(double sfxVolume) {
        AudioManager.sfxVolume = sfxVolume;
        updateVolumes();
    }

    public static double getSfxVolume() {
        return sfxVolume;
    }

    public static void setMusicVolume(double musicVolume) {
        AudioManager.musicVolume = musicVolume;
        updateVolumes();
    }

    public static double getMusicVolume() {
        return musicVolume;
    }

    public static void mute() {
        sfxPlayer.setVolume(0);
        musicPlayer.setVolume(0);
    }

    public static void unmute() {
        updateVolumes();
    }

}
