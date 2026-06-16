package image;

import java.awt.*;

/**
 * Utility class for image processing tasks within the ASCII Art application.
 * This class provides static methods for manipulating images, including
 * padding images to powers of two dimensions, splitting them into sub-images,
 * and calculating their average brightness. It encapsulates the core image
 * manipulation logic required by the ASCII art algorithm.
 * @author salma,mariam
 */
public class ImageProcessor {

    // Implementation comment for a private static final field.
    // Represents a white pixel, used for padding images.
    private static final Color WHITE_PIXEL = Color.WHITE;


    /**
     * Pads an image with white pixels so its dimensions (width and height)
     * become the smallest powers of 2 greater than or equal to the original dimensions. [cite: 232]
     * Padding is symmetrical. [cite: 234]
     * If a dimension is already a power of 2, it is not padded along that dimension. [cite: 235]
     *
     * @param originalImage The image to pad.
     * @return A new, padded Image object. If no padding is needed, the original Image object may be returned.
     */
    public static Image padImage(Image originalImage){
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        int newWidth = nextPowerOfTwo(originalWidth);
        int newHeight = nextPowerOfTwo(originalHeight);

        if (newHeight == originalHeight &&
            newWidth == originalWidth) {
            return originalImage; // No padding needed
        }

        //Create pixel array for the padded image,assuming [height][wight] structure
        Color[][] paddedPixelArray = new Color[newHeight][newWidth];

        //Initialize with white pixels
        for (int row = 0; row < newHeight; row++) {
            for (int col = 0; col < newWidth; col++) {
                paddedPixelArray[row][col] = WHITE_PIXEL;
            }
        }

        // Calculate padding offsets for centering.
        // Symmetrical padding is assumed; original dimensions are even if asymmetry would occur.
        int padTop = (newHeight - originalHeight) / 2;
        int padLeft = (newWidth - originalWidth) / 2;

        // Copy original image into the center of the padded array
        // Image.getPixel(row, col)
        for (int r_orig = 0; r_orig < originalHeight; r_orig++) {
            for (int c_orig = 0; c_orig < originalWidth; c_orig++) {
                paddedPixelArray[padTop + r_orig][padLeft + c_orig] = originalImage.getPixel(r_orig, c_orig);
            }
        }
        return new Image(paddedPixelArray, newWidth, newHeight);
        
    }

    /**
     * Splits a given image (typically a padded image) into a 2D array of square sub-images. [cite: 236, 237]
     * The division is based on the specified resolution, which is the number of sub-images per row. [cite: 237]
     *
     * @param imageToSplit The image to be split (should be padded for dimensions to be divisible).
     * @param resolution   The number of sub-images desired in a row. [cite: 237]
     * @return A 2D array (Image[][]) of sub-images. Each sub-image is square. [cite: 237]
     */
    public static Image[][] splitImage(Image imageToSplit,
                                       int resolution) {
        int paddleWidth = imageToSplit.getWidth();
        int paddleHeight = imageToSplit.getHeight();

        //sub-images are square
        int subImageWidth = paddleWidth / resolution;
        int subImageHeight = subImageWidth; // Square sub-images

        if(subImageWidth == 0){ // Avoid issues if resolution is too high or the image us too small
            return new Image[0][0];
        }

        int numColsSubImages = resolution;
        int numRowsSubImages = paddleHeight / subImageHeight;

        if (numRowsSubImages == 0 || numColsSubImages == 0) {
            return new Image[0][0]; // Not enough pixels to form any sub-images
        }

        Image[][] subImages = new Image[numRowsSubImages][numColsSubImages];

        for (int row_sub = 0; row_sub < numRowsSubImages; row_sub++) {
            for (int col_sub = 0; col_sub < numColsSubImages; col_sub++) {
                // Define the top-left corner of the current sub-image in the padded image
                int startRowInPadded = row_sub * subImageHeight;
                int startColInPadded = col_sub * subImageWidth;

                Color[][] subPixelArray = new Color[subImageHeight][subImageWidth];

                //Copy pixels for te current sub-image
                //imageToSplit.getPixel(row, col)
                for (int r_pix = 0; r_pix < subImageHeight; r_pix++) {
                    for (int c_pix = 0; c_pix < subImageWidth; c_pix++) {
                        subPixelArray[r_pix][c_pix] = imageToSplit.getPixel(
                                startRowInPadded + r_pix,
                                startColInPadded + c_pix);
                    }
                }
                subImages[row_sub][col_sub] = new Image(subPixelArray, subImageWidth, subImageHeight);
            }
        }
        return subImages;
    }

    /**
     * Calculates the average brightness of an image (or sub-image). [cite: 239]
     * Brightness is normalized to a value between 0 (black) and 1 (white).
     * Each pixel is first converted to grayscale. [cite: 240, 241]
     *
     * @param image The image for which to calculate brightness.
     * @return The normalized brightness value (0.0 to 1.0).
     */
    public static double calculateImageBrightness(Image image) {
        double totalGrayScaleValue = 0;
        int numPixels = image.getWidth() * image.getHeight();

        if (numPixels == 0) {
            return 0.0; // Avoid division by zero for an empty image
        }

        // Image.getPixel(row, col)
        for (int r = 0; r < image.getHeight(); r++) {
            for (int c = 0; c < image.getWidth(); c++) {
                Color pixelColor = image.getPixel(r, c);
                // Convert to grayscale using the formula: R*0.2126 + G*0.7152 + B*0.0722 [cite: 241]
                double grayPixel = (pixelColor.getRed() * 0.2126 +
                        pixelColor.getGreen() * 0.7152 +
                        pixelColor.getBlue() * 0.0722);
                totalGrayScaleValue += grayPixel;
            }
        }
        // Normalize by dividing by total pixels and max RGB value (255) [cite: 241]
        return totalGrayScaleValue / (numPixels * 255.0);
    }


    /**
     * Calculates the smallest power of two that is greater than or equal to the given integer.
     * This is a helper method for the image padding functionality.
     * @param n The number to find the next power of two for.
     * @return The smallest power of two that is greater than or equal to n. Returns 1 if n is less than or equal to 0.
     */
    private static int nextPowerOfTwo(int n) {
        if(n <= 0) return 1;
        int power = 1;
        while(power < n) {
            power *= 2;
        }
        return power;
    }
}
