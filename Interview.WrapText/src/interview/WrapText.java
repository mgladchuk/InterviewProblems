package interview;

/**
 * Implement a function that takes an arbitrary natural language text and wraps it, such that:
 * <p>
 * <ol>
 * <li>Every line is indented with four spaces</li>
 * <li>No line is longer than 80 characters in length</li>
 * <li>Words are not cut in the middle</li>
 * </ol>
 */

public final class WrapText {

    static final String FOUR_SPACES = "    ";
    private static final int MAX_LENGHT = 76;

    private WrapText() {

    }

    public static String wrap(final String text) {
        String[] words = text.split("\\s+");
        StringBuilder resultBuilder = new StringBuilder();
        int counter = MAX_LENGHT;
        for (String word : words) {
            if (word.length() <= counter) {
                counter = appendWordAndCountLength(resultBuilder, counter, word);
            } else {
                resultBuilder.append(FOUR_SPACES);
                resultBuilder.append(System.lineSeparator());
                counter = appendWordAndCountLength(resultBuilder, MAX_LENGHT, word);
            }
        }

        return resultBuilder.toString();
    }

    private static int appendWordAndCountLength(StringBuilder resultBuilder, int counter, String word) {
        int val = counter;
        resultBuilder.append(word);
        resultBuilder.append(" ");
        val -= word.length() + 1;
        return val;
    }

}
