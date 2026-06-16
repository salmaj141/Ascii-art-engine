package ascii_art.output;

/**
 * Strategy interface for rendering a two-dimensional ASCII character grid.
 */
public interface AsciiOutput {

    /**
     * Outputs the specified two-dimensional character array.
     *
     * @param chars ASCII art grid to render
     */
    void out(char[][] chars);
}
