import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
public class RW {
    public int[][] readImage(String originalImgPath) {
        File file = new File(originalImgPath);
        BufferedImage image;
        int width, height;
        try {
            image = ImageIO.read(file);
            width = image.getWidth();
            height = image.getHeight();
            int[][] pixels = new int[height][width];
            int rgb;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    rgb = image.getRGB(x, y);
                    pixels[y][x] = (rgb>>16) & 0xff;
                }
            }
            return pixels;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeImage(int[][] pixels,String compressedImgPath) {
        File fileout = new File(compressedImgPath);
        int height = pixels.length;
        int width = pixels[0].length;
        BufferedImage image2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image2.setRGB(x, y, ((pixels[y][x]<<16)|(pixels[y][x])|(pixels[y][x]<<8)));
            }
        }
        try {
            ImageIO.write(image2, "jpg", fileout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}