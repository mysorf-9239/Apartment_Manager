package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class ImageLoader {

    public static BufferedImage loadImage(String path, int width, int height) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(Objects.requireNonNull(ImageLoader.class.getResourceAsStream(path)));
            // Resize the image to the desired dimensions
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
            return resizedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
