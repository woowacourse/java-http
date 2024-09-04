package util;

public class StringUtil {
    public static final String BLANK = "";

    public static int findWithStartIndex1(final String str, final String value) {
        return str.indexOf(value, 1);
    }

    private StringUtil() {
    }
}
