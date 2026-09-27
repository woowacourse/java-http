package org.apache.coyote.http11.response;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", "html"),
    CSS("text/css;charset=utf-8", "css"),
    JAVASCRIPT("application/javascript;charset=utf-8", "js"),
    SVG("image/svg+xml;charset=utf-8", "svg");

    private static final String EXTENSION_SEPARATOR = ".";

    private final String value;
    private final String extension;

    ContentType(final String value, final String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType from(final String path) {
        final String extension = extractExtension(path);
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findFirst()
                .orElse(HTML);
    }

    private static String extractExtension(final String path) {
        final int idx = path.lastIndexOf(EXTENSION_SEPARATOR);
        if (idx == -1) {
            return "";
        }
        return path.substring(idx + 1);
    }

    public String getValue() {
        return value;
    }
}
