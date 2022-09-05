package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentType {

    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    TEXT_JAVASCRIPT("js", "text/javascript")
    ;

    public static final int FILE_PATH_LENGTH = 2;
    private static final int EXTENSION_INDEX = 1;
    private static final String EXTENSION_SEPARATOR = "\\.";

    private final String extension;
    private final String value;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType findByExtension(final String url) {
        String[] filePath = url.split(EXTENSION_SEPARATOR);
        if (isDirectory(filePath)) {
            return TEXT_HTML;
        }
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.extension.equals(filePath[EXTENSION_INDEX]))
                .findFirst()
                .orElse(TEXT_HTML);
    }

    private static boolean isDirectory(final String[] split) {
        return split.length < FILE_PATH_LENGTH;
    }

    public String getValue() {
        return value;
    }
}
