package ascii_art.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * HTML implementation of {@link AsciiOutput} that writes a browser-viewable document.
 */
public class HtmlAsciiOutput implements AsciiOutput {

    private static final double BASE_LINE_SPACING = 0.8;
    private static final double BASE_FONT_SIZE = 150.0;

    private final String fontName;
    private final String filename;

    /**
     * Creates an HTML output strategy.
     *
     * @param filename destination file path
     * @param fontName CSS font family used for rendering
     */
    public HtmlAsciiOutput(String filename, String fontName) {
        this.fontName = fontName;
        this.filename = filename;
    }

    @Override
    public void out(char[][] chars) {
        if (chars == null || chars.length == 0 || chars[0].length == 0) {
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(String.format(
                    "<!DOCTYPE html>\n" +
                            "<html>\n" +
                            "<body style=\"" +
                            "\tCOLOR:#000000;" +
                            "\tTEXT-ALIGN:center;" +
                            "\tFONT-SIZE:1px;\">\n" +
                            "<p style=\"" +
                            "\twhite-space:pre;" +
                            "\tFONT-FAMILY:%s;" +
                            "\tFONT-SIZE:%frem;" +
                            "\tLETTER-SPACING:0.15em;" +
                            "\tLINE-HEIGHT:%fem;\">\n",
                    fontName, BASE_FONT_SIZE / chars[0].length, BASE_LINE_SPACING));

            for (char[] row : chars) {
                for (char character : row) {
                    writer.write(escapeHtml(character));
                }
                writer.newLine();
            }
            writer.write(
                    "</p>\n" +
                            "</body>\n" +
                            "</html>\n");
        } catch (IOException e) {
            Logger.getGlobal().severe(String.format("Failed to write to \"%s\"", filename));
        }
    }

    private static String escapeHtml(char character) {
        switch (character) {
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '&':
                return "&amp;";
            default:
                return String.valueOf(character);
        }
    }
}
