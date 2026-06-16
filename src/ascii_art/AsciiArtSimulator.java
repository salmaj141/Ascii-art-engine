package ascii_art;

import java.io.IOException;

/**
 * Application entry point for the ASCII Art simulator.
 * Delegates all runtime behavior to the interactive {@link Shell}.
 */
public class AsciiArtSimulator {

    /**
     * Launches the ASCII Art interactive shell.
     *
     * @param args command-line arguments; expects exactly one image path
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ascii_art.AsciiArtSimulator <image-path>");
            return;
        }

        try {
            Shell shell = new Shell(args[0]);
            shell.run();
        } catch (IOException e) {
            System.out.println("Error: Failed to load image. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }
}
