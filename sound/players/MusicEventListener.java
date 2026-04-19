package sound.players;

public abstract class MusicEventListener {
    abstract void onMusicStart(String id);
    abstract void onMusicEnd(String id);
}
