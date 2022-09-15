package org.richard.utils;

import java.util.Arrays;

public class StringUtils {

    public static boolean isNullOrBlank(final String string) {
        return string == null || string.isBlank();
    }

    public static boolean isNullOrBlank(final String... name) {
        return Arrays.stream(name)
                .anyMatch(StringUtils::isNullOrBlank);
    }

    private StringUtils() {
    }
}
