package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentFormat {

    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    TEXT_JAVASCRIPT("js", "text/javascript")
    ;

    private static final int EXTENSION_INDEX = 1;
    private static final String EXTENSION_SEPARATOR = "\\.";

    private final String extension;
    private final String value;

    ContentFormat(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentFormat findByExtension(final String url) {
        String[] filePath = url.split(EXTENSION_SEPARATOR);
        if (isDirectory(filePath)) {
            return TEXT_HTML;
        }
        return Arrays.stream(ContentFormat.values())
                .filter(contentFormat -> contentFormat.extension.equals(filePath[EXTENSION_INDEX]))
                .findFirst()
                .orElse(TEXT_HTML);
    }

    private static boolean isDirectory(final String[] split) {
        return split.length < 2;
    }

    public String getValue() {
        return value;
    }
}
