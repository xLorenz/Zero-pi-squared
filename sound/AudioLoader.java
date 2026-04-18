package sound;

import java.util.List;

public class AudioLoader {
    public static void loadAudioFiles() {
        AudioAssetCache.loadClip("jump", "assets/sounds/jump.wav");
        AudioAssetCache.loadClip("double_jump", "assets/sounds/double_jump.wav");
        AudioAssetCache.loadClip("explosion", "assets/sounds/explosion.wav");
        AudioAssetCache.loadClip("bass", "assets/sounds/bass.wav");

        AudioAssetCache.loadClip("bullet_0", "assets/sounds/bullet_0.wav");
        AudioAssetCache.loadClip("bullet_1", "assets/sounds/bullet_1.wav");
        AudioAssetCache.loadClip("bullet_2", "assets/sounds/bullet_2.wav");

        AudioAssetCache.loadBundledIds("bullet", List.of("bullet_0", "bullet_1", "bullet_2"));

        AudioAssetCache.loadMusic("menu_music_0", "assets/music/menu_music_0.wav");
        AudioAssetCache.loadMusic("menu_music_1", "assets/music/menu_music_1.wav");
    }
}
