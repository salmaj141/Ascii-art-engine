package ascii_art;

import ascii_art.img_to_char.SubImgCharMatcher;
import ascii_art.output.AsciiOutput;
import ascii_art.output.ConsoleAsciiOutput;
import ascii_art.output.HtmlAsciiOutput;
import image.Image;

import java.io.IOException;

/**
 * Interactive command-line controller for the ASCII Art application.
 * Parses user commands, maintains runtime configuration (resolution, charset,
 * output mode, rounding strategy), and delegates rendering to the algorithm layer.
 */
public class Shell {

    private static final int DEFAULT_RES = 2;
    private static final String DEFAULT_ROUND = "abs";
    private static final String DEFAULT_OUTPUT = "console";
    private static final char[] DEFAULT_CHARS = "0123456789".toCharArray();
    private static final int MIN_PRINTABLE_ASCII = 32;
    private static final int MAX_PRINTABLE_ASCII = 126;

    private final Image image;
    private final SubImgCharMatcher matcher;
    private int resolution;
    private String outputMode;
    private String roundingMethod;

    /**
     * Creates a shell bound to the image at the given path.
     *
     * @param imagePath path to the source image file
     * @throws IOException if the image cannot be loaded
     */
    public Shell(String imagePath) throws IOException {
        this.image = new Image(imagePath);
        this.resolution = DEFAULT_RES;
        this.outputMode = DEFAULT_OUTPUT;
        this.roundingMethod = DEFAULT_ROUND;
        this.matcher = new SubImgCharMatcher(DEFAULT_CHARS);
    }

    /**
     * Starts the interactive command loop until the user issues {@code exit}.
     */
    public void run() {
        System.out.println("Welcome to ASCII Art!");

        while (true) {
            try {
                System.out.print(">>> ");
                String line = KeyboardInput.readLine();
                if (line == null || line.isEmpty()) {
                    continue;
                }

                String[] tokens = line.trim().split("\\s+");
                if (tokens.length == 0 || tokens[0].isEmpty()) {
                    continue;
                }

                switch (tokens[0]) {
                    case "exit":
                        return;
                    case "chars":
                        printChars();
                        break;
                    case "add":
                        handleAdd(tokens);
                        break;
                    case "remove":
                        handleRemove(tokens);
                        break;
                    case "res":
                        handleRes(tokens);
                        break;
                    case "output":
                        handleOutput(tokens);
                        break;
                    case "round":
                        handleRound(tokens);
                        break;
                    case "render":
                        handleRender();
                        break;
                    default:
                        System.out.println("Did not execute due to incorrect command.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    private void printChars() {
        if (matcher.charsetSize() == 0) {
            return;
        }
        for (char c : matcher.getCharset()) {
            System.out.print(c + " ");
        }
        System.out.println();
    }

    private void handleAdd(String[] tokens) {
        if (tokens.length < 2) {
            throw new IllegalArgumentException("Did not add due to incorrect format.");
        }
        applyCharsetChange(tokens[1], true);
    }

    private void handleRemove(String[] tokens) {
        if (tokens.length < 2) {
            throw new IllegalArgumentException("Did not remove due to incorrect format.");
        }
        applyCharsetChange(tokens[1], false);
    }

    private void applyCharsetChange(String param, boolean add) {
        if (param.equals("all")) {
            if (add) {
                for (char c = MIN_PRINTABLE_ASCII; c <= MAX_PRINTABLE_ASCII; c++) {
                    matcher.addChar(c);
                }
            } else {
                for (char c = MIN_PRINTABLE_ASCII; c <= MAX_PRINTABLE_ASCII; c++) {
                    matcher.removeChar(c);
                }
            }
            return;
        }

        if (param.equals("space")) {
            if (add) {
                matcher.addChar(' ');
            } else {
                matcher.removeChar(' ');
            }
            return;
        }

        if (param.length() == 1) {
            char c = param.charAt(0);
            validatePrintableAscii(c, add);
            if (add) {
                matcher.addChar(c);
            } else {
                matcher.removeChar(c);
            }
            return;
        }

        if (param.length() == 3 && param.charAt(1) == '-') {
            char start = param.charAt(0);
            char end = param.charAt(2);
            if (start > end) {
                char temp = start;
                start = end;
                end = temp;
            }
            for (char c = start; c <= end; c++) {
                validatePrintableAscii(c, add);
                if (add) {
                    matcher.addChar(c);
                } else {
                    matcher.removeChar(c);
                }
            }
            return;
        }

        throw new IllegalArgumentException(
                add ? "Did not add due to incorrect format." : "Did not remove due to incorrect format.");
    }

    private static void validatePrintableAscii(char c, boolean add) {
        if (c < MIN_PRINTABLE_ASCII || c > MAX_PRINTABLE_ASCII) {
            throw new IllegalArgumentException(
                    add ? "Did not add due to incorrect format." : "Did not remove due to incorrect format.");
        }
    }

    private void handleRes(String[] tokens) {
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        int minRes = Math.max(1, imgWidth / imgHeight);

        if (tokens.length == 1) {
            System.out.println("Resolution set to " + resolution + ".");
            return;
        }

        if (tokens[1].equals("up")) {
            if (resolution * 2 <= imgWidth) {
                resolution *= 2;
                System.out.println("Resolution set to " + resolution + ".");
            } else {
                System.out.println("Did not change resolution due to exceeding boundaries.");
            }
            return;
        }

        if (tokens[1].equals("down")) {
            if (resolution / 2 >= minRes) {
                resolution /= 2;
                System.out.println("Resolution set to " + resolution + ".");
            } else {
                System.out.println("Did not change resolution due to exceeding boundaries.");
            }
            return;
        }

        throw new IllegalArgumentException("Did not change resolution due to incorrect format.");
    }

    private void handleOutput(String[] tokens) {
        if (tokens.length != 2
                || !(tokens[1].equals("console") || tokens[1].equals("html"))) {
            throw new IllegalArgumentException("Did not change output method due to incorrect format.");
        }
        outputMode = tokens[1];
    }

    private void handleRound(String[] tokens) {
        if (tokens.length != 2
                || !(tokens[1].equals("abs") || tokens[1].equals("up") || tokens[1].equals("down"))) {
            throw new IllegalArgumentException("Did not change rounding method due to incorrect format.");
        }
        roundingMethod = tokens[1];
    }

    private void handleRender() {
        if (matcher.charsetSize() < 2) {
            System.out.println("Did not execute. Charset is too small.");
            return;
        }

        try {
            AsciiArtAlgorithm algorithm = new AsciiArtAlgorithm(
                    image, resolution, matcher, roundingMethod);
            char[][] result = algorithm.run();
            selectOutputStrategy().out(result);

            if ("html".equals(outputMode)) {
                System.out.println("Output written to out.html");
            }
        } catch (Exception e) {
            System.out.println("Failed to generate ASCII art: " + e.getMessage());
        }
    }

    private AsciiOutput selectOutputStrategy() {
        if ("html".equals(outputMode)) {
            return new HtmlAsciiOutput("out.html", "Courier New");
        }
        return new ConsoleAsciiOutput();
    }
}
