package org.apache.coyote.http11.model;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    JS("js", "application/javascript;charset=utf-8"),
    SVG("svg", "image/svg+xml"),
    DEFAULT("", "text/html;charset=utf-8");

    private final String extension;
    private final String value;

    ContentType(final String extension, final String value) {
        this.extension = extension;
        this.value = value;
    }

    public static ContentType from(final String path) {
        final String extension = getExtension(path);
        return Arrays.stream(values())
                .filter(contentType -> contentType.extension.equals(extension))
                .findFirst()
                .orElse(DEFAULT);
    }

    private static String getExtension(final String path) {
        if (path == null || !path.contains(".")) {
            return "";
        }
        return path.substring(path.lastIndexOf(".") + 1);
    }

    public String getValue() {
        return value;
    }
}
