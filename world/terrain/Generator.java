package world.terrain;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import physics.process.PhysicsHandler;

public class Generator {
    private PhysicsHandler handler;

    private BufferedImage mapImage;

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
                } else {
                    handler.addObject(new Block(x, y, r, g, b, handler.chunkDimension));
                }

            }

    }

}
