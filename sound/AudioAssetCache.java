package sound;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Loads audio files into playable data
 */
public class AudioAssetCache {
    private static Random rand = new Random();
    private static Map<String, byte[]> audioData = new HashMap<>();
    private static Map<String, AudioFormat> formats = new HashMap<>();
    private static Map<String, URL> musicTracks = new HashMap<>();
    private static Map<String, List<String>> bundledIds = new HashMap<>();

    public static void loadMusic(String id, String path) {
        if (musicTracks.containsKey(id)) {
            return;
        } else {
            URL url = AudioAssetCache.class.getResource(path);
            if (url == null) {
                throw new IllegalArgumentException("Music file not found: " + path);
            }

            musicTracks.put(id, url);
        }
    }

    public static URL getMusic(String id) {
        URL url = musicTracks.get(id);

        if (url == null) {
            throw new IllegalArgumentException("Music not loaded: " + id);
        }
        return url;
    }

    /** Assings an id to a set of data originated from the path */
    public static void loadClip(String id, String path) {
        if (audioData.containsKey(id)) {
            return;
        }
        try {
            // load resource
            URL url = AudioAssetCache.class.getResource(path);
            if (url == null) {
                throw new IllegalArgumentException("Audio file not found: " + path);
            }

            AudioInputStream originalStream = AudioSystem.getAudioInputStream(url);

            // ensure format compatibility
            AudioFormat baseFormat = originalStream.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    2, // force stereo for panning
                    2 * 2, // force stereo for panning
                    baseFormat.getSampleRate(),
                    false);

            AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, originalStream);

            // create clip and load to memory
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] temp = new byte[4096];
            int bytesRead;

            while ((bytesRead = decodedStream.read(temp)) != -1) {
                buffer.write(temp, 0, bytesRead);
            }

            audioData.put(id, buffer.toByteArray());
            formats.put(id, decodedFormat);

            // close streams
            originalStream.close();
            decodedStream.close();

        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException("Unsupported audio format: " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load audio: " + path, e);
        }
    }

    /** Assings an id to a list of ids */
    public static void loadBundledIds(String id, List<String> listOfIds) {
        if (bundledIds.containsKey(id))
            return;
        for (String i : listOfIds) {
            if (!audioData.containsKey(i)) {
                throw new IllegalArgumentException("Clip not loaded: " + i);
            }
        }
        bundledIds.put(id, listOfIds);
    }

    public static Clip getClip(String id) {

        if (bundledIds.containsKey(id)) {
            ArrayList<String> list = new ArrayList<>(bundledIds.get(id));
            id = list.get(rand.nextInt(list.size()));
        }

        byte[] data = audioData.get(id);
        AudioFormat format = formats.get(id);

        if (data == null || format == null) {
            throw new IllegalArgumentException("Clip not loaded: " + id);
        }
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, data.length);
            return clip;

        } catch (LineUnavailableException e) {
            throw new RuntimeException("Failed to create clip: " + id);
        }
    }
}
