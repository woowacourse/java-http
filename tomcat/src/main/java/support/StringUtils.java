package support;

import java.util.List;

public class StringUtils {
    private static final String EMPTY_STRING = "";

    public static boolean isEmpty(final String[] strings) {
        return null == strings || strings.length == 0;
    }

    public static boolean isEmpty(final List<String> strings) {
        return null == strings || strings.size() == 0;
    }

    public static boolean isEmpty(final String string) {
        return isNull(string) || isBlank(string);
    }

    public static boolean isNull(final String string) {
        return null == string;
    }

    public static boolean isBlank(final String string) {
        return EMPTY_STRING.equals(string.replaceAll("\\s+", ""));
    }
}
