package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum FileExtension {

    HTML(".html"),
    CSS(".css"),
    JAVASCRIPT(".js"),
    ICON(".ico");

    private final String value;

    FileExtension(final String value) {
        this.value = value;
    }

    public static boolean isContains(final String fullPath) {
        long matchCount = Arrays.stream(values())
                .filter(extension -> fullPath.endsWith(extension.value))
                .count();

        return matchCount != 0;
    }

    public String getValue() {
        return value;
    }
}
