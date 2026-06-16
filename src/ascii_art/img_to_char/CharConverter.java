package ascii_art.img_to_char;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Renders characters to a fixed-resolution binary grid used for brightness analysis.
 * Inspired by and partly adapted from the asciimg project (korhner/asciimg).
 */
public class CharConverter {

    private static final double X_OFFSET_FACTOR = 0.2;
    private static final double Y_OFFSET_FACTOR = 0.75;
    private static final String FONT_NAME = "Courier New";
    public static final int DEFAULT_PIXEL_RESOLUTION = 16;

    /**
     * Renders a character to a square black-and-white boolean matrix.
     *
     * @param c the character to render
     * @return a {@code DEFAULT_PIXEL_RESOLUTION} by {@code DEFAULT_PIXEL_RESOLUTION} boolean grid
     */
    public static boolean[][] convertToBoolArray(char c) {
        BufferedImage img = getBufferedImage(c, FONT_NAME, DEFAULT_PIXEL_RESOLUTION);
        boolean[][] matrix = new boolean[DEFAULT_PIXEL_RESOLUTION][DEFAULT_PIXEL_RESOLUTION];
        for (int y = 0; y < DEFAULT_PIXEL_RESOLUTION; y++) {
            for (int x = 0; x < DEFAULT_PIXEL_RESOLUTION; x++) {
                matrix[y][x] = img.getRGB(x, y) == 0;
            }
        }
        return matrix;
    }

    private static BufferedImage getBufferedImage(char c, String fontName, int pixelsPerRow) {
        String charStr = Character.toString(c);
        Font font = new Font(fontName, Font.PLAIN, pixelsPerRow);
        BufferedImage img = new BufferedImage(pixelsPerRow, pixelsPerRow, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setFont(font);
        int xOffset = (int) Math.round(pixelsPerRow * X_OFFSET_FACTOR);
        int yOffset = (int) Math.round(pixelsPerRow * Y_OFFSET_FACTOR);
        g.drawString(charStr, xOffset, yOffset);
        return img;
    }
}
