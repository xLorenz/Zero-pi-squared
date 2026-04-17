package sound;

import java.util.List;

public class AudioLoader {
    public static void loadAudioFiles() {
        AudioAssetCache.loadClip("jump", "sounds/jump.wav");
        AudioAssetCache.loadClip("double_jump", "sounds/double_jump.wav");
        AudioAssetCache.loadClip("explosion", "sounds/explosion.wav");
        AudioAssetCache.loadClip("bass", "sounds/bass.wav");

        AudioAssetCache.loadClip("bullet_0", "sounds/bullet_0.wav");
        AudioAssetCache.loadClip("bullet_1", "sounds/bullet_1.wav");
        AudioAssetCache.loadClip("bullet_2", "sounds/bullet_2.wav");

        AudioAssetCache.loadBundledIds("bullet", List.of("bullet_0", "bullet_1", "bullet_2"));
    }
}
