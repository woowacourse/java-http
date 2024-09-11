package util;

import java.util.Collection;

public class StringUtil {
    public static final String BLANK = "";

    public static int findIndexStartIndexOne(final String str, final String value) {
        return str.indexOf(value, 1);
    }

    public static boolean filterBlank(final Collection<String> list) {
        return list.stream()
                .anyMatch(String::isBlank);
    }

    public static BiValue<String, String> splitBiValue(final String str, final String delimiter) {
        final int index = str.indexOf(delimiter);
        if (index == -1) {
            return new BiValue<>(str, BLANK);
        }
        return new BiValue<>(str.substring(0, index), str.substring(index+delimiter.length()));
    }

    public static String combineWithDelimiter(final BiValue<String, String> biValue, final String delimiter) {
        return biValue.first() + delimiter + biValue.second();
    }

    public static String blankIfNull(final String str) {
        return str == null ? BLANK : str;
    }

    public static String addSuffixIfNotEndSuffix(final String str, final String suffix) {
        if (str.endsWith(suffix)) {
            return str;
        }
        return str + suffix;
    }

    private StringUtil() {
    }
}
