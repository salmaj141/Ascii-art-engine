package ascii_art.output;

/**
 * Console implementation of {@link AsciiOutput} that prints each row to standard output.
 */
public class ConsoleAsciiOutput implements AsciiOutput {

    @Override
    public void out(char[][] chars) {
        if (chars == null || chars.length == 0) {
            return;
        }
        for (char[] row : chars) {
            for (int x = 0; x < row.length; x++) {
                System.out.print(row[x] + " ");
            }
            System.out.println();
        }
    }
}
