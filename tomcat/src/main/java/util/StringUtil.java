package util;

public class StringUtil {
    public static final String BLANK = "";

    public static int findWithStartIndex1(final String str, final String value) {
        return str.indexOf(value, 1);
    }


    public static BiValue<String, String> splitBiValue(final String str, final String delimiter) {
        final int index = str.indexOf(delimiter);
        if (index == -1) {
            return new BiValue<>(str, BLANK);
        }
        return new BiValue<>(str.substring(0, index), str.substring(index + 1));
    }

    public static String blankIfNull(final String str) {
        return str == null ? BLANK : str;
    }

    private StringUtil() {
    }
}
