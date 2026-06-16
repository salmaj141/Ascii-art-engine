//// Import necessary classes
//import ascii_art.AsciiArtAlgorithm;
//import image.Image;
//import image_char_matching.SubImgCharMatcher; // Using the SubImgCharMatcher you provided
//// CharConverter is used by SubImgCharMatcher internally, no direct import needed here.
//// ImageProcessor is used by AsciiArtAlgorithm internally.
//
//import java.io.IOException;
//import java.util.Arrays;
//
//public class AsciiArtSanityCheck {
//
//    public static void main(String[] args) {
//        System.out.println("Starting ASCII Art Sanity Check...");
//
//        Image image = null;
//        try {
//            // Load the image [cite: 93]
//            // Make sure "board.jpeg" is in your project's root directory or provide the full path.
//            image = new Image("board.jpeg");
//            System.out.println("Image 'board.jpeg' loaded successfully.");
//            System.out.println("Image dimensions: " + image.getWidth() + "x" + image.getHeight());
//        } catch (IOException e) {
//            System.err.println("Error loading image 'board.jpeg': " + e.getMessage());
//            e.printStackTrace();
//            return; // Cannot proceed without the image
//        }
//
//        // Define the character set [cite: 93]
//        char[] charset = {'o', 'm'};
//        System.out.println("Character set: " + Arrays.toString(charset));
//
//        // Create SubImgCharMatcher instance
//        SubImgCharMatcher charMatcher = new SubImgCharMatcher(charset);
//        System.out.println("SubImgCharMatcher initialized.");
//
//        // Define the resolution [cite: 93]
//        int resolution = 2; // 2 characters per row
//        System.out.println("Resolution: " + resolution);
//
//        // Create AsciiArtAlgorithm instance
//        AsciiArtAlgorithm algorithm = new AsciiArtAlgorithm(image, resolution, charMatcher);
//        System.out.println("AsciiArtAlgorithm initialized.");
//
//        // Run the algorithm
//        System.out.println("Running the algorithm...");
//        char[][] asciiArtResult = algorithm.run();
//
//        // Print the result
//        System.out.println("Algorithm finished. Output:");
//        if (asciiArtResult == null) {
//            System.out.println("Result is null.");
//        } else {
//            // Using Arrays.deepToString for a readable representation of the 2D array
//            System.out.println(Arrays.deepToString(asciiArtResult));
//
//            // For a more visual console output:
//            for (int i = 0; i < asciiArtResult.length; i++) {
//                for (int j = 0; j < asciiArtResult[i].length; j++) {
//                    System.out.print(asciiArtResult[i][j] + " ");
//                }
//                System.out.println();
//            }
//        }
//
//        // Expected output check [cite: 93]
//        char[][] expectedOutput = {{'m', 'o'}, {'o', 'm'}};
//        if (Arrays.deepEquals(asciiArtResult, expectedOutput)) {
//            System.out.println("\nSanity check PASSED! Output matches the expected output.");
//        } else {
//            System.out.println("\nSanity check FAILED. Output does not match the expected output.");
//            System.out.println("Expected: " + Arrays.deepToString(expectedOutput));
//        }
//    }
//}
