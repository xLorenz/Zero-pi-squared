package sound;

import java.util.List;

public class AudioLoader {

    public class Clips {
        public static String JUMP = registerClip("jump", "assets/sounds/jump.wav");
        public static String DOUBLE_JUMP = registerClip("double_jump", "assets/sounds/double_jump.wav");
        public static String EXPLOSION = registerClip("explosion", "assets/sounds/explosion.wav");
        public static String BASS = registerClip("bass", "assets/sounds/bass.wav");
        public static String BULLET_0 = registerClip("bullet_0", "assets/sounds/bullet_0.wav");
        public static String BULLET_1 = registerClip("bullet_1", "assets/sounds/bullet_1.wav");
        public static String BULLET_2 = registerClip("bullet_2", "assets/sounds/bullet_2.wav");
    }

    public class BundledIds {
        public static String BULLET = registerBundledIds("bullet", List.of(Clips.BULLET_0, Clips.BULLET_1, Clips.BULLET_2));
    }

    public class Music {
        
        public static String MENU_MUSIC_0 = registerMusic("menu_music_0", "assets/music/menu_music_0.wav");
        public static String MENU_MUSIC_1 = registerMusic("menu_music_1", "assets/music/menu_music_1.wav");
    }


    private static String registerClip(String id, String path) {
        AudioAssetCache.loadClip(id, path);
        return id;
    }

    private static String registerMusic(String id, String path) {
        AudioAssetCache.loadMusic(id, path);
        return id;
    }
    private static String registerBundledIds(String id, List<String> list) {
        AudioAssetCache.loadBundledIds(id, list);
        return id;
    }
}
