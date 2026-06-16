package ascii_art.img_to_char;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Matches sub-image brightness values to characters from a configurable charset.
 * Maintains normalized brightness mappings and supports absolute, round-up,
 * and round-down matching strategies.
 */
public class SubImgCharMatcher {

    private final Map<Character, Double> rawCharBrightness;
    private Map<Character, Double> normalizedCharBrightness;
    private final TreeSet<Character> charset;

    /**
     * Constructs a matcher with an initial character set.
     *
     * @param initialCharset characters used for brightness matching
     */
    public SubImgCharMatcher(char[] initialCharset) {
        this.rawCharBrightness = new HashMap<>();
        this.normalizedCharBrightness = new HashMap<>();
        this.charset = new TreeSet<>();
        for (char c : initialCharset) {
            addCharInternal(c, false);
        }
        normalizeCharBrightness();
    }

    /**
     * Returns an unmodifiable view of the active charset in ascending ASCII order.
     *
     * @return sorted set of active characters
     */
    public SortedSet<Character> getCharset() {
        return Collections.unmodifiableSortedSet(charset);
    }

    /**
     * Returns the number of characters currently in the charset.
     *
     * @return charset size
     */
    public int charsetSize() {
        return charset.size();
    }

    /**
     * Adds a character to the charset and re-normalizes brightness values.
     *
     * @param c character to add
     */
    public void addChar(char c) {
        if (!charset.contains(c)) {
            addCharInternal(c, true);
        }
    }

    /**
     * Removes a character from the charset and re-normalizes brightness values.
     *
     * @param c character to remove
     */
    public void removeChar(char c) {
        if (charset.contains(c)) {
            charset.remove(c);
            rawCharBrightness.remove(c);
            normalizeCharBrightness();
        }
    }

    /**
     * Finds the character whose normalized brightness is closest in absolute value.
     *
     * @param brightness normalized sub-image brightness in {@code [0.0, 1.0]}
     * @return best matching character, or {@code '?'} when the charset is empty
     */
    public char getCharByImageBrightness(double brightness) {
        char bestChar = '?';
        double bestDiff = Double.MAX_VALUE;
        for (Map.Entry<Character, Double> entry : normalizedCharBrightness.entrySet()) {
            double diff = Math.abs(entry.getValue() - brightness);
            if (diff < bestDiff || (diff == bestDiff && entry.getKey() < bestChar)) {
                bestDiff = diff;
                bestChar = entry.getKey();
            }
        }
        return bestChar;
    }

    /**
     * Finds the character with the smallest brightness greater than or equal to the target.
     *
     * @param brightness normalized sub-image brightness in {@code [0.0, 1.0]}
     * @return matching character, or {@code '?'} when none qualifies
     */
    public char getCharByImageBrightnessUp(double brightness) {
        char bestChar = '?';
        double bestCandidate = Double.MAX_VALUE;
        for (Map.Entry<Character, Double> entry : normalizedCharBrightness.entrySet()) {
            double value = entry.getValue();
            if (value >= brightness && value < bestCandidate) {
                bestCandidate = value;
                bestChar = entry.getKey();
            }
        }
        return bestChar;
    }

    /**
     * Finds the character with the largest brightness less than or equal to the target.
     *
     * @param brightness normalized sub-image brightness in {@code [0.0, 1.0]}
     * @return matching character, or {@code '?'} when none qualifies
     */
    public char getCharByImageBrightnessDown(double brightness) {
        char bestChar = '?';
        double bestCandidate = -1;
        for (Map.Entry<Character, Double> entry : normalizedCharBrightness.entrySet()) {
            double value = entry.getValue();
            if (value <= brightness && value > bestCandidate) {
                bestCandidate = value;
                bestChar = entry.getKey();
            }
        }
        return bestChar;
    }

    private void addCharInternal(char c, boolean normalizeAfter) {
        double brightness = computeCharBrightness(c);
        charset.add(c);
        rawCharBrightness.put(c, brightness);
        if (normalizeAfter) {
            normalizeCharBrightness();
        }
    }

    private void normalizeCharBrightness() {
        if (charset.isEmpty()) {
            normalizedCharBrightness = new HashMap<>();
            return;
        }
        double min = Collections.min(rawCharBrightness.values());
        double max = Collections.max(rawCharBrightness.values());

        normalizedCharBrightness = new HashMap<>();
        for (Map.Entry<Character, Double> entry : rawCharBrightness.entrySet()) {
            double normalized = (max == min) ? 0.5 : (entry.getValue() - min) / (max - min);
            normalizedCharBrightness.put(entry.getKey(), normalized);
        }
    }

    private double computeCharBrightness(char c) {
        boolean[][] pixels = CharConverter.convertToBoolArray(c);
        int total = 0;
        int white = 0;
        for (boolean[] row : pixels) {
            for (boolean pixel : row) {
                total++;
                if (pixel) {
                    white++;
                }
            }
        }
        return total == 0 ? 0.0 : (double) white / total;
    }
}
