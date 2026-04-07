package world.terrain;

import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import physics.process.PhysicsHandler;
import world.terrain.folliage.Bush;

public class Generator {
    private PhysicsHandler handler;

    private BufferedImage mapImage;

    private Map<Long, Block> generatedBlocks = new HashMap<>();

    private long key(int x, int y) {
        return (((long) x) << 32) | (y & 0xffffffffL);
    }

    public Generator(PhysicsHandler handler) {
        this.handler = handler;
        Block.handler = handler;
    }

    public void loadMapImage(String filePath) {
        try {
            mapImage = ImageIO.read(new File(filePath));
            // for when packagin in jar
            // mapImage = ImageIO.read(getClass().getResource(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateMap() {
        if (mapImage == null)
            return;

        int width = mapImage.getWidth();
        int height = mapImage.getHeight();

        // iterate every pixel
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int pixel = mapImage.getRGB(x, y); // 32b int

                // int a = (pixel >> 24) & 0xff;
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = (pixel) & 0xff;

                if (r + g + b == 0) {
                    // black pixels
                } else if (r == g && g == b && b == 255) {
                    // white pixels
                } else if (r == 0 && g == 255 && b == 0) {
                    // green pixels
                    Block block = new Bush(x, y, r, g, b, handler.chunkDimension);
                    addBlock(block, x, y);
                } else {
                    Block block = new Block(x, y, r, g, b, handler.chunkDimension);
                    addBlock(block, x, y);
                }

            }

    }

    public void addBlock(Block block, int x, int y) {
        // check folliage
        if (checkColorAbove(x, y, 0, 255, 0))
            block.setFolliage(getBlockAbove(x, y));
        handler.addObject(block);
        generatedBlocks.put(key(x, y), block);
    }

    public Color checkAbove(int x, int y) {
        return new Color(mapImage.getRGB(x, y - 1));
    }

    public boolean checkColorAbove(int x, int y, int r, int g, int b) {
        int pixel = mapImage.getRGB(x, y - 1); // 32b int

        // int a = (pixel >> 24) & 0xff;
        int r0 = (pixel >> 16) & 0xff;
        int g0 = (pixel >> 8) & 0xff;
        int b0 = (pixel) & 0xff;

        return r0 == r && g0 == g && b0 == b;
    }

    public Block getBlockAtTile(int x, int y) {
        return generatedBlocks.get(key(x, y));
    }

    public Block getBlockAbove(int x, int y) {
        return generatedBlocks.get(key(x, y - 1));
    }

}
