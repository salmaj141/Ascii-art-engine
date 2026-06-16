package ascii_art;

import image.Image;
import image.ImageProcessor;
import ascii_art.img_to_char.SubImgCharMatcher;

/**
 * Implements the ASCII Art generation algorithm. [cite: 242]
 * Responsible for a single run of the algorithm. For each run, a new instance of this class
 * should be created. [cite: 243]
 * Parameters for the run (image, resolution, character set via SubImgCharMatcher)
 * are provided via the constructor. [cite: 244]
 */
public class AsciiArtAlgorithm {

    private final Image originalImage;
    private final int resolution;
    private final SubImgCharMatcher charMatcher;
    private final String roundingMode;


    // Cache for sub-image brightness values to optimize repeated runs
    // with the same image and resolution, as per section 1.5.1. [cite: 250]
    // Stores results from only one previous run. [cite: 251]
    private static Image lastCachedOriginalImage = null;
    private static int lastCachedResolution = -1;
    private static double[][] cachedSubImageBrightnesses = null;

    /**
     * Constructor for the AsciiArtAlgorithm.
     *
     * @param image        The image to convert.
     * @param resolution   The number of characters per row in the output ASCII art. [cite: 244]
     * @param charMatcher  The SubImgCharMatcher instance configured with the desired character set.
     */
    public AsciiArtAlgorithm(Image image, int resolution,
                             SubImgCharMatcher charMatcher, String roundingMode
                             ) {
        this.originalImage = image;
        this.resolution = resolution;
        this.charMatcher = charMatcher;
        this.roundingMode = roundingMode;
    }

    /**
     * Runs the ASCII art generation algorithm.
     * Returns a 2D array of characters representing the ASCII art. [cite: 246]
     * Each sub-image is replaced by the character with the brightness closest
     * in absolute value by default. [cite: 247]
     *
     * @return A 2D char array representing the generated ASCII art.
     */
    public char[][] run() {
        // Step 1: Pad the image (as per algorithm step 1.1.2) [cite: 188]
        Image paddedImage = ImageProcessor.padImage(this.originalImage);

        // Basic validation for resolution and calculate sub-image size.
        // Section 1.4.2.2 states we can assume resolution is valid, but subImageSize can still be 0.
        if (this.resolution <= 0) {
            return new char[0][0]; // Resolution must be positive to make sense.
        }
        int subImageSize = paddedImage.getWidth() / this.resolution;

        if (subImageSize == 0) {
            // This occurs if resolution is too high for the padded image's width,
            // resulting in zero-width/height sub-images. No art can be generated.
            return new char[0][0];
        }

        int numSubImageRows = paddedImage.getHeight() / subImageSize;
        int numSubImageCols = this.resolution; // As per definition of resolution [cite: 190]

        if (numSubImageRows == 0 || numSubImageCols == 0) {
            // Not enough dimensions in the padded image to form even one row/column of sub-images.
            return new char[0][0];
        }

        double[][] subImageBrightnesses;

        // Step 2: Check cache for sub-image brightnesses (efficiency measure from 1.5.1) [cite: 250]
        // Compare originalImage by instance equality (==) for the "one run back" cache.
        boolean useCache = this.originalImage == lastCachedOriginalImage &&
                this.resolution == lastCachedResolution &&
                cachedSubImageBrightnesses != null &&
                cachedSubImageBrightnesses.length == numSubImageRows &&
                // Ensure column count also matches, handling empty row case
                (numSubImageRows == 0 || (cachedSubImageBrightnesses.length > 0 &&
                        cachedSubImageBrightnesses[0].length == numSubImageCols));

        if (useCache) {
            subImageBrightnesses = cachedSubImageBrightnesses;
        } else {
            // Cache miss or invalid: Calculate brightnesses.
            // Step 2 (cont.): Divide padded image into sub-images (algorithm step 1.1.3) [cite: 189]
            // And Step 3 (cont.): Calculate brightness for each sub-image (algorithm step 1.1.4) [cite: 191]

            subImageBrightnesses = new double[numSubImageRows][numSubImageCols];
            Image[][] subImages = ImageProcessor.splitImage(paddedImage, this.resolution);

            // The dimensions of subImages array should match numSubImageRows and numSubImageCols
            // if ImageProcessor.splitImage works as expected and preceding checks are correct.
            for (int r = 0; r < numSubImageRows; r++) {
                for (int c = 0; c < numSubImageCols; c++) {
                    if (subImages[r][c] != null) {
                        subImageBrightnesses[r][c] = ImageProcessor.calculateImageBrightness(subImages[r][c]);
                    } else {
                        // This case should ideally not be reached if splitImage is robust
                        // and calculations for numSubImageRows/Cols are correct.
                        // Assign a default brightness (e.g., black) or handle as an error.
                        subImageBrightnesses[r][c] = 0.0; // Default to black's brightness
                    }
                }
            }

            // Update cache for the next potential run [cite: 251]
            lastCachedOriginalImage = this.originalImage;
            lastCachedResolution = this.resolution;
            cachedSubImageBrightnesses = subImageBrightnesses;
        }

        // Step 3 (final part): Convert sub-image brightnesses to ASCII characters [cite: 192]
        char[][] asciiArt = new char[numSubImageRows][numSubImageCols];
        for (int r = 0; r < numSubImageRows; r++) {
            for (int c = 0; c < numSubImageCols; c++) {
                double brightness = subImageBrightnesses[r][c];
                // SubImgCharMatcher.getCharByImageBrightness handles the "closest in absolute value" logic [cite: 247]
                switch (roundingMode) {
                    case "up":
                        asciiArt[r][c] = this.charMatcher.getCharByImageBrightnessUp(brightness);
                        break;
                    case "down":
                        asciiArt[r][c] = this.charMatcher.getCharByImageBrightnessDown(brightness);
                        break;
                    case "abs":
                    default:
                        asciiArt[r][c] = this.charMatcher.getCharByImageBrightness(brightness);
                        break;
                }            }
        }
        return asciiArt;
    }
}